package ex.org.project.reportservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivitiesMetrics {

    private String dimension;
    private String startDate;
    private String endDate;
    private Integer rowCount;
    private List<String> headers;
    private Map<String, Map<String, Object>> metrics;
}
