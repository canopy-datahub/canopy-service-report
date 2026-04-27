package org.canopyplatform.canopy.reportservice.model.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record UserMetricsResponse (
        List<String> columnNames,
        List<? extends UserMetricsDto> aggDtos
){}
