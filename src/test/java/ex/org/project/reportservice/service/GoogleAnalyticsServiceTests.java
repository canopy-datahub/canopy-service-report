package ex.org.project.reportservice.service;

import com.google.analytics.data.v1beta.*;
import ex.org.project.reportservice.config.GoogleAnalyticsConfig;
import ex.org.project.reportservice.exceptions.UserActivitiesReportException;
import ex.org.project.reportservice.model.UserActivitiesMetrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class GoogleAnalyticsServiceTests {

    @Mock
    private BetaAnalyticsDataClient analyticsDataClient;

    @Mock
    private GoogleAnalyticsConfig googleAnalyticsConfig;

    @InjectMocks
    private GoogleAnalyticsService googleAnalyticsService;

    private final List<String> mockMetricDimensionValues = List.of("DimensionValue1", "DimensionValue2", "DimensionValue3");
    private final List<String> mockEventDimensionValues = List.of("DimensionValue1", "Event1", "DimensionValue2", "Event2", "DimensionValue4", "Event4");
    private final List<String> mockMetricProperties = List.of("Metric1", "Metric2", "Metric3");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        googleAnalyticsService = new GoogleAnalyticsService(analyticsDataClient, googleAnalyticsConfig);
    }

    private UserActivitiesMetrics getMockUserActivitiesMetrics(String dimension, String startDate, String endDate, String testProperty) {
        Map<String, Map<String, Object>> mockMetrics = new HashMap<>();
        Map<String, Object> mockProperties = new HashMap<>();
        mockProperties.put(testProperty, "testValue");
        mockMetrics.put("TestDimension1", mockProperties);
        mockMetrics.put("TestDimension2", mockProperties);
        UserActivitiesMetrics mockReport = new UserActivitiesMetrics();
        mockReport.setDimension(dimension);
        mockReport.setStartDate(startDate);
        mockReport.setEndDate(endDate);
        mockReport.setHeaders(List.of("TestDimension1", "TestDimension2"));
        mockReport.setMetrics(mockMetrics);
        mockReport.setRowCount(2);
        return mockReport;
    }

    private RunReportResponse getMockMetricResponse(List<String> dimensions, List<String> metrics){
        List<DimensionHeader> dimensionHeaders = dimensions
                .stream()
                .map(dimension -> DimensionHeader.newBuilder().setName(dimension).build())
                .toList();
        List<MetricHeader> metricHeaders = metrics
                .stream()
                .map(metric -> MetricHeader.newBuilder().setName(metric).build())
                .toList();
        List<DimensionValue> dimensionValues = mockMetricDimensionValues
                .stream()
                .map(string -> DimensionValue.newBuilder().setValue(string).build())
                .toList();
        List<MetricValue> metricValues = List.of("1", "2", "3")
                .stream()
                .map(string -> MetricValue.newBuilder().setValue(string).build())
                .toList();
        List<Row> mockResponseRows = IntStream
                .range(0, dimensionValues.size())
                .boxed()
                .map(i -> Row
                        .newBuilder()
                        .addDimensionValues(dimensionValues.get(i))
                        .addAllMetricValues(metricValues)
                        .build()
                )
                .toList();
        RunReportResponse mockResponse = RunReportResponse
                .newBuilder()
                .addAllDimensionHeaders(dimensionHeaders)
                .addAllMetricHeaders(metricHeaders)
                .addAllRows(mockResponseRows)
                .setRowCount(dimensionValues.size())
                .build();
        return mockResponse;
    }

    private RunReportResponse getMockEventResponse(List<String> dimensions, List<String> metrics){
        List<DimensionHeader> dimensionHeaders = dimensions
                .stream()
                .map(dimension -> DimensionHeader.newBuilder().setName(dimension).build())
                .toList();
        List<MetricHeader> metricHeaders = List.of(MetricHeader.newBuilder().setName("eventCount").build());
        List<DimensionValue> dimensionValues = mockEventDimensionValues
                .stream()
                .map(string -> DimensionValue.newBuilder().setValue(string).build())
                .toList();
        List<MetricValue> metricValues = List.of(MetricValue.newBuilder().setValue("1").build());
        List<Row> mockResponseRows = IntStream
                .range(0, dimensionValues.size()/2)
                .boxed()
                .map(i -> Row
                        .newBuilder()
                        .addDimensionValues(dimensionValues.get(i*2))
                        .addDimensionValues(dimensionValues.get((i*2)+1))
                        .addAllMetricValues(metricValues)
                        .build()
                )
                .toList();
        RunReportResponse mockResponse = RunReportResponse
                .newBuilder()
                .addAllDimensionHeaders(dimensionHeaders)
                .addAllMetricHeaders(metricHeaders)
                .addAllRows(mockResponseRows)
                .setRowCount(dimensionValues.size()/2)
                .build();
        return mockResponse;
    }

    @Test
    void testRunMetricReport(){
        //set up mock data
        String dimension = "Test";
        String startDate = "yesterday";
        String endDate = "today";
        List<String> dimensions = List.of(dimension);
        RunReportResponse mockGoogleMetricResponse = getMockMetricResponse(dimensions, mockMetricProperties);

        //fake responses
        when(googleAnalyticsConfig.getUserAnalyticsFields()).thenReturn(mockMetricProperties);
        when(googleAnalyticsConfig.getPropertyId())
                .thenReturn(String.valueOf(12345));
        when(analyticsDataClient.runReport(any()))
                .thenReturn(mockGoogleMetricResponse);

        //run method to test
        UserActivitiesMetrics response = googleAnalyticsService.runMetricReport(dimension, startDate, endDate);

        //check if everything worked
        Assertions.assertEquals(dimension, response.getDimension());
        Assertions.assertEquals(startDate, response.getStartDate());
        Assertions.assertEquals(endDate, response.getEndDate());
        Assertions.assertEquals(3, response.getRowCount());
        Assertions.assertTrue(response.getMetrics().containsKey(mockMetricDimensionValues.get(0)));
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(0))
                .containsKey(mockMetricProperties.get(0))
        );
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(0))
                .containsKey(mockMetricProperties.get(1))
        );
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(0))
                .containsKey(mockMetricProperties.get(2))
        );
        Assertions.assertTrue(response.getMetrics().containsKey(mockMetricDimensionValues.get(1)));
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(1))
                .containsKey(mockMetricProperties.get(0))
        );
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(1))
                .containsKey(mockMetricProperties.get(1))
        );
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(1))
                .containsKey(mockMetricProperties.get(2))
        );
        Assertions.assertTrue(response.getMetrics().containsKey(mockMetricDimensionValues.get(2)));
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(2))
                .containsKey(mockMetricProperties.get(0))
        );
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(2))
                .containsKey(mockMetricProperties.get(1))
        );
        Assertions.assertTrue(response.getMetrics().get(mockMetricDimensionValues.get(2))
                .containsKey(mockMetricProperties.get(2))
        );
    }

    @Test
    void testRunEventReport(){
        //set up mock data
        String dimension = "Test";
        String startDate = "yesterday";
        String endDate = "today";
        List<String> dimensions = List.of(dimension, "eventName");
        RunReportResponse mockGoogleEventResponse = getMockEventResponse(dimensions, mockMetricProperties);

        //fake responses
        when(googleAnalyticsConfig.getUserAnalyticsFields()).thenReturn(mockMetricProperties);
        when(googleAnalyticsConfig.getPropertyId())
                .thenReturn(String.valueOf(12345));
        when(analyticsDataClient.runReport(any()))
                .thenReturn(mockGoogleEventResponse);

        //run method to test
        UserActivitiesMetrics response = googleAnalyticsService.runEventReport(dimension, startDate, endDate);

        //check if everything worked
        Assertions.assertEquals(dimension, response.getDimension());
        Assertions.assertEquals(startDate, response.getStartDate());
        Assertions.assertEquals(endDate, response.getEndDate());
        Assertions.assertEquals(3, response.getRowCount());
        Assertions.assertTrue(response.getMetrics().containsKey(mockEventDimensionValues.get(0)));
        Assertions.assertTrue(response.getMetrics().get(mockEventDimensionValues.get(0))
                .containsKey(mockEventDimensionValues.get(1))
        );
        Assertions.assertEquals(1,response.getMetrics().get(mockEventDimensionValues.get(0))
                .get(mockEventDimensionValues.get(1)));
        Assertions.assertTrue(response.getMetrics().containsKey(mockEventDimensionValues.get(2)));
        Assertions.assertTrue(response.getMetrics().get(mockEventDimensionValues.get(2))
                .containsKey(mockEventDimensionValues.get(3))
        );
        Assertions.assertEquals(1,response.getMetrics().get(mockEventDimensionValues.get(2))
                .get(mockEventDimensionValues.get(3)));
        Assertions.assertTrue(response.getMetrics().containsKey(mockEventDimensionValues.get(4)));
        Assertions.assertTrue(response.getMetrics().get(mockEventDimensionValues.get(4))
                .containsKey(mockEventDimensionValues.get(5))
        );
        Assertions.assertEquals(1,response.getMetrics().get(mockEventDimensionValues.get(4))
                .get(mockEventDimensionValues.get(5)));
    }

    @Test
    void testConsolidateReportsIntoFirstReport(){
        //set up fake data
        String dimension = "Test";
        String startDate = "yesterday";
        String endDate = "today";
        UserActivitiesMetrics mockMetrics = getMockUserActivitiesMetrics(dimension, startDate, endDate, "metrics");
        UserActivitiesMetrics mockEvents = getMockUserActivitiesMetrics(dimension, startDate, endDate, "events");
        Map<String, Object> mockProperties = new HashMap<>();
        mockProperties.put("ExtraEventProperty", "testValue");
        mockEvents.getMetrics().put("TestDimension3", mockProperties);

        //run method to be tested
        googleAnalyticsService.consolidateReportsIntoFirstReport(mockMetrics, mockEvents);

        //evaluate results
        Assertions.assertEquals(dimension, mockMetrics.getDimension());
        Assertions.assertEquals(startDate, mockMetrics.getStartDate());
        Assertions.assertEquals(endDate, mockMetrics.getEndDate());
        Assertions.assertEquals(2, mockMetrics.getRowCount());
        Assertions.assertTrue(mockMetrics.getMetrics().containsKey("TestDimension1"));
        Assertions.assertTrue(mockMetrics.getMetrics().containsKey("TestDimension2"));
        Assertions.assertFalse(mockMetrics.getMetrics().containsKey("TestDimension3"));
    }

    @Test
    void testConsolidateReportsIntoFirstReport_DifferentDimension(){
        //set up fake data
        String dimension1 = "Test1";
        String dimension2 = "Test2";
        String startDate = "yesterday";
        String endDate = "today";
        UserActivitiesMetrics mockMetrics = getMockUserActivitiesMetrics(dimension1, startDate, endDate, "metrics");
        UserActivitiesMetrics mockEvents = getMockUserActivitiesMetrics(dimension2, startDate, endDate, "events");

        //run method to be tested
        try {
            googleAnalyticsService.consolidateReportsIntoFirstReport(mockMetrics, mockEvents);
        } catch (UserActivitiesReportException e){
            Assertions.assertTrue(true);
            return;
        }
        Assertions.fail("Method should fail for different dimensions: " + dimension1 + " : " + dimension2);
    }

    @Test
    void testConsolidateReportsIntoFirstReport_DifferentStartDates(){
        //set up fake data
        String dimension = "Test";
        String startDate1 = "yesterday";
        String startDate2 = "7daysAgo";
        String endDate = "today";
        UserActivitiesMetrics mockMetrics = getMockUserActivitiesMetrics(dimension, startDate1, endDate, "metrics");
        UserActivitiesMetrics mockEvents = getMockUserActivitiesMetrics(dimension, startDate2, endDate, "events");

        //run method to be tested
        Assertions.assertThrows(UserActivitiesReportException.class,
                () -> googleAnalyticsService.consolidateReportsIntoFirstReport(mockMetrics, mockEvents)
        );
    }

    @Test
    void testConsolidateReportsIntoFirstReport_DifferentEndDates(){
        //set up fake data
        String dimension = "Test";
        String startDate = "7daysAgo";
        String endDate1 = "today";
        String endDate2 = "yesterday";
        UserActivitiesMetrics mockMetrics = getMockUserActivitiesMetrics(dimension, startDate, endDate1, "metrics");
        UserActivitiesMetrics mockEvents = getMockUserActivitiesMetrics(dimension, startDate, endDate2, "events");

        //run method to be tested
        Assertions.assertThrows(UserActivitiesReportException.class,
                () -> googleAnalyticsService.consolidateReportsIntoFirstReport(mockMetrics, mockEvents)
        );
    }

    @Disabled("There is no overlap between metrics keys and event keys currently but should still add in the future")
    @Test
    void testConsolidateReportsIntoFirstReportInnerKeyCollisions(){}

}
