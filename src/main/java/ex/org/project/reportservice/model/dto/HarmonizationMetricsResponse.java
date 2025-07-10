package ex.org.project.reportservice.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HarmonizationMetricsResponse (
        List<String> columnNames,
        List<? extends HarmonizationMetricsDTO> dtos
){}
