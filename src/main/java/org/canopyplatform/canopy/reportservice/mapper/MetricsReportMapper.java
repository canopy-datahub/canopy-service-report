package org.canopyplatform.canopy.reportservice.mapper;

import java.util.List;

import org.canopyplatform.canopy.reportservice.model.dto.ReportDateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.canopyplatform.canopy.reportservice.model.MetricsReport;

@Mapper(componentModel = "spring")
public interface MetricsReportMapper {

    @Mapping(source = "id", target = "reportId")
    @Mapping(source = "reportDate", target = "reportDate")
    ReportDateDTO toReportDateDto(MetricsReport report);

    List<ReportDateDTO> toReportDateDtos(List<MetricsReport> reports);
}
