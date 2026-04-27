package org.canopyplatform.canopy.reportservice.mapper;

import org.canopyplatform.canopy.reportservice.model.dto.CenterDto;
import org.canopyplatform.canopy.reportservice.model.HubContentMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HubContentMetricsCenterMapper {

    @Mapping(source = "entity.countStudy", target = "totalStudies")
    @Mapping(source = "entity.countStudyHasDataFile", target = "studiesWithData")
    @Mapping(source = "entity.standardizedDataFileCount", target = "transformFilesCount")
    @Mapping(source = "entity.origDataFileCount", target = "origRawFileCount")
    @Mapping(source = "entity.center",target="center")
    @Mapping(source = "entity.totalFileSize", target = "totalFileSize", qualifiedByName = "doubleMapper")
    CenterDto toDto(HubContentMetrics entity);

    List<CenterDto> toDto(List<HubContentMetrics> entity);

    @Named("doubleMapper")
    static Double mapDouble(Double num){
        if (num == null){
            return 0.;
        }
        return num;
    }
}
