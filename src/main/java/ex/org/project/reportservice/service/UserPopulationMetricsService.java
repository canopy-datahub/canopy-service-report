package ex.org.project.reportservice.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import ex.org.project.reportservice.exceptions.BadDataException;
import ex.org.project.reportservice.model.dto.UserMetricsDto;
import ex.org.project.reportservice.model.dto.UserMetricsResponse;
import ex.org.project.reportservice.model.populationMetrics.UserPopulationAggregateType;
import ex.org.project.reportservice.model.populationMetrics.UserPopulationMetrics;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.comparators.FixedOrderComparator;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPopulationMetricsService {

    private final UserPopulationMetricsAggregator userPopulationMetricsAggregator;

    /**
     * Method for getting registered and active users based on an aggregate, startDate, and endDate provided by the
     * user. This method will call a different helper method to calculate the metrics based on the provided aggregate.
     */
    public UserMetricsResponse getUserMetricsByAggregate(String aggBy, String startDate, String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        var aggType = UserPopulationAggregateType.valueOfLabel(aggBy);
        if(aggType == null) {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }

        UserPopulationMetrics metrics = userPopulationMetricsAggregator.getUserPopulationMetrics(aggType, startDateTime, endDateTime);
        return UserMetricsResponse.builder()
                .columnNames(metrics.getColumnList())
                .aggDtos(metrics.getAggregations())
                .build();
    }

    /**
     * Method for getting registered and active users based on an aggregate, startDate, and endDate provided by the
     * user. This method will call a different helper method to calculate the metrics based on the provided aggregate
     * and return that data in a CSV file.
     */
    public void getUserPopulationMetricsCSV(HttpServletResponse response, String aggBy, String startDate,
                                            String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        var aggType = UserPopulationAggregateType.valueOfLabel(aggBy);
        if(aggType == null) {
            throw new BadDataException("Invalid 'Aggregate By' option provided");
        }

        UserPopulationMetrics metrics = userPopulationMetricsAggregator.getUserPopulationMetrics(aggType, startDateTime, endDateTime);
        String[] columnListUppercase = metrics.getColumnList().stream().map(String::toUpperCase).toArray(String[]::new);
        getCSVReport(response, metrics.getAggregations(), metrics.getFileName(), metrics.getDtoClass(), columnListUppercase);
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
     */
    private static void getCSVReport(HttpServletResponse response, List<? extends UserMetricsDto> dtoList, String fileName,
                                     Class<?> dtoClass, String[] columnNames) {
        try {
            response.setContentType("text/csv");
            String headerKey = HttpHeaders.CONTENT_DISPOSITION;
            String headerValue = "attachment; filename=\"" + fileName + "\"";
            response.setHeader(headerKey, headerValue);

            // Create the mapping strategy
            FixedOrderComparator comparator = new FixedOrderComparator(columnNames);
            HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategyBuilder().build();
            strategy.setType(dtoClass);
            strategy.setColumnOrderOnWrite(comparator);

            // Create a CSV writer
            StatefulBeanToCsv writer = new StatefulBeanToCsvBuilder(response.getWriter()).withMappingStrategy(
                    strategy).withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(true).build();

            // Write all hub content metrics to the CSV file
            writer.write(dtoList);

        }
        catch(Exception e) {
            log.error("Exception occurred", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
