package ex.org.project.reportservice.mapper;

import ex.org.project.reportservice.model.DatafileHarmonizationMetricsDashboard;
import ex.org.project.reportservice.model.StudyHarmonizationMetricsDashboard;
import ex.org.project.reportservice.model.dto.DatafileHarmonizationMetricsDTO;
import ex.org.project.reportservice.model.dto.StudyHarmonizationMetricsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HarmonizationMetricsMapper {

    @Mapping(source = "studyPhs", target = "phs")
    @Mapping(source = "origTransformPairsCount", target = "numberOfFiles")
    @Mapping(source = "variableCount", target = "uniqueVariables")
    @Mapping(source = "harmonizableTier1VariableCount", target = "uniqueHarmonizableVariablesT1")
    @Mapping(source = "harmonizedTier1VariableCount", target = "uniqueHarmonizedVariablesT1")
    @Mapping(source = "harmonizableTier1Variables", target = "harmonizableVariablesT1")
    @Mapping(source = "variables", target = "allVariables")
    @Mapping(source = "harmonizedTier1Variables", target = "harmonizedVariablesT1")
    StudyHarmonizationMetricsDTO studyToDto(StudyHarmonizationMetricsDashboard studyEntity);

    List<StudyHarmonizationMetricsDTO> studyListToDtoList(List<StudyHarmonizationMetricsDashboard> studyEntityList);

    @Mapping(source = "origVariableCount", target = "originalUniqueVariables")
    @Mapping(source = "transformVariableCount", target = "transformUniqueVariables")
    @Mapping(source = "studyPhs", target = "phs")
    @Mapping(source = "harmonizableTier1VariableCount", target = "uniqueHarmonizableVariablesT1")
    @Mapping(source = "harmonizedTier1VariableCount", target = "uniqueHarmonizedVariablesT1")
    @Mapping(source = "harmonizableTier1Variables", target = "harmonizableVariablesT1")
    @Mapping(source = "origVariables", target = "origVariables")
    @Mapping(source = "transformVariables", target = "transformVariables")
    @Mapping(source = "harmonizedTier1Variables", target = "harmonizedVariablesT1")
    DatafileHarmonizationMetricsDTO datafileToDto(DatafileHarmonizationMetricsDashboard datafileEntity);

    List<DatafileHarmonizationMetricsDTO> datafileListToDtoList(List<DatafileHarmonizationMetricsDashboard> datafileEntityList);
}
