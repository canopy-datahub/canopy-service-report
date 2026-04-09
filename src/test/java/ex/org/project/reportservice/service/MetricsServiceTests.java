package ex.org.project.reportservice.service;

import ex.org.project.reportservice.exceptions.BadDataException;
import ex.org.project.reportservice.exceptions.RequestParamException;
import ex.org.project.reportservice.mapper.*;
import ex.org.project.reportservice.model.*;
import ex.org.project.reportservice.model.dto.*;
import ex.org.project.reportservice.repositories.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import ex.org.project.reportservice.model.dto.CenterDto;
import ex.org.project.reportservice.model.dto.HubContentAggMetricsResponse;
import ex.org.project.reportservice.model.dto.StudyIdDto;
import ex.org.project.reportservice.model.dto.UserActivitiesDto;


@ExtendWith(SpringExtension.class)
class MetricsServiceTests {

    @Mock
    private HubContentMetricsRepository hubContentMetricsRepository;

    @Mock
    private HubContentMetricsStudyMapper hubContentMetricsStudyMapper;

    @Mock
    private GoogleAnalyticsService googleAnalyticsService;

    @Mock
    private SubmissionActivityRepository submissionActivityRepository;

    @Mock
    private MetricsReportRepository metricsReportRepository;

    @Mock
    private DatafileHarmonizationRepository datafileHarmonizationRepository;

    @Mock
    private StudyHarmonizationRepository studyHarmonizationRepository;

    private UserActivitiesMapper userActivitiesMapper = new UserActivitiesMapperImpl();

    @Mock
    WeeklyHubContentRepository weeklyHubContentRepository;

    @Spy
    @InjectMocks
    private MetricsService reportService;

    @Mock
    private AWSStorageService awsStorageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HubContentMetricsCenterMapper dccMapper = Mappers.getMapper(HubContentMetricsCenterMapper.class);
        SubmissionActivityMapper activityMapper = Mappers.getMapper(SubmissionActivityMapper.class);
        MetricsReportMapper metricsMapper = Mappers.getMapper(MetricsReportMapper.class);
        HarmonizationMetricsMapper harmonizationMapper = Mappers.getMapper(HarmonizationMetricsMapper.class);
        awsStorageService = mock(AWSStorageService.class);
        reportService = new MetricsService(hubContentMetricsRepository, metricsReportRepository,
                submissionActivityRepository, datafileHarmonizationRepository, studyHarmonizationRepository,
                weeklyHubContentRepository, googleAnalyticsService, userActivitiesMapper, dccMapper,
                hubContentMetricsStudyMapper, metricsMapper, activityMapper, harmonizationMapper,awsStorageService);
    }

    private UserActivitiesMetrics getMockUserActivitiesMetrics(String dimension, String startDate, String endDate) {
        Map<String, Map<String, Object>> mockMetrics = new HashMap<>();
        Map<String, Object> mockProperties = new HashMap<>();
        mockProperties.put("testProperty", "testValue");
        mockProperties.put(dimension, "dimensionValue");
        mockMetrics.put("TestDimension", mockProperties);
        UserActivitiesMetrics mockReport = new UserActivitiesMetrics();
        mockReport.setDimension(dimension);
        mockReport.setStartDate(startDate);
        mockReport.setEndDate(endDate);
        mockReport.setMetrics(mockMetrics);
        mockReport.setRowCount(1);
        return mockReport;
    }

    @Test
    void testCreateReportWithDccAggBy() {
        String aggBy = "center";
        Integer reportId = 1;
        List<HubContentMetrics> aggregateMetrics = new ArrayList<>();
        aggregateMetrics.add(getTestHubContentMetrics());

        when(hubContentMetricsRepository.findTotalFileSizeByCenterAndReportId(reportId))
                .thenReturn(aggregateMetrics);

        HubContentAggMetricsResponse result = reportService.createReport(aggBy, reportId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.aggDtos().size());
        CenterDto dto = (CenterDto) result.aggDtos().get(0);
        Assertions.assertEquals("Center", dto.getCenter());
        Assertions.assertEquals(3, dto.getTotalStudies());
        Assertions.assertEquals(13, dto.getStudiesWithData());
        Assertions.assertEquals(6.0, dto.getTotalFileSize());
        Assertions.assertEquals(4, dto.getTotalFileCount());
        Assertions.assertEquals(5, dto.getDataFileCount());
        Assertions.assertEquals(7, dto.getOrigRawFileCount());
        Assertions.assertEquals(8, dto.getTransformFilesCount());
        Assertions.assertEquals(9, dto.getMetadataFileCount());
        Assertions.assertEquals(10, dto.getDictionaryFileCount());
        Assertions.assertEquals(11, dto.getReadmeFileCount());
        Assertions.assertEquals(12, dto.getOtherFileCount());

    }

    @Test
    void testAggTotal() {
        // Create a list of HubContentMetrics for testing
        List<HubContentMetrics> aggregateMetrics = new ArrayList<>();
        aggregateMetrics.add(new HubContentMetrics("center", 1, 10, 100.0, 5, 2, 4, 1, 6, 7, 8, 9,6));
        aggregateMetrics.add(new HubContentMetrics("center", 2, 10, 100.0, 5, 2, 4, 1, 6, 7, 8, 9,6));


        // Test with totalFor = "DCC"
        CenterDto result1 = reportService.aggTotalCenter(aggregateMetrics);
        Assertions.assertEquals("Total", result1.getCenter());
        Assertions.assertEquals(20, result1.getTotalStudies());
        Assertions.assertEquals(200.0, result1.getTotalFileSize());
        Assertions.assertEquals(10, result1.getTotalFileCount());
        Assertions.assertEquals(4, result1.getDataFileCount());
        Assertions.assertEquals(16, result1.getReadmeFileCount());
        Assertions.assertEquals(8, result1.getOrigRawFileCount());
        Assertions.assertEquals(2, result1.getTransformFilesCount());
        Assertions.assertEquals(12, result1.getMetadataFileCount());
        Assertions.assertEquals(14, result1.getDictionaryFileCount());
        Assertions.assertEquals(18, result1.getOtherFileCount());
        Assertions.assertEquals(12, result1.getStudiesWithData());

        aggregateMetrics = new ArrayList<>();
        aggregateMetrics.add(new HubContentMetrics("study", 3, 10, 100.0, 5, 2, 4, 1, 6, 7, 8, 9,6));
        aggregateMetrics.add(new HubContentMetrics("study", 4, 10, 100.0, 5, 2, 4, 1, 6, 7, 8, 9,6));

        // Test with totalFor = "study"
        StudyIdDto result2 = reportService.aggTotalStudy(aggregateMetrics);
        Assertions.assertEquals("Total", result2.getStudyId());
        Assertions.assertEquals(200.0, result2.getTotalFileSize());
        Assertions.assertEquals(10, result2.getTotalFileCount());
        Assertions.assertEquals(4, result2.getDataFileCount());
        Assertions.assertEquals(16, result2.getReadmeFileCount());
        Assertions.assertEquals(8, result2.getOrigRawFileCount());
        Assertions.assertEquals(2, result2.getTransformFilesCount());
        Assertions.assertEquals(12, result2.getMetadataFileCount());
        Assertions.assertEquals(14, result2.getDictionaryFileCount());
        Assertions.assertEquals(18, result2.getOtherFileCount());
    }

    @Test
    void getUserActivitiesMetrics_HappyPath(){
        //setup mock data
        String dimension = "test";
        String startDate = "2023-01-01";
        String endDate = "2023-12-31";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //mock external calls
        when(googleAnalyticsService.getUserActivitiesReport(dimension, startDate, endDate))
                .thenReturn(mockReport);

        //run code to be tested
        UserActivitiesDto mockReportDto = reportService.getUserActivitiesMetrics(dimension, startDate, endDate);
        //test results
        assertEquals(dimension, mockReportDto.dimension());

        Assertions.assertEquals(mockReport.getHeaders(), mockReportDto.headers());
        Assertions.assertEquals(List.of("dimensionValue"), mockReportDto.metrics().stream().map(entry -> entry.get(dimension)).toList()
        );
    }

    @Test
    void getUserActivitiesMetrics_InvalidStartDate(){
        //setup mock data
        String dimension = "test";
        String startDate = "203-01-01"; //"203-01-01"
        String endDate = "2023-12-31";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //run code to be tested
        Assertions.assertThrows(RequestParamException.class,
                () -> reportService.getUserActivitiesMetrics(dimension, startDate, endDate)
        );
    }

    @Test
    void getUserActivitiesMetrics_InvalidEndDate(){
        //setup mock data
        String dimension = "test";
        String startDate = "2023-01-01";
        String endDate = "2023-47-31"; //"2023-47-31"
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //run code to be tested
        Assertions.assertThrows(RequestParamException.class,
                () -> reportService.getUserActivitiesMetrics(dimension, startDate, endDate)
        );
    }

    @Test
    void getUserActivitiesMetrics_HappyPath_KeywordDates(){
        //setup mock data
        String dimension = "test";
        String startDate = "7daysAgo";
        String endDate = "today";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //mock external calls
        when(googleAnalyticsService.getUserActivitiesReport(dimension, startDate, endDate))
                .thenReturn(mockReport);

        //run code to be tested
        UserActivitiesDto mockReportDto = reportService.getUserActivitiesMetrics(dimension, startDate, endDate);
        //test results
        assertEquals(dimension, mockReportDto.dimension());

        Assertions.assertEquals(mockReport.getHeaders(), mockReportDto.headers());
        Assertions.assertEquals(List.of("dimensionValue"),
                mockReportDto.metrics().stream().map(entry -> entry.get(dimension)).toList()
        );
    }

    @Test
    void getUserActivitiesMetrics_HappyPath_KeywordAndNumericalDates(){
        //setup mock data
        String dimension = "test";
        String startDate = "2023-01-01";
        String endDate = "today";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //mock external calls
        when(googleAnalyticsService.getUserActivitiesReport(dimension, startDate, endDate))
                .thenReturn(mockReport);

        //run code to be tested
        UserActivitiesDto mockReportDto = reportService.getUserActivitiesMetrics(dimension, startDate, endDate);
        //test results
        assertEquals(dimension, mockReportDto.dimension());

        Assertions.assertEquals(mockReport.getHeaders(), mockReportDto.headers());
        Assertions.assertEquals(List.of("dimensionValue"),
                mockReportDto.metrics().stream().map(entry -> entry.get(dimension)).toList()
        );
    }

    @Test
    void getUserActivitiesMetrics_StartDateAfterEndDate(){
        //setup mock data
        String dimension = "test";
        String startDate = "2023-05-07";
        String endDate = "2023-04-10";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //run code to be tested
        Assertions.assertThrows(RequestParamException.class,
                () -> reportService.getUserActivitiesMetrics(dimension, startDate, endDate)
        );
    }

    @Test
    void getUserActivitiesMetrics_HappyPath_SameStartAndEndDate(){
        //setup mock data
        String dimension = "test";
        String startDate = "2023-11-09";
        String endDate = "2023-11-09";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //mock external calls
        when(googleAnalyticsService.getUserActivitiesReport(dimension, startDate, endDate))
                .thenReturn(mockReport);

        //run code to be tested
        UserActivitiesDto mockReportDto = reportService.getUserActivitiesMetrics(dimension, startDate, endDate);
        //test results
        assertEquals(dimension, mockReportDto.dimension());

        Assertions.assertEquals(mockReport.getHeaders(), mockReportDto.headers());
        Assertions.assertEquals(List.of("dimensionValue"),
                mockReportDto.metrics().stream().map(entry -> entry.get(dimension)).toList()
        );
    }

    @Disabled("Logic comparing the ordering of keywords and dates not implemented")
    @Test
    void getUserActivitiesMetrics_KeywordStartDateAfterEndDate(){
        //setup mock data
        String dimension = "test";
        String startDate = "today";
        String endDate = "2020-03-15";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //run code to be tested
        Assertions.assertThrows(RequestParamException.class,
                () -> reportService.getUserActivitiesMetrics(dimension, startDate, endDate)
        );
    }

    @Disabled("Logic comparing the ordering of keywords and dates not implemented")
    @Test
    void getUserActivitiesMetrics_StartDateAfterKeywordEndDate(){
        //setup mock data
        String dimension = "test";
        String startDate = "2999-12-31";
        String endDate = "today";
        UserActivitiesMetrics mockReport = getMockUserActivitiesMetrics(dimension, startDate, endDate);

        //run code to be tested
        Assertions.assertThrows(RequestParamException.class,
                () -> reportService.getUserActivitiesMetrics(dimension, startDate, endDate)
        );
    }


    @Test
    void testGetUserActivitiesMetricsCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserActivitiesMetrics userActivitiesMetrics = new UserActivitiesMetrics();
        List<String> headers = Arrays.asList("Country", "Active Users", "Screen Page Views", "Sessions", "File Download");
        userActivitiesMetrics.setMetrics(getUserActivityMetricsMap());
        userActivitiesMetrics.setHeaders(headers);
        when(googleAnalyticsService.getUserActivitiesReport(anyString(), anyString(), anyString())).thenReturn(userActivitiesMetrics);
        reportService.getUserActivitiesMetricsCSV(response, "country", "2023-11-01", "2023-11-30");

        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=User_Activities_Metrics.csv", response.getHeaderValue("Content-Disposition"));

        StringBuilder responseString = new StringBuilder();
        for (String s : headers){
            responseString.append(",\"").append(s).append("\"");
        }
        responseString.deleteCharAt(0);
        responseString.append("\n\"Canada\",\"5\",\"6\",\"7\",\"8\"\n");
        responseString.append("\"United States\",\"1\",\"2\",\"3\",\"4\"");

        Assertions.assertEquals(responseString + "\n", response.getContentAsString());
    }

    private Map<String, Map<String, Object>> getUserActivityMetricsMap(){
        Map<String, Object> map1 = new HashMap<>();
        map1.put("Country", "United States");
        map1.put("Active Users", 1);
        map1.put("Screen Page Views", 2);
        map1.put("Sessions", 3);
        map1.put("File Download", 4);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("Country", "Canada");
        map2.put("Active Users", 5);
        map2.put("Screen Page Views", 6);
        map2.put("Sessions", 7);
        map2.put("File Download", 8);
        Map<String, Map<String, Object>> outerMap = new HashMap<>();
        outerMap.put("United States", map1);
        outerMap.put("Canada", map2);
        return outerMap;
    }


    @Test
    void testGetCSVReport() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<CenterDto> aggDtos = new ArrayList<>();
        aggDtos.add(getDccDto());
        String[] columnNames = {"DCC","TOTAL STUDIES",
                "STUDIES WITH DATA", "DATA SIZE", "ALL FILES", "DATA FILES",
                "ORIG FILES", "TRANSFORM FILES", "META FILES", "DICTIONARY FILES",
                "README FILES", "OTHER FILES"};
        String fileName = "hub_content_metrics.csv";
        reportService.getCSVReport(response, aggDtos, fileName, CenterDto.class, columnNames);

        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"hub_content_metrics.csv\"", response.getHeaderValue("Content-Disposition"));
        String responseString = "";
        for (String s : columnNames){
            responseString += "\"" + s + "\",";
        }
        //Removes the last comma
        responseString = responseString.substring(0, responseString.length() - 1);
        for (CenterDto dto : aggDtos){
            String dtoString = "\"" + dto.getCenter() + "\"," +
                    "\"" + dto.getTotalStudies() + "\"," +
                    "\"" + dto.getStudiesWithData() + "\"," +
                    "\"" + dto.getTotalFileSize() + "\"," +
                    "\"" + dto.getTotalFileCount() + "\"," +
                    "\"" + dto.getDataFileCount() + "\"," +
                    "\"" + dto.getOrigRawFileCount() + "\"," +
                    "\"" + dto.getTransformFilesCount() + "\"," +
                    "\"" + dto.getMetadataFileCount() + "\"," +
                    "\"" + dto.getDictionaryFileCount() + "\"," +
                    "\"" + dto.getReadmeFileCount() + "\"," +
                    "\"" + dto.getOtherFileCount() + "\"";


            responseString += "\n" + dtoString;
        }
        Assertions.assertEquals(responseString + "\n", response.getContentAsString());
    }


    private HubContentMetrics getTestHubContentMetrics(){
        HubContentMetrics cm = new HubContentMetrics();
        cm.setId(1L);
        cm.setReportId(2);
        cm.setCenter("DCC");
        cm.setCountStudy(3);
        cm.setStudyTitle("StudyTitle");
        cm.setStudyStatus("StudyStatus");
        cm.setTotalFileCount(4);
        cm.setDataFileCount(5);
        cm.setTotalFileSize(6.0);
        cm.setOrigDataFileCount(7);
        cm.setStandardizedDataFileCount(8);
        cm.setMetadataFileCount(9);
        cm.setDictionaryFileCount(10);
        cm.setReadmeFileCount(11);
        cm.setOtherFileCount(12);
        cm.setHasDataFile(true);
        cm.setCountStudyHasDataFile(13);
        return cm;
    }

    private CenterDto getDccDto(){
        return CenterDto.builder()
                .center("center")
                .studiesWithData(1)
                .totalStudies(2)
                .dataFileCount(3)
                .dictionaryFileCount(4)
                .metadataFileCount(5)
                .origRawFileCount(6)
                .otherFileCount(7)
                .readmeFileCount(8)
                .totalFileCount(9)
                .totalFileSize(10.0)
                .transformFilesCount(11)
                .build();
    }




    @Test
    void testSubmissionActivitiesMetrics(){
        when(submissionActivityRepository.findDccActivityMetrics(any(), any())).thenReturn(getDccActivityMetrics());
        SubmissionActivitiesMetricsResponse dccResponse = reportService.submissionActivitiesMetrics("center", "2021-12-01", "2021-12-31");
        SubmissionActivitiesMetricsDccDto dto1 = (SubmissionActivitiesMetricsDccDto) dccResponse.dtos().get(0);
        Assertions.assertEquals("DCCName", dccResponse.dtos().get(0).getCenter());
        Assertions.assertEquals(1, dto1.getStudiesInitiated());
        Assertions.assertEquals(2, dto1.getStudiesPublished());
        Assertions.assertEquals(3, dto1.getDataFilesSubmitted());
        Assertions.assertEquals(4, dto1.getDataFilesApproved());
        Assertions.assertEquals(5, dto1.getDataFilesRejected());

        when(submissionActivityRepository.findStudyActivityMetrics(any(), any())).thenReturn(getStudyActivityMetrics());
        SubmissionActivitiesMetricsResponse studyResponse = reportService.submissionActivitiesMetrics("study", "2021-12-01", "2021-12-31");
        SubmissionActivitiesMetricsStudyDto dto2 = (SubmissionActivitiesMetricsStudyDto) studyResponse.dtos().get(0);
        Assertions.assertEquals("TestStudyName", dto2.getStudyName());
        Assertions.assertEquals("StudyDccName", dto2.getCenter());
        Assertions.assertEquals(3, dto2.getDataFilesSubmitted());
        Assertions.assertEquals(4, dto2.getDataFilesApproved());
        Assertions.assertEquals(5, dto2.getDataFilesRejected());

        Assertions.assertThrows(BadDataException.class, () -> reportService.submissionActivitiesMetrics("aggBy", "2021-12-01", "2021-12-31"));
    }

    private List<Map<String, Object>> getDccActivityMetrics(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("center", "DCCName");
        metrics.put("studies_initiated", 1L);
        metrics.put("studies_published", 2L);
        metrics.put("data_files_submitted", 3L);
        metrics.put("data_files_approved", 4L);
        metrics.put("data_files_rejected", 5L);
        list.add(metrics);
        return list;
    }


    private List<Map<String, Object>> getStudyActivityMetrics(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("study_id", "1");
        metrics.put("study_name", "TestStudyName");
        metrics.put("center", "StudyDccName");
        metrics.put("data_files_submitted", 3L);
        metrics.put("data_files_approved", 4L);
        metrics.put("data_files_rejected", 5L);
        list.add(metrics);
        return list;
    }


    @Test
    void testHarmonizationReportIds(){
        when(metricsReportRepository.findByTypeName(anyString())).thenReturn(getMetricsReportList());
        List<ReportYearDTO> resultList = reportService.getDataHarmonizationReportIds();
        Assertions.assertFalse(resultList.isEmpty());
        ReportYearDTO dto = resultList.get(0);
        Assertions.assertEquals(2023, dto.getYear());
        List<ReportMonthDTO> months = dto.getMonths();
        Assertions.assertFalse(months.isEmpty());
        ReportMonthDTO mdto = months.get(0);
        Assertions.assertEquals(Month.DECEMBER, mdto.getMonth());
        Assertions.assertFalse(mdto.getReports().isEmpty());
        ReportDateDTO ddto = mdto.getReports().get(0);
        Assertions.assertEquals(1, ddto.getReportId());
        Assertions.assertEquals("2023-12-01", ddto.getReportDate());
    }

    private List<MetricsReport> getMetricsReportList(){

    	MetricsReportType type = new MetricsReportType(2, MetricsService.HARMONIZATION_METRICS_REPORT_TYPE);

        List<MetricsReport> list = new ArrayList<>();
        MetricsReport report = new MetricsReport();
        report.setId(1);
        report.setType(type);
        report.setReportDate(LocalDate.of(2023, 12, 1));
        list.add(report);
        return list;
    }

    private List<StudyByFileReport> getWeeklyMetricsReportList(){

        List<StudyByFileReport> list = new ArrayList<>();
        StudyByFileReport report = new StudyByFileReport();
        report.setCenter("Tech");
        report.setStudyId("phs001234");
        report.setStudyTitle("Test Study");
        report.setStudyStatus("approved");
        report.setStudyCreatedDate(LocalDateTime.of(2023,03,15,13,30,15));
        report.setFileName("demo_DATA_transformcopy.csv");
        report.setFileVersion(String.valueOf(1));
        report.setFileCategory("Tabular Data - Harmonized");
        report.setFileStatus("pending approval");
        report.setFileSize(12);
        report.setFileCreatedAt(LocalDateTime.of(2023,04,5,9,10,15));
        report.setSubmissionId(1);
        report.setSubmissionStatus("in_review");
        report.setSubmissionCreatedDate(LocalDateTime.of(2023,04,5,9,10,15));
        list.add(report);
        return list;
    }

    @Test
    void testGetHarmonizationMetrics(){
        List<StudyHarmonizationMetricsDashboard> studyList = new ArrayList<>();
        studyList.add(getStudyHarmonizationMetrics());
        when(studyHarmonizationRepository.findByReportId(anyInt())).thenReturn(studyList);
        HarmonizationMetricsResponse response1 = reportService.getHarmonizationMetrics("study", 1);
        List<String> columns = response1.columnNames();

        List<String> studyColumnNames = Arrays.asList("PHS", "Study Name",
                "DCC", "Files", "Variables", "Harmonizable Variables (Tier 1)", "Harmonized Variables (Tier 1)");
        Assertions.assertTrue(columns.containsAll(studyColumnNames));

        StudyHarmonizationMetricsDTO dto = (StudyHarmonizationMetricsDTO) response1.dtos().get(0);
        Assertions.assertEquals("1", dto.getStudyId());
        Assertions.assertEquals("TestStudyName", dto.getStudyName());
        Assertions.assertEquals("TestDCC", dto.getCenter());
        Assertions.assertEquals(2, dto.getNumberOfFiles());
        Assertions.assertEquals(3, dto.getUniqueVariables());
        Assertions.assertEquals(4, dto.getUniqueHarmonizableVariablesT1());

        List<String> datafileColumnNames = Arrays.asList("File Name (Orig)",
                "File Name (Trans)", "PHS", "Study Name", "DCC", "Variables (Orig)",
                "Variables (Trans)", "Harmonizable Variables (Tier 1)", "Harmonized Variables (Tier 1)");
        List<DatafileHarmonizationMetricsDashboard> datafileList = new ArrayList<>();
        datafileList.add(getDatafileHarmonizationMetrics());
        when(datafileHarmonizationRepository.findByReportId(anyInt())).thenReturn(datafileList);
        HarmonizationMetricsResponse response2 = reportService.getHarmonizationMetrics("dataset", 1);
        columns = response2.columnNames();
        Assertions.assertTrue(columns.containsAll(datafileColumnNames));

        DatafileHarmonizationMetricsDTO dto2 = (DatafileHarmonizationMetricsDTO) response2.dtos().get(0);
        Assertions.assertEquals("TestOrigFileName", dto2.getOriginalFileName());
        Assertions.assertEquals("TestTransFileName", dto2.getTransformFileName());
        Assertions.assertEquals("12", dto2.getStudyId());
        Assertions.assertEquals("TestDatafileName", dto2.getStudyName());
        Assertions.assertEquals("TestDCCdata", dto2.getCenter());
        Assertions.assertEquals(13, dto2.getOriginalUniqueVariables());
        Assertions.assertEquals(14, dto2.getTransformUniqueVariables());
        Assertions.assertEquals(15, dto2.getUniqueHarmonizableVariablesT1());
        Assertions.assertEquals(19, dto2.getUniqueHarmonizedVariablesT1());

    }

    private StudyHarmonizationMetricsDashboard getStudyHarmonizationMetrics(){
        StudyHarmonizationMetricsDashboard metrics = new StudyHarmonizationMetricsDashboard();
        metrics.setId(0);
        metrics.setStudyId("1");
        metrics.setStudyName("TestStudyName");
        metrics.setCenter("TestDCC");
        metrics.setOrigTransformPairsCount(2);
        metrics.setVariableCount(3);
        metrics.setHarmonizableTier1VariableCount(4);
        metrics.setHarmonizedTier1VariableCount(8);
        metrics.setVariables("Variables");
        metrics.setHarmonizableTier1Variables("Tier 1");
        metrics.setHarmonizedTier1Variables("Tier 1");
        return metrics;
    }

    private DatafileHarmonizationMetricsDashboard getDatafileHarmonizationMetrics(){
        DatafileHarmonizationMetricsDashboard metrics = new DatafileHarmonizationMetricsDashboard();
        metrics.setOriginalFileName("TestOrigFileName");
        metrics.setTransformFileName("TestTransFileName");
        metrics.setStudyId("12");
        metrics.setStudyName("TestDatafileName");
        metrics.setCenter("TestDCCdata");
        metrics.setOrigVariableCount(13);
        metrics.setTransformVariableCount(14);
        metrics.setHarmonizableTier1VariableCount(15);
        metrics.setHarmonizedTier1VariableCount(19);
        metrics.setOrigVariables("Orig Variables");
        metrics.setTransformVariables("Transform Variables");
        metrics.setHarmonizableTier1Variables("Tier 1");
        metrics.setHarmonizedTier1Variables("Tier 1");
        return metrics;
    }


    @Test
    void testHarmonizationMetricsAsCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<StudyHarmonizationMetricsDashboard> studyList = new ArrayList<>();
        studyList.add(getStudyHarmonizationMetrics());
        when(studyHarmonizationRepository.findByReportId(1)).thenReturn(studyList);
        reportService.getHarmonizationMetricsCSV("study", response, 1);

        Assertions.assertEquals("text/csv", response.getContentType());
        Assertions.assertEquals("attachment; filename=\"Study_Harmonization_Metrics.csv\"", response.getHeaderValue("Content-Disposition"));

        List<String> columnNames = Arrays.asList("PHS", "STUDY NAME",
                "DCC", "FILES", "VARIABLES COUNT", "HARMONIZABLE VARIABLES COUNT (TIER 1)", "HARMONIZED VARIABLES COUNT (TIER 1)",
                "HARMONIZABLE VARIABLES COUNT (TIER 2)", "HARMONIZED VARIABLES COUNT (TIER 2)",
                "TOTAL HARMONIZABLE", "TOTAL HARMONIZED", "VARIABLES", "HARMONIZABLE VARIABLES (TIER 1)",
                "HARMONIZED VARIABLES (TIER 1)", "HARMONIZABLE VARIABLES (TIER 2)", "HARMONIZED VARIABLES (TIER 2)");


        String responseString = "";
        for (String s : columnNames){
            responseString += "\"" + s + "\",";
        }
        //Removes the last comma
        responseString = responseString.substring(0, responseString.length() - 1);
        StudyHarmonizationMetricsDashboard metric = studyList.get(0);
        responseString += "\n\"" + metric.getStudyId() + "\",";
        responseString += "\"" + metric.getStudyName() + "\",";
        responseString += "\"" + metric.getCenter() + "\",";
        responseString += "\"" + metric.getOrigTransformPairsCount() + "\",";
        responseString += "\"" + metric.getVariableCount() + "\",";
        responseString += "\"" + metric.getHarmonizableTier1VariableCount() + "\",";
        responseString += "\"" + metric.getHarmonizedTier1VariableCount() + "\",";
        responseString += "\"" + metric.getVariables() + "\",";
        responseString += "\"" + metric.getHarmonizableTier1Variables() + "\",";
        responseString += "\"" + metric.getHarmonizedTier1Variables() + "\",";
        Assertions.assertEquals(responseString + "\n", response.getContentAsString());


        MockHttpServletResponse response1 = new MockHttpServletResponse();
        List<DatafileHarmonizationMetricsDashboard> dataList = new ArrayList<>();
        dataList.add(getDatafileHarmonizationMetrics());
        when(datafileHarmonizationRepository.findByReportId(1)).thenReturn(dataList);
        reportService.getHarmonizationMetricsCSV("dataset", response1, 1);

        Assertions.assertEquals("text/csv", response1.getContentType());
        Assertions.assertEquals("attachment; filename=\"Datafile_Harmonization_Metrics.csv\"", response1.getHeaderValue("Content-Disposition"));
        List<String> dataColumnNames = Arrays.asList("FILE NAME (ORIG)",
                "FILE NAME (TRANS)", "PHS", "STUDY NAME", "DCC", "VARIABLES COUNT (ORIG)",
                "VARIABLES COUNT (TRANS)", "HARMONIZABLE VARIABLES COUNT (TIER 1)", "HARMONIZED VARIABLES COUNT (TIER 1)",
                "ORIGINAL VARIABLES", "TRANSFORM VARIABLES", "HARMONIZABLE VARIABLES (TIER 1)",
                "HARMONIZED VARIABLES (TIER 1)");

        String responseString2 = "";
        for (String s : dataColumnNames){
            responseString2 += "\"" + s + "\",";
        }
        //Removes the last comma
        responseString2 = responseString2.substring(0, responseString2.length() - 1);
        DatafileHarmonizationMetricsDashboard metrics2 = dataList.get(0);
        responseString2 += "\n\"" + metrics2.getOriginalFileName() + "\",";
        responseString2 += "\"" + metrics2.getTransformFileName() + "\",";
        responseString2 += "\"" + metrics2.getStudyId() + "\",";
        responseString2 += "\"" + metrics2.getStudyName() + "\",";
        responseString2 += "\"" + metrics2.getCenter() + "\",";
        responseString2 += "\"" + metrics2.getOrigVariableCount() + "\",";
        responseString2 += "\"" + metrics2.getTransformVariableCount() + "\",";
        responseString2 += "\"" + metrics2.getHarmonizableTier1VariableCount() + "\",";
        responseString2 += "\"" + metrics2.getHarmonizedTier1VariableCount() + "\",";
        responseString2 += "\"" + metrics2.getOrigVariables() + "\",";
        responseString2 += "\"" + metrics2.getTransformVariables() + "\",";
        responseString2 += "\"" + metrics2.getHarmonizableTier1Variables() + "\",";
        responseString2 += "\"" + metrics2.getHarmonizedTier1Variables() + "\",";
        Assertions.assertEquals(responseString2 + "\n", response1.getContentAsString());

        Assertions.assertThrows(BadDataException.class, () -> reportService.getHarmonizationMetricsCSV("aggby", response, 1));
    }


    @Test
    void testSubmissionActivityMetricsCSV() throws UnsupportedEncodingException {
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        List<Map<String, Object>> dccList = getDccActivityMetrics();
        when(submissionActivityRepository.findDccActivityMetrics(any(), any())).thenReturn(dccList);
        reportService.submissionActivitiesMetricsCSV(response1, "dcc", "2021-04-01", "2023-01-01");
        Assertions.assertEquals("text/csv", response1.getContentType());
        Assertions.assertEquals("attachment; filename=\"Submission_Activities_Metrics.csv\"", response1.getHeaderValue("Content-Disposition"));
        List<String> dccColumnNames = Arrays.asList("DCC", "STUDIES INITIATED",
                "STUDIES PUBLISHED", "DATA FILES SUBMITTED", "DATA FILES APPROVED", "DATA FILES REJECTED");
        String responseString = "";
        for (String s : dccColumnNames){
            responseString += "\"" + s + "\",";
        }
        //Removes the last comma
        responseString = responseString.substring(0, responseString.length() - 1);
        Map<String, Object> dccMap = dccList.get(0);
        responseString += "\n\"" + dccMap.get("center") + "\",";
        responseString += "\"" + dccMap.get("studies_initiated") + "\",";
        responseString += "\"" + dccMap.get("studies_published") + "\",";
        responseString += "\"" + dccMap.get("data_files_submitted") + "\",";
        responseString += "\"" + dccMap.get("data_files_approved") + "\",";
        responseString += "\"" + dccMap.get("data_files_rejected") + "\"";
        Assertions.assertEquals(responseString + "\n", response1.getContentAsString());

        MockHttpServletResponse response2 = new MockHttpServletResponse();
        List<Map<String, Object>> studyList = getStudyActivityMetrics();
        when(submissionActivityRepository.findStudyActivityMetrics(any(), any())).thenReturn(studyList);
        reportService.submissionActivitiesMetricsCSV(response2, "study", "2021-04-01", "2023-01-01");
        List<String> studyColumnNames = Arrays.asList("PHS", "STUDY NAME",
                "DCC", "DATA FILES SUBMITTED", "DATA FILES APPROVED", "DATA FILES REJECTED");
        String responseString2 = "";
        for (String s : studyColumnNames){
            responseString2 += "\"" + s + "\",";
        }
        responseString2 = responseString2.substring(0, responseString2.length() - 1);
        Map<String, Object> studyMap = studyList.get(0);
        responseString2 += "\n\"" + studyMap.get("study_id") + "\",";
        responseString2 += "\"" + studyMap.get("study_name") + "\",";
        responseString2 += "\"" + studyMap.get("center") + "\",";
        responseString2 += "\"" + studyMap.get("data_files_submitted") + "\",";
        responseString2 += "\"" + studyMap.get("data_files_approved") + "\",";
        responseString2 += "\"" + studyMap.get("data_files_rejected") + "\"";
        Assertions.assertEquals(responseString2 + "\n", response2.getContentAsString());

        Assertions.assertThrows(BadDataException.class, () -> reportService.submissionActivitiesMetricsCSV(response1, "aggBy", "2021-12-01", "2021-12-31"));
    }
    @Test
    void testStudyByFileCSV() throws UnsupportedEncodingException {
        List<StudyByFileReport> studyList = getWeeklyMetricsReportList();
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        when(weeklyHubContentRepository.findAllByOrderByCenterAsc()).thenReturn(studyList);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = currentDateTime + "-DataHub-Weekly-Metrics.csv";
        reportService.generateStudyByFileCSVReport(response1);
        Assertions.assertEquals("text/csv", response1.getContentType());
        Assertions.assertEquals("attachment; filename=\"" + fileName + "\"", response1.getHeaderValue("Content-Disposition"));
        List<String> weeklyContentColumnNames = Arrays.asList("STUDY PROGRAM","STUDY PHS","STUDY TITLE","STUDY STATUS","STUDY CREATE DATE",
                "SUBMISSION ID","SUBMISSION CREATE DATE","SUBMISSION STATUS",
                "FILE NAME","FILE VERSION","FILE CATEGORY","FILE CREATE DATE","FILE STATUS","FILE SIZE"
        );
        String responseString = "";
        for (String s : weeklyContentColumnNames){
            responseString += "\"" + s + "\",";
        }
        //Removes the last comma
        responseString = responseString.substring(0, responseString.length() - 1);
        StudyByFileReport reportMap = studyList.get(0);
        responseString += "\n\"" + reportMap.getCenter() + "\",";
        responseString += "\"" + reportMap.getStudyId() + "\",";
        responseString += "\"" + reportMap.getStudyTitle() + "\",";
        responseString += "\"" + reportMap.getStudyStatus() + "\",";
        responseString += "\"" + reportMap.getStudyCreatedDate() + "\",";
        responseString += "\"" + reportMap.getSubmissionId()+ "\",";
        responseString += "\"" + reportMap.getSubmissionCreatedDate()+ "\",";
        responseString += "\"" + reportMap.getSubmissionStatus()+ "\",";
        responseString += "\"" + reportMap.getFileName() + "\",";
        responseString += "\"" + reportMap.getFileVersion() + "\",";
        responseString += "\"" + reportMap.getFileCategory()+ "\",";
        responseString += "\"" + reportMap.getFileCreatedAt()+ "\",";
        responseString += "\"" + reportMap.getFileStatus()+ "\",";
        responseString += "\"" + reportMap.getFileSize()+ "\"";
        Assertions.assertEquals(responseString + "\n", response1.getContentAsString());
    }
}

