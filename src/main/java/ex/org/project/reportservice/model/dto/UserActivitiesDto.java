package ex.org.project.reportservice.model.dto;

import java.util.List;
import java.util.Map;

public record UserActivitiesDto(
        String dimension,
        List<String> headers,
        List<Map<String, Object>> metrics
){}
