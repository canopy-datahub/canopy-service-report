package org.canopyplatform.canopy.reportservice.mapper;

import org.canopyplatform.canopy.reportservice.model.dto.StudyIdDto;
import org.canopyplatform.canopy.reportservice.model.HubContentMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HubContentMetricsStudyMapper {

    @Mapping(source = "entity.studyTitle", target = "studyName")
    @Mapping(source = "entity.standardizedDataFileCount", target = "transformFilesCount")
    @Mapping(source = "entity.origDataFileCount", target = "origRawFileCount")
    StudyIdDto toDto(HubContentMetrics entity);

    List<StudyIdDto> toDto(List<HubContentMetrics> entity);
}
