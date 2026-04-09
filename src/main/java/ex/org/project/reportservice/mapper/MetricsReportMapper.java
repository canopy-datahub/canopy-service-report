package ex.org.project.reportservice.mapper;

import java.util.List;

import ex.org.project.reportservice.model.dto.ReportDateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ex.org.project.reportservice.model.MetricsReport;

@Mapper(componentModel = "spring")
public interface MetricsReportMapper {

    @Mapping(source = "id", target = "reportId")
    @Mapping(source = "reportDate", target = "reportDate")
    ReportDateDTO toReportDateDto(MetricsReport report);

    List<ReportDateDTO> toReportDateDtos(List<MetricsReport> reports);
}
