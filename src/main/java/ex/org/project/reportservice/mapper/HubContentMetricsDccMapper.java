package ex.org.project.reportservice.mapper;

import ex.org.project.reportservice.model.HubContentMetrics;
import ex.org.project.reportservice.model.dto.DccDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HubContentMetricsDccMapper {

    @Mapping(source = "entity.countStudyPhs", target = "totalStudies")
    @Mapping(source = "entity.countStudyHasDataFile", target = "studiesWithData")
    @Mapping(source = "entity.standardizedDataFileCount", target = "transformFilesCount")
    @Mapping(source = "entity.origDataFileCount", target = "origRawFileCount")
    @Mapping(source = "entity.dcc",target="dcc")
    @Mapping(source = "entity.totalFileSize", target = "totalFileSize", qualifiedByName = "doubleMapper")
    DccDto toDto(HubContentMetrics entity);

    List<DccDto> toDto(List<HubContentMetrics> entity);

    @Named("doubleMapper")
    static Double mapDouble(Double num){
        if (num == null){
            return 0.;
        }
        return num;
    }
}
