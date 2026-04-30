package org.canopyplatform.canopy.reportservice.model.populationMetrics;

import org.canopyplatform.canopy.reportservice.model.ViewUserPopulation;
import org.canopyplatform.canopy.reportservice.model.dto.UserMetricsTypeDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.canopyplatform.canopy.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

public class UserMetricsByInstitutionType extends UserPopulationMetrics {

    //if columns need changed, also change in the dto object
    private final List<String> columns = List.of(INSTITUTION_TYPE, REGISTERED_USERS, ACTIVE_USERS);
    private final List<UserMetricsTypeDto> aggregations;
    private static final Class<UserMetricsTypeDto> dtoClass = UserMetricsTypeDto.class;

    public UserMetricsByInstitutionType(List<ViewUserPopulation> viewUserPopulationList,
                                        List<Integer> userLoginIds, LocalDateTime startDate,
                                        LocalDateTime endDate){

        this.aggregations = getUserMetricsByInstitutionType(viewUserPopulationList, userLoginIds, startDate, endDate);
    }

    @Override
    public List<String> getColumnList() {
        return columns;
    }

    @Override
    public List<UserMetricsTypeDto> getAggregations() {
        return aggregations;
    }

    @Override
    public Class<UserMetricsTypeDto> getDtoClass() {
        return dtoClass;
    }

    /**
     * Helper method for calculating user metrics when provided the Institution: Type aggregate.
     */
    private List<UserMetricsTypeDto> getUserMetricsByInstitutionType(List<ViewUserPopulation> institutionTypeList,
                                                                    List<Integer> userLoginIds, LocalDateTime startDate,
                                                                    LocalDateTime endDate) {
        List<UserMetricsTypeDto> userMetricsList = new ArrayList<>();
        for(ViewUserPopulation user : institutionTypeList) {
            if(user.getInstitutionType() == null) {
                continue;
            }

            UserMetricsTypeDto dto = null;
            boolean updateListFlag = false;
            //Get metrics by aggregate if it exists, or create new if it does not exist
            if(!userMetricsList.isEmpty()) {
                dto = getUserMetricsTypeFromListByAggregate(userMetricsList, user.getInstitutionType());
                if(dto == null) {
                    dto = new UserMetricsTypeDto();
                    dto.setInstitutionType(user.getInstitutionType());
                    dto.setRegisteredUsers(0);
                    dto.setActiveUsers(0);
                    updateListFlag = true;
                }
            }
            else {
                dto = new UserMetricsTypeDto();
                dto.setInstitutionType(user.getInstitutionType());
                dto.setRegisteredUsers(0);
                dto.setActiveUsers(0);
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
     * @param aggregate This should be the "full" aggregate, example: Institution: Type
     */
    public static UserMetricsTypeDto getUserMetricsTypeFromListByAggregate(List<UserMetricsTypeDto> dtoList,
                                                                    String aggregate) {
        for(UserMetricsTypeDto dto : dtoList) {
            if(dto.getInstitutionType().equals(aggregate)) {
                return dto;
            }
        }
        return null;
    }
}
