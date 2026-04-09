package ex.org.project.reportservice.model.dto;


import lombok.Builder;
import java.util.List;
@Builder
public record HubContentAggMetricsResponse(

        List<String> columnNames,
        List<? extends HubContentAggMetricsDto> aggDtos
) {}
