package ex.org.project.reportservice.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SubmissionActivitiesMetricsResponse(
        List<String> columnNames,
        List<? extends SubmissionActivitiesMetricsDto> dtos
) {}
