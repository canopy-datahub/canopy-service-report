package ex.org.project.reportservice.mapper;

import ex.org.project.reportservice.model.HubContentMetrics;
import ex.org.project.reportservice.model.dto.StudyPhsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HubContentMetricsStudyMapper {

    @Mapping(source = "entity.studyTitle", target = "studyName")
    @Mapping(source = "entity.standardizedDataFileCount", target = "transformFilesCount")
    @Mapping(source = "entity.origDataFileCount", target = "origRawFileCount")
    @Mapping(source = "entity.studyPhs", target= "studyPhs")
    StudyPhsDto toDto(HubContentMetrics entity);

    List<StudyPhsDto> toDto(List<HubContentMetrics> entity);
}
