package org.canopyplatform.canopy.reportservice.mapper;

import org.canopyplatform.canopy.reportservice.model.dto.UserActivitiesDto;
import org.canopyplatform.canopy.reportservice.model.UserActivitiesMetrics;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mapper(componentModel="spring")
public interface UserActivitiesMapper {

    @Mapping(source = "metrics", target = "metrics", qualifiedByName = "mapToList")
    UserActivitiesDto userActivitiesMetricsToDto(UserActivitiesMetrics metrics, @Context String sortedBy);

    @Named("mapToList")
    static List<Map<String, Object>> mapToList(Map<String, Map<String, Object>> metrics, @Context String sortedBy){
        return metrics.values().stream()
                .sorted(Comparator.comparing(value -> (Integer) value.get(sortedBy),
                        Comparator.nullsLast(Comparator.reverseOrder()))
                )
                .toList();
    }
}
