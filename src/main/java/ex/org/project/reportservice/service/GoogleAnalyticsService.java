package ex.org.project.reportservice.service;

import com.google.analytics.data.v1beta.*;
import ex.org.project.reportservice.exceptions.UserActivitiesReportException;
import ex.org.project.reportservice.config.GoogleAnalyticsConfig;
import ex.org.project.reportservice.model.UserActivitiesMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@Service
public class GoogleAnalyticsService {

    private final BetaAnalyticsDataClient analyticsDataClient;
    private final GoogleAnalyticsConfig googleAnalyticsConfig;
    
    @Autowired
    public GoogleAnalyticsService(
            @Autowired(required = false) BetaAnalyticsDataClient analyticsDataClient,
            GoogleAnalyticsConfig googleAnalyticsConfig) {
        this.analyticsDataClient = analyticsDataClient;
        this.googleAnalyticsConfig = googleAnalyticsConfig;
        
        if (analyticsDataClient == null) {
            log.warn("Google Analytics client is not available. GA features will return empty data.");
        } else {
            log.info("Google Analytics client initialized successfully.");
        }
    }
    
    private boolean isGoogleAnalyticsEnabled() {
        return analyticsDataClient != null;
    }

    /**
     * Runs a metric report and an event report and then consolidates them into a single report
     * @param dimension The Google Analytics dimension on which to aggregate metrics
     * @param startDate The start date to filter by
     * @param endDate The end date to filter by
     * @return UserActivitiesMetrics
     */
    public UserActivitiesMetrics getUserActivitiesReport(String dimension, String startDate, String endDate){
        if (!isGoogleAnalyticsEnabled()) {
            log.warn("Google Analytics is not configured. Returning empty metrics.");
            return UserActivitiesMetrics.builder()
                    .dimension(formatLabel(dimension))
                    .startDate(startDate)
                    .endDate(endDate)
                    .rowCount(0)
                    .headers(List.of(formatLabel(dimension)))
                    .metrics(new HashMap<>())
                    .build();
        }
        
        UserActivitiesMetrics metricReport = runMetricReport(dimension, startDate, endDate);
        UserActivitiesMetrics eventReport = runEventReport(dimension, startDate, endDate);
        //consolidate reports into metricReport
        consolidateReportsIntoFirstReport(metricReport, eventReport);
        log.debug(metricReport.toString());
        metricReport.setDimension(formatLabel(dimension));
        return metricReport;
    }

    /**
     * Runs a timeframe based metric report on the user provided dimension and the metrics fields set in the config
     * @param dimension The Google Analytics dimension on which to aggregate metrics
     * @param startDate The start date to filter by
     * @param endDate The end date to filter by
     * @return UserActivitiesMetrics
     */
    public UserActivitiesMetrics runMetricReport(String dimension, String startDate, String endDate) {
        //build request to GA, aggregate metrics by dimension

        RunReportRequest.Builder requestBuilder = RunReportRequest.newBuilder();
        for(String metric : googleAnalyticsConfig.getUserAnalyticsFields()){
            requestBuilder.addMetrics(Metric.newBuilder().setName(metric));
        }
        RunReportRequest request = requestBuilder
            .setProperty("properties/" + googleAnalyticsConfig.getPropertyId())
            .addDimensions(Dimension.newBuilder().setName(dimension))
            .addDateRanges(DateRange.newBuilder().setStartDate(startDate).setEndDate(endDate))
            .build();

        // Make the request.
        RunReportResponse response = analyticsDataClient.runReport(request);

        List<String> headerList = new ArrayList<>();
        headerList.add(formatLabel(dimension));
        headerList.addAll(formatMetricLabels(response.getMetricHeadersList()));

        //convert response to Map<countryName, Map<property, value>>
        Map<String, Map<String, Object>> metrics = new HashMap<>();
        for (Row row : response.getRowsList()) {
            String key = row.getDimensionValues(0).getValue();
            Map<String, Object> value = IntStream.range(0, response.getMetricHeadersCount())
                .boxed().collect(Collectors.toMap(
                    i -> headerList.get(i+1),
                    i -> Integer.valueOf(row.getMetricValues(i).getValue()))
                );
            value.put(formatLabel(dimension), key);
            metrics.put(key, value);
        }

        log.debug(metrics.toString());
        return UserActivitiesMetrics.builder()
                .dimension(dimension)
                .startDate(startDate)
                .endDate(endDate)
                .rowCount(response.getRowCount())
                .headers(headerList)
                .metrics(metrics)
                .build();
    }

    /**
     * Runs a timeframe based event report on the user provided dimension
     * Currently only  provides information on file downloads
     * @param dimension The Google Analytics dimension on which to aggregate metrics
     * @param startDate The start date to filter by
     * @param endDate The end date to filter by
     * @return UserActivitiesMetrics
     */
    public UserActivitiesMetrics runEventReport(String dimension, String startDate, String endDate) {
        // build request to get event data, filter out every event except file downloads
        // this call would be easier if there were actual metrics set up for the events
        RunReportRequest request =
            RunReportRequest.newBuilder()
                .setProperty("properties/" + googleAnalyticsConfig.getPropertyId())
                .addDimensions(Dimension.newBuilder().setName(dimension))
                .addDimensions(Dimension.newBuilder().setName("eventName"))
                    //if another metric is added, the loop retrieving the data will need to be updated slightly
                .addMetrics(Metric.newBuilder().setName("eventCount"))
                    //remove this filter and it will return the count of all events
                .setDimensionFilter(FilterExpression.newBuilder()
                    .setFilter(Filter.newBuilder()
                        .setStringFilter(Filter.StringFilter.newBuilder()
                            .setMatchType(Filter.StringFilter.MatchType.EXACT)
                            .setValue("file_download")
                            .build())
                        .setFieldName("eventName")
                        .build())
                    .build())
                .addDateRanges(DateRange.newBuilder().setStartDate(startDate).setEndDate(endDate))
                .build();

        // Make the request to GA
        RunReportResponse response = analyticsDataClient.runReport(request);

        //convert response to Map<countryName, Map<property, value>>
        Map<String, Map<String, Object>>  metrics = new HashMap<>();
        Set<String> headerList = new HashSet<>();
        //List<String> headerList = List.of("Events");
        for (Row row : response.getRowsList()) {
            String key = row.getDimensionValues(0).getValue();
            Map<String, Object> value = IntStream.range(0, response.getMetricHeadersCount())
                    .boxed().collect(Collectors.toMap(
                            i -> formatEventLabel(row.getDimensionValues(1)), //won't work if you add another metric, but I want it to say the specific event not 'eventName'
                            i -> Integer.valueOf(row.getMetricValues(i).getValue()))
                    );
            if(metrics.containsKey(key)){
                metrics.get(key).putAll(value);
                headerList.addAll(value.keySet());
            }
            else {
                metrics.put(key, value);
                headerList.addAll(value.keySet());
            }
        }
        log.debug(metrics.toString());
        return UserActivitiesMetrics.builder()
                .dimension(dimension)
                .startDate(startDate)
                .endDate(endDate)
                .rowCount(response.getRowCount())
                .headers(headerList.stream().toList())
                .metrics(metrics)
                .build();
    }

    /**
     * @param metricReport UserActivitiesMetrics object that will be the primary for the combination.
     *                     The keys in this object will be the only keys in the final combination.
     * @param eventReport UserActivitiesMetrics object that will be added into the metric report.
     *                    If there is a key in this object that is not in metricReport, it will be ignored.
     */
    public void consolidateReportsIntoFirstReport(UserActivitiesMetrics metricReport, UserActivitiesMetrics eventReport){

        //check if reports cover the same date range and aggregation
        if((!Objects.equals(metricReport.getDimension(), eventReport.getDimension())) ||
                (!Objects.equals(metricReport.getStartDate(), eventReport.getStartDate())) ||
                (!Objects.equals(metricReport.getEndDate(), eventReport.getEndDate()))
        ){
            String errorMessage = "Report parameters must match";
            log.error(errorMessage);
            throw new UserActivitiesReportException(errorMessage);
        }

        List<String> headers = new ArrayList<>();
        headers.addAll(metricReport.getHeaders());
        headers.addAll(eventReport.getHeaders());
        metricReport.setHeaders(headers);

        //need to add error handling / unit test for key collisions
        for(Map.Entry<String, Map<String, Object>> entry : metricReport.getMetrics().entrySet()){
            //if the event report has a key that the metric report doesn't, that key won't exist in the final report
            if(eventReport.getMetrics().containsKey(entry.getKey())){
                entry.getValue().putAll(eventReport.getMetrics().get(entry.getKey()));
            }
        }
    }

    private List<String> formatMetricLabels(List<MetricHeader> metricLabels) {
        return metricLabels.stream()
                .map(header ->
                        Arrays.stream(
                                header.getName().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
                                .map(string -> StringUtils.capitalize(string.concat(" ")))
                                .collect(Collectors.joining())
                                .trim())
                .toList();
    }

    private String formatLabel(String label) {
        return Arrays.stream(
                    label.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
                )
                .map(string -> StringUtils.capitalize(string.concat(" ")))
                .collect(Collectors.joining())
                .trim();
    }

    private List<String> formatEventLabels(List<DimensionValue> eventLabels){
        return eventLabels.stream()
                .map(label ->
                        Arrays.stream(
                                        label.getValue().split("_"))
                                .map(string -> StringUtils.capitalize(string.concat(" ")))
                                .collect(Collectors.joining())
                                .trim())
                .toList();
    }

    private String formatEventLabel(DimensionValue eventLabel){
        return Arrays.stream(eventLabel.getValue().split("_"))
                .map(string -> StringUtils.capitalize(string.concat(" ")))
                .collect(Collectors.joining())
                .trim();
    }

}
