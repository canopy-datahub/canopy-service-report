package ex.org.project.reportservice.mapper;

import ex.org.project.reportservice.model.dto.SubmissionActivitiesMetricsDccDto;
import ex.org.project.reportservice.model.dto.SubmissionActivitiesMetricsStudyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface SubmissionActivityMapper {

    @Mapping(target = "dcc", source = "dcc", qualifiedByName = "stringMapper")
    @Mapping(target = "studiesInitiated", source = "studies_initiated", qualifiedByName = "longMapper")
    @Mapping(target = "studiesPublished", source = "studies_published", qualifiedByName = "longMapper")
    @Mapping(target = "dataFilesSubmitted", source = "data_files_submitted", qualifiedByName = "longMapper")
    @Mapping(target = "dataFilesApproved", source = "data_files_approved", qualifiedByName = "longMapper")
    @Mapping(target = "dataFilesRejected", source = "data_files_rejected", qualifiedByName = "longMapper")
    SubmissionActivitiesMetricsDccDto mapToDccDto(Map<String, Object> map);

    List<SubmissionActivitiesMetricsDccDto> mapToDccDtoList(List<Map<String, Object>> maps);


    @Mapping(target = "dcc", source = "dcc", qualifiedByName = "stringMapper")
    @Mapping(target = "dataFilesSubmitted", source = "data_files_submitted", qualifiedByName = "longMapper")
    @Mapping(target = "dataFilesApproved", source = "data_files_approved", qualifiedByName = "longMapper")
    @Mapping(target = "dataFilesRejected", source = "data_files_rejected", qualifiedByName = "longMapper")
    @Mapping(target = "phs", source = "study_phs", qualifiedByName = "stringMapper")
    @Mapping(target = "studyName", source = "study_name", qualifiedByName = "stringMapper")
    SubmissionActivitiesMetricsStudyDto mapToStudyDto(Map<String, Object> map);

    List<SubmissionActivitiesMetricsStudyDto> mapToStudyDtoList(List<Map<String, Object>> maps);

    @Named("stringMapper")
    public static String mapString(Object value){
        return (String) value;
    }

    @Named("longMapper")
    public static Long mapLong(Object value){
        if (value == null){
            return 0L;
        }
        return (Long) value;
    }
}
