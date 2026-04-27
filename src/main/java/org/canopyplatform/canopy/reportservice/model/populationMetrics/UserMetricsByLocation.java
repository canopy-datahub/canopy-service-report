package org.canopyplatform.canopy.reportservice.model.populationMetrics;

import org.canopyplatform.canopy.reportservice.model.ViewUserPopulation;
import org.canopyplatform.canopy.reportservice.model.dto.UserMetricsLocationDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.canopyplatform.canopy.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

public class UserMetricsByLocation extends UserPopulationMetrics {

    //if columns need changed, also change in the dto object
    private final List<String> columns = List.of(INSTITUTION_LOCATION, REGISTERED_USERS, ACTIVE_USERS,WORKSPACE_COUNT);
    private final List<UserMetricsLocationDto> aggregations;
    private static final Class<UserMetricsLocationDto> dtoClass = UserMetricsLocationDto.class;

    public UserMetricsByLocation(List<ViewUserPopulation> viewUserPopulationList,
                                 List<Integer> userLoginIds, LocalDateTime startDate,
                                 LocalDateTime endDate){
        this.aggregations = getUserMetricsByInstitutionLocation(viewUserPopulationList, userLoginIds, startDate, endDate);
    }

    @Override
    public List<String> getColumnList() {
        return columns;
    }

    @Override
    public List<UserMetricsLocationDto> getAggregations() {
        return aggregations;
    }

    @Override
    public Class<UserMetricsLocationDto> getDtoClass() {
        return dtoClass;
    }

    /**
     * Helper method for calculating user metrics when provided the Institution: Location aggregate.
     */
    private List<UserMetricsLocationDto> getUserMetricsByInstitutionLocation(List<ViewUserPopulation> userLocationList,
                                                                            List<Integer> userLoginIds,
                                                                            LocalDateTime startDate,
                                                                            LocalDateTime endDate) {
        List<UserMetricsLocationDto> userMetricsList = new ArrayList<>();
        for(ViewUserPopulation user : userLocationList) {
            if(user.getCountry() == null) {
                continue;
            }

            UserMetricsLocationDto dto = null;
            boolean updateListFlag = false;
            //Get metrics by aggregate if it exists, or create new if it does not exist
            if(!userMetricsList.isEmpty()) {
                dto = getUserMetricsLocationFromListByAggregate(userMetricsList, user.getCountry());
                if(dto == null) {
                    dto = new UserMetricsLocationDto();
                    dto.setInstitutionLocation(user.getCountry());
                    dto.setRegisteredUsers(0);
                    dto.setActiveUsers(0);
                    dto.setWorkspaceCount(0);
                    updateListFlag = true;
                }
            }
            else {
                dto = new UserMetricsLocationDto();
                dto.setInstitutionLocation(user.getCountry());
                dto.setRegisteredUsers(0);
                dto.setActiveUsers(0);
                dto.setWorkspaceCount(0);
                updateListFlag = true;
            }

            incrementUserMetrics(user, dto, startDate, endDate, userLoginIds);
            if(updateListFlag) {
                userMetricsList.add(dto);
            }
        }
        return userMetricsList;
    }

    /**
     * Helper method to find a specific DTO from a list of DTOs based on an aggregate
     *
     * @param dtoList   List to retrieve the DTO from
     * @param aggregate This should be the "full" aggregate, example: Institution: Location
     */
    public static UserMetricsLocationDto getUserMetricsLocationFromListByAggregate(List<UserMetricsLocationDto> dtoList,
                                                                            String aggregate) {
        for(UserMetricsLocationDto dto : dtoList) {
            if(dto.getInstitutionLocation().equals(aggregate)) {
                return dto;
            }
        }
        return null;
    }
}
