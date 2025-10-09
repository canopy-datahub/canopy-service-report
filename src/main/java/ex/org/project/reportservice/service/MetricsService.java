package ex.org.project.reportservice.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ex.org.project.reportservice.exceptions.BadDataException;
import ex.org.project.reportservice.exceptions.DownloadServiceReadWriteError;
import ex.org.project.reportservice.exceptions.RequestParamException;
import ex.org.project.reportservice.mapper.*;
import ex.org.project.reportservice.model.*;
import ex.org.project.reportservice.model.dto.*;
import ex.org.project.reportservice.repositories.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.comparators.FixedOrderComparator;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ex.org.project.reportservice.util.MetricsColumns.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetricsService {

    // "Agg By" options
    public static final String CENTER = "center";
    public static final String STUDY = "study";
    public static final String DATASET = "dataset";

    public static final String HUBCONTENT_METRICS_REPORT_TYPE = "hub_content";
    public static final String HARMONIZATION_METRICS_REPORT_TYPE = "harmonization";

    private final HubContentMetricsRepository hubContentMetricsRepository;
    private final MetricsReportRepository metricsReportRepository;
    private final SubmissionActivityRepository submissionActivityRepository;
    private final DatafileHarmonizationRepository datafileHarmonizationRepository;
    private final StudyHarmonizationRepository studyHarmonizationRepository;
    private final WeeklyHubContentRepository weeklyHubContentRepository;
    private final GoogleAnalyticsService googleAnalyticsService;
    private final UserActivitiesMapper userActivitiesMapper;
    private final HubContentMetricsCenterMapper hubContentMetricsCenterMapper;
    private final HubContentMetricsStudyMapper hubContentMetricsStudyMapper;
    private final MetricsReportMapper metricsReportMapper;
    private final SubmissionActivityMapper submissionActivityMapper;
    private final HarmonizationMetricsMapper harmonizationMetricsMapper;
    private final AWSStorageService awsStorageService;

    private static final String ACTIVE_USERS = "Active Users";



    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Pattern datePattern = Pattern.compile("^today$|^yesterday$|^\\d+daysAgo$");
    private static final LocalDate minValidDate = LocalDate.of(2015, 8, 13);
    private static final LocalDate maxValidDate = LocalDate.of(3000, 1, 1);

    /**
     * Creates a report based on the given reportName and reportId.
     *
     * @param aggBy    The 'aggregate by' option of the report (aggBy or study).
     * @param reportId The ID of the report.
     * @return A HubContentAggMetricsDto object containing the aggregated metrics
     * for the report.
     *
     * @throws BadDataException If an unsupported reportName is provided.
     */
    public HubContentAggMetricsResponse createReport(String aggBy, Integer reportId) {

        if(aggBy.equalsIgnoreCase(CENTER)) {
            List<CenterDto> aggDtos = getHubMetricsForCenter(reportId);
            // Build and return the HubContentAggMetricsDto object for aggBy report
            return HubContentAggMetricsResponse.builder().columnNames(CENTER_COLUMN_NAMES).aggDtos(aggDtos).build();
        }
        else if(aggBy.equalsIgnoreCase(STUDY)) {
            List<StudyPhsDto> aggDtos = getHubMetricsForStudy(reportId);
            // Build and return the HubContentAggMetricsDto object for study report
            return HubContentAggMetricsResponse.builder().columnNames(STUDY_COLUMN_NAMES).aggDtos(aggDtos).build();
        }
        else {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }
    }

    /**
     * Helper method for calling the DB and mapping the DTOs for the hub metrics report when aggBy is DCC.
     */
    public List<CenterDto> getHubMetricsForCenter(Integer reportId) {
        // Retrieve aggregate metrics for aggBy report from the repository
        List<HubContentMetrics> aggregateMetrics = hubContentMetricsRepository.findTotalFileSizeByCenterAndReportId(
                reportId);
        // Map the aggregate metrics to CenterDto objects
        List<CenterDto> aggDtos = hubContentMetricsCenterMapper.toDto(aggregateMetrics);
        // Add the total metrics for aggBy report
        aggDtos.add(aggTotalCenter(aggregateMetrics));
        return aggDtos;
    }

    /**
     * Helper method for calling the DB and mapping the DTOs for the hub metrics report and aggBy is Study
     */
    public List<StudyPhsDto> getHubMetricsForStudy(Integer reportId) {
        List<HubContentMetrics> aggregateMetrics = hubContentMetricsRepository.findByStudyStatusAndHasDataFileAndReportId(
                "Approved", true, reportId);
        List<StudyPhsDto> aggDtos = hubContentMetricsStudyMapper.toDto(aggregateMetrics);
        aggDtos.add(aggTotalStudy(aggregateMetrics));
        return aggDtos;
    }


    /**
     * Calculates the total metrics for all DCCs.
     *
     * @param aggregateMetrics The list of HubContentMetrics representing the aggregate metrics.
     * @return A DccDto object with the total metrics for all DCCs.
     */
    public static CenterDto aggTotalCenter(List<HubContentMetrics> aggregateMetrics) {
        return CenterDto.builder()
                .center("Total")
                .totalStudies(aggregateMetrics.stream().mapToInt(HubContentMetrics::getCountStudyPhs).sum())
                .totalFileSize(aggregateMetrics.stream()
                                       .map(HubContentMetrics::getTotalFileSize)
                                       .mapToDouble(MetricsService::normalizeNullDouble)
                                       .sum())
                .totalFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getTotalFileCount).sum())
                .dataFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getDataFileCount).sum())
                .readmeFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getReadmeFileCount).sum())
                .origRawFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getOrigDataFileCount).sum())
                .transformFilesCount(
                        aggregateMetrics.stream().mapToInt(HubContentMetrics::getStandardizedDataFileCount).sum())
                .metadataFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getMetadataFileCount).sum())
                .dictionaryFileCount(
                        aggregateMetrics.stream().mapToInt(HubContentMetrics::getDictionaryFileCount).sum())
                .otherFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getOtherFileCount).sum())
                .studiesWithData(aggregateMetrics.stream().mapToInt(HubContentMetrics::getCountStudyHasDataFile).sum())
                .build();
    }

    private static Double normalizeNullDouble(Double num) {
        if(num == null) {
            return 0.;
        }
        else {
            return num;
        }
    }

    /**
     * Calculates the total metrics for all Study.
     *
     * @param aggregateMetrics The list of HubContentMetrics representing the aggregate metrics.
     * @return A DccDto object with the total metrics for all Studys.
     */
    public static StudyPhsDto aggTotalStudy(List<HubContentMetrics> aggregateMetrics) {
        return StudyPhsDto.builder()
                .studyPhs("Total")
                .totalFileSize(aggregateMetrics.stream().mapToDouble(HubContentMetrics::getTotalFileSize).sum())
                .totalFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getTotalFileCount).sum())
                .dataFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getDataFileCount).sum())
                .readmeFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getReadmeFileCount).sum())
                .origRawFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getOrigDataFileCount).sum())
                .transformFilesCount(
                        aggregateMetrics.stream().mapToInt(HubContentMetrics::getStandardizedDataFileCount).sum())
                .metadataFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getMetadataFileCount).sum())
                .dictionaryFileCount(
                        aggregateMetrics.stream().mapToInt(HubContentMetrics::getDictionaryFileCount).sum())
                .otherFileCount(aggregateMetrics.stream().mapToInt(HubContentMetrics::getOtherFileCount).sum())
                .build();
    }

    /**
     * @param dimension The Google Analytics dimension on which to aggregate metrics
     * @param startDate The start date to filter by
     * @param endDate   The end date to filter by
     * @return UserActivitiesDto
     *
     * @throws RequestParamException if date parameters are invalid
     */
    public UserActivitiesDto getUserActivitiesMetrics(String dimension, String startDate, String endDate) {
        if(!(checkDateFormat(startDate, endDate))) {
            throw new RequestParamException("Invalid date parameter(s)");
        }
        UserActivitiesMetrics userActivitiesMetrics = googleAnalyticsService.getUserActivitiesReport(dimension,
                                                                                                     startDate,
                                                                                                     endDate);
        return userActivitiesMapper.userActivitiesMetricsToDto(userActivitiesMetrics, ACTIVE_USERS);
    }

    /**
     * Retrieves and returns the User Activities Metrics similar to the getUserActivitiesMetrics method above.
     * Uses a different mapper method to convert the metrics from the entity map into a list of DTOs which can be
     * converted into a CSV.
     */
    public void getUserActivitiesMetricsCSV(HttpServletResponse response, String dimension, String startDate,
                                            String endDate) {
        UserActivitiesDto dto = getUserActivitiesMetrics(dimension, startDate, endDate);

        //Create the CSV writer and write the column headers for the file
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        csvWriter.writeNext(dto.headers().toArray(new String[0]));

        //Extract the data from the metrics list and write it to the CSV string
        for(Map<String, Object> map : dto.metrics()) {
            String[] rowValues = new String[dto.headers().size()];
            for(int i = 0; i < dto.headers().size(); i++) {
                Object o = map.get(dto.headers().get(i));
                String s = String.valueOf(o);
                rowValues[i] = s.equals("null") ? "" : s;
            }
            csvWriter.writeNext(rowValues);
        }

        //Build the final response object
        String csvData = stringWriter.toString();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=User_Activities_Metrics.csv");
        try {
            response.getWriter().write(csvData);
        }
        catch(IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Utility method to check if a start date and end date are a valid Google
     * Analytics date range
     *
     * @param startDateString String containing date in format 'yyyy-mm-dd',
     *                        'yyyy-m-d', 'today', 'yesterday', 'NdaysAgo'
     * @param endDateString   String containing date in format 'yyyy-mm-dd',
     *                        'yyyy-m-d', 'today', 'yesterday', 'NdaysAgo' (N is any
     *                        positive integer as long as the date it's referencing
     *                        isn't before 2015-08-13)
     * @return boolean
     */
    private boolean checkDateFormat(String startDateString, String endDateString) {
        boolean startValid = false;
        LocalDate startDate;
        LocalDate endDate;
        try {
            // checking if both are proper dates
            startDate = LocalDate.parse(startDateString, dateFormatter);
            startValid = startDate.isAfter(minValidDate);
            endDate = LocalDate.parse(endDateString, dateFormatter);
            return (startValid && endDate.isBefore(maxValidDate) && (startDate.isBefore(endDate) || startDate.isEqual(
                    endDate)));
        }
        catch(DateTimeParseException e) {
            // if startDate is a string
            if(!startValid) {
                Matcher startMatcher = datePattern.matcher(startDateString);
                startValid = startMatcher.matches();
            }
        }
        // checking endDate in case startDate was a string or endDate is a string
        try {
            endDate = LocalDate.parse(endDateString, dateFormatter);
            return (startValid && endDate.isBefore(maxValidDate));
        }
        catch(DateTimeParseException e) {
            Matcher endMatcher = datePattern.matcher(endDateString);
            // edge case startDate.isBefore(endDate) not caught if one or both are words
            return (startValid && endMatcher.matches());
        }
    }


    /**
     * Return all available week options for Hub Content Report grouped by year and
     * then month
     *
     * @return List<ReportYearDTO>
     */
    public List<ReportYearDTO> getHubContentWeeklyReportOptions() {

        List<Integer> reportIds = hubContentMetricsRepository.findDistinctReportId();
        List<ReportYearDTO> reportOptions = new ArrayList<>();

        Map<Integer, List<MetricsReport>> reportsByYear = metricsReportRepository.findByIdInAndTypeName(reportIds,
                                                                                                        HUBCONTENT_METRICS_REPORT_TYPE)
                .stream()
                .collect(Collectors.groupingBy(r -> r.getReportDate().getYear()));

        reportsByYear.forEach((year, reportsForYear) -> {
            List<ReportMonthDTO> reportMonthDTOs = new ArrayList<>();
            Map<Month, List<MetricsReport>> reportsByMonth = reportsForYear.stream()
                    .collect(Collectors.groupingBy(r -> r.getReportDate().getMonth()));

            reportsByMonth.forEach((month, reportsForMonth) -> {
                List<ReportDateDTO> reportDateDTOs = metricsReportMapper.toReportDateDtos(reportsForMonth);
                reportMonthDTOs.add(new ReportMonthDTO(month, reportDateDTOs));
            });

            reportMonthDTOs.sort(Comparator.comparing(ReportMonthDTO::getMonth));
            reportOptions.add(new ReportYearDTO(year, reportMonthDTOs));
        });

        return reportOptions;
    }

    /**
     * Method to generate the Hub Content Metrics and return the data as a CSV file.
     */
    public void getHubContentReport(HttpServletResponse response, String aggBy, Integer reportId) {
        String fileName = "Hub_Content_Metrics.csv";
        if(aggBy.equalsIgnoreCase(CENTER)) {
            List<CenterDto> aggDtos = getHubMetricsForCenter(reportId);
            String[] columnNames = CENTER_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new);
            // Build and return the HubContentAggMetricsDto object for aggBy report
            getCSVReport(response, aggDtos, fileName, CenterDto.class, columnNames);
        }
        else if(aggBy.equalsIgnoreCase(STUDY)) {
            List<StudyPhsDto> aggDtos = getHubMetricsForStudy(reportId);
            String[] columnNames = STUDY_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new);
            // Build and return the HubContentAggMetricsDto object for study report
            getCSVReport(response, aggDtos, fileName, StudyPhsDto.class, columnNames);
        }
        else {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }
    }

    /**
     * This method writes a CSV file into the provided HttpServletResponse object.
     * To do this, it uses OpenCSV StatefulBeanToCsv. It will convert the list of
     * DTOs into the CSV data, csv annotations within the DTO class as headers
     * for each column.
     *
     * @param response    HttpServletResponse this is how the generated CSV will be
     *                    returned to the controller
     * @param dtoList     List of DTOs to be converted into the CSV
     * @param fileName    The name the file should be when downloaded, including the extension.
     * @param dtoClass    The DTO class provided in the dtoList
     * @param columnNames An array of column names, Strings, in the order in which
     *                    they should appear in the CSV, capitalized. Failing to
     *                    capitalize will likely result in an exception.
     * @param <DTO>       The type of DTO used for the list
     */
    public static <DTO> void getCSVReport(HttpServletResponse response, List<DTO> dtoList, String fileName,
                                          Class<DTO> dtoClass, String[] columnNames) {
        try {
            response.setContentType("text/csv");
            String headerKey = HttpHeaders.CONTENT_DISPOSITION;
            String headerValue = "attachment; filename=\"" + fileName + "\"";
            response.setHeader(headerKey, headerValue);

            // Create the mapping strategy
            FixedOrderComparator comparator = new FixedOrderComparator(columnNames);
            HeaderColumnNameMappingStrategy<DTO> strategy = new HeaderColumnNameMappingStrategyBuilder<DTO>().build();
            strategy.setType(dtoClass);
            strategy.setColumnOrderOnWrite(comparator);

            // Create a CSV writer
            StatefulBeanToCsv<DTO> writer = new StatefulBeanToCsvBuilder<DTO>(response.getWriter()).withMappingStrategy(
                    strategy).withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(true).build();

            // Write all hub content metrics to the CSV file
            writer.write(dtoList);

        }
        catch(Exception e) {
            log.error("Exception occurred", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * Method to derive and return the Submission Activities Metrics as json data.
     */
    public SubmissionActivitiesMetricsResponse submissionActivitiesMetrics(String aggBy, String startDate,
                                                                           String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        if(aggBy.equals(CENTER)) {
            return SubmissionActivitiesMetricsResponse.builder()
                    .columnNames(SUBMISSION_DCC_COLUMN_NAMES)
                    .dtos(getDccSubmissionActivitiesMetricsDtos(startDateTime, endDateTime))
                    .build();
        }
        else if(aggBy.equals(STUDY)) {
            return SubmissionActivitiesMetricsResponse.builder()
                    .columnNames(SUBMISSION_STUDY_COLUMN_NAMES)
                    .dtos(getStudySubmissionActivitiesMetricsDtos(startDateTime, endDateTime))
                    .build();
        }
        else {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }
    }

    /**
     * Method to derive and return the Submission Activities Metrics as a CSV file
     */
    public void submissionActivitiesMetricsCSV(HttpServletResponse response, String aggBy, String startDate,
                                               String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
        if(aggBy.equals(CENTER)) {
            List<SubmissionActivitiesMetricsDccDto> dtos = getDccSubmissionActivitiesMetricsDtos(startDateTime,
                                                                                                 endDateTime);
            getCSVReport(response, dtos, "Submission_Activities_Metrics.csv", SubmissionActivitiesMetricsDccDto.class,
                         SUBMISSION_DCC_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new));
        }
        else if(aggBy.equals(STUDY)) {
            List<SubmissionActivitiesMetricsStudyDto> dtos = getStudySubmissionActivitiesMetricsDtos(startDateTime,
                                                                                                     endDateTime);
            getCSVReport(response, dtos, "Submission_Activities_Metrics.csv", SubmissionActivitiesMetricsStudyDto.class,
                         SUBMISSION_STUDY_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new));
        }
        else {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }
    }

    /**
     * Helper method for deriving the Submission Activities Metrics. Calls the DB and performs the mapping into DTO objects.
     */
    public List<SubmissionActivitiesMetricsDccDto> getDccSubmissionActivitiesMetricsDtos(LocalDateTime startDate,
                                                                                         LocalDateTime endDate) {
        List<Map<String, Object>> metrics = submissionActivityRepository.findDccActivityMetrics(startDate, endDate);
        return submissionActivityMapper.mapToDccDtoList(metrics);
    }

    /**
     * Helper method for deriving the Submission Activities Metrics. Calls the DB and performs the mapping into DTO objects.
     */
    public List<SubmissionActivitiesMetricsStudyDto> getStudySubmissionActivitiesMetricsDtos(LocalDateTime startDate,
                                                                                             LocalDateTime endDate) {
        List<Map<String, Object>> metrics = submissionActivityRepository.findStudyActivityMetrics(startDate, endDate);
        return submissionActivityMapper.mapToStudyDtoList(metrics);
    }

    /**
     * Method to derive the data Harmonization Report Ids
     */
    public List<ReportYearDTO> getDataHarmonizationReportIds() {
        List<ReportYearDTO> reportOptions = new ArrayList<>();

        Map<Integer, List<MetricsReport>> reportsByYear = metricsReportRepository.findByTypeName(
                        HARMONIZATION_METRICS_REPORT_TYPE)
                .stream()
                .collect(Collectors.groupingBy(r -> r.getReportDate().getYear()));

        reportsByYear.forEach((year, reportsForYear) -> {
            List<ReportMonthDTO> reportMonthDTOs = new ArrayList<>();
            Map<Month, List<MetricsReport>> reportsByMonth = reportsForYear.stream()
                    .collect(Collectors.groupingBy(r -> r.getReportDate().getMonth()));

            reportsByMonth.forEach((month, reportsForMonth) -> {
                List<ReportDateDTO> reportDateDTOs = metricsReportMapper.toReportDateDtos(reportsForMonth);
                reportMonthDTOs.add(new ReportMonthDTO(month, reportDateDTOs));
            });

            reportMonthDTOs.sort(Comparator.comparing(ReportMonthDTO::getMonth));
            reportOptions.add(new ReportYearDTO(year, reportMonthDTOs));
        });

        return reportOptions;
    }

    /**
     * Method to derive the Harmonization Metrics and return the data as json.
     */
    public HarmonizationMetricsResponse getHarmonizationMetrics(String aggBy, int reportId) {
        if(aggBy.equals(STUDY)) {
            List<StudyHarmonizationMetricsDashboard> metrics = studyHarmonizationRepository.findByReportId(reportId);
            return HarmonizationMetricsResponse.builder()
                    .dtos(harmonizationMetricsMapper.studyListToDtoList(metrics))
                    .columnNames(STUDY_HARMONIZATION_COLUMN_NAMES)
                    .build();
        }
        else if(aggBy.equals(DATASET)) {
            List<DatafileHarmonizationMetricsDashboard> metrics = datafileHarmonizationRepository.findByReportId(
                    reportId);
            return HarmonizationMetricsResponse.builder()
                    .dtos(harmonizationMetricsMapper.datafileListToDtoList(metrics))
                    .columnNames(DATASET_HARMONIZATION_COLUMN_NAMES)
                    .build();
        }
        else {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }
    }

    /**
     * Method to derive the Harmonization Metrics and return the data as a CSV file.
     */
    public void getHarmonizationMetricsCSV(String aggBy, HttpServletResponse response, int reportId) {

        if(aggBy.equals(STUDY)) {
            List<StudyHarmonizationMetricsDashboard> metrics = studyHarmonizationRepository.findByReportId(reportId);
            List<StudyHarmonizationMetricsDTO> dtos = harmonizationMetricsMapper.studyListToDtoList(metrics);
            String fileName = "Study_Harmonization_Metrics.csv";
            getCSVReport(response, dtos, fileName, StudyHarmonizationMetricsDTO.class,
                         STUDY_HARMONIZATION_CSV_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new));
        }
        else if(aggBy.equals(DATASET)) {
            List<DatafileHarmonizationMetricsDashboard> metrics = datafileHarmonizationRepository.findByReportId(
                    reportId);
            List<DatafileHarmonizationMetricsDTO> dtos = harmonizationMetricsMapper.datafileListToDtoList(metrics);
            String fileName = "Datafile_Harmonization_Metrics.csv";
            getCSVReport(response, dtos, fileName, DatafileHarmonizationMetricsDTO.class,
                         DATASET_HARMONIZATION_CSV_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new));
        }
        else {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }
    }

    /**
     * Generate Study by File Report for the most recent current snapshot.
     * TODO: Used for ADHOC requests of StudyByFileCSVReport, will be deleted once report is uploaded on schedule and accessible via api
     */
    public void generateStudyByFileCSVReport(HttpServletResponse response) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new Date());
        List<StudyByFileReport> studyByFileReportContent = weeklyHubContentRepository.findAllByOrderByCenterAsc();
        String fileName = currentDateTime + "-DataHub-Weekly-Metrics.csv";
        String[] columnNames = WEEKLY_METRICS_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new);

        try {
            getCSVReport(response, studyByFileReportContent, fileName, StudyByFileReport.class, columnNames);
        }
        catch(Exception e) {
            throw new BadDataException("Unable to generate study by file report: " + e);
        }

    }

    /**
     * Uploads WeeklyFileReport to s3 every Monday at 12pm ET
     */
    public void uploadWeeklyReportToS3(){
        //get current hub content
        List<StudyByFileReport> studyByFileReportContent = weeklyHubContentRepository.findAllByOrderByCenterAsc();

        String[] columnNames = WEEKLY_METRICS_COLUMN_NAMES.stream().map(String::toUpperCase).toArray(String[]::new);
        StringWriter writer = new StringWriter();
        // Create Mapping Strategy to arrange the column name in order
        FixedOrderComparator comparator = new FixedOrderComparator(columnNames);
        HeaderColumnNameMappingStrategy<StudyByFileReport> mappingStrategy = new HeaderColumnNameMappingStrategyBuilder<StudyByFileReport>().build();
        mappingStrategy.setType(StudyByFileReport.class);
        mappingStrategy.setColumnOrderOnWrite(comparator);

        // Creating StatefulBeanToCsv object
        StatefulBeanToCsvBuilder<StudyByFileReport> builder = new StatefulBeanToCsvBuilder(writer);
        StatefulBeanToCsv beanWriter = builder
                .withMappingStrategy(mappingStrategy)
                .withOrderedResults(true)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();

        try {
            //// Write weekly metrics list to StatefulBeanToCsv object
            beanWriter.write(studyByFileReportContent);

        } catch (CsvDataTypeMismatchException e) {
            throw new DownloadServiceReadWriteError("Unable to complete csv data conversion: "+ e.getMessage());
        } catch (CsvRequiredFieldEmptyException e) {
            throw new DownloadServiceReadWriteError("Missing mandatory csv field: "+ e.getMessage());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
        //upload csv report
        awsStorageService.uploadWeeklyFileReport(inputStream);
        // closing the writer object
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
