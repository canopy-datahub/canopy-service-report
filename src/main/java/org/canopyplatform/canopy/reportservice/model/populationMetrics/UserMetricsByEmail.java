package org.canopyplatform.canopy.reportservice.model.populationMetrics;

import org.canopyplatform.canopy.reportservice.model.dto.UserMetricsEmailDto;
import org.canopyplatform.canopy.reportservice.mapper.UserPopulationMapper;
import org.canopyplatform.canopy.reportservice.mapper.UserPopulationMapperImpl;
import org.canopyplatform.canopy.reportservice.model.ViewUserPopulation;

import java.time.LocalDateTime;
import java.util.List;

import static org.canopyplatform.canopy.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

public class UserMetricsByEmail extends UserPopulationMetrics {

    //if columns need changed, also change in the dto object
    private final List<String> columns = List.of(NAME, EMAIL, ORCID_ID, JOB_TITLE, INSTITUTION, INSTITUTION_TYPE, USER_LOCATION_STATE, USER_LOCATION_COUNTRY, USER_LEVEL, CREATED_AT, LAST_LOGIN, TOTAL_LOGIN, INTERNAL_USER, DOWNLOADED_DATA);
    private final List<UserMetricsEmailDto> aggregations;
    private static final Class<UserMetricsEmailDto> dtoClass = UserMetricsEmailDto.class;
    private final UserPopulationMapper userPopulationMapper = new UserPopulationMapperImpl();

    public UserMetricsByEmail(List<ViewUserPopulation> viewUserPopulationList, LocalDateTime startDate,
                              LocalDateTime endDate){
        this.aggregations = getUserMetricsByEmail(viewUserPopulationList, startDate, endDate);
    }

    @Override
    public List<String> getColumnList() {
        return columns;
    }

    @Override
    public List<UserMetricsEmailDto> getAggregations() {
        return aggregations;
    }

    @Override
    public Class<UserMetricsEmailDto> getDtoClass() {
        return dtoClass;
    }

    private List<UserMetricsEmailDto> getUserMetricsByEmail(List<ViewUserPopulation> userPopulationList,
                                                            LocalDateTime startDate, LocalDateTime endDate){
        return userPopulationList
                .stream()
                .filter(viewUserPopulation -> userActiveBetweenDates(viewUserPopulation, startDate, endDate))
                .map(userPopulationMapper::toDTO)
                .toList();
    }

    private boolean userActiveBetweenDates(ViewUserPopulation user, LocalDateTime startDate, LocalDateTime endDate){
        return (user.getCreatedAt().isAfter(startDate) && user.getCreatedAt().isBefore(endDate)) ||
                (user.getLastLogin() != null && (user.getLastLogin().isAfter(startDate) && user.getLastLogin().isBefore(endDate)));
    }
}
