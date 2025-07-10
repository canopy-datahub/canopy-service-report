package ex.org.project.reportservice.model.populationMetrics;

import ex.org.project.reportservice.model.ViewUserPopulation;
import ex.org.project.reportservice.model.dto.UserMetricsLevelDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ex.org.project.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

public class UserMetricsByResearcherLevel extends UserPopulationMetrics {

    //if columns need changed, also change in the dto object
    private final List<String> columns = List.of(USER_LEVEL, REGISTERED_USERS, ACTIVE_USERS,WORKSPACE_COUNT);
    private final List<UserMetricsLevelDto> aggregations;
    private static final Class<UserMetricsLevelDto> dtoClass = UserMetricsLevelDto.class;

    public UserMetricsByResearcherLevel(List<ViewUserPopulation> viewUserPopulationList,
                                        List<Integer> userLoginIds, LocalDateTime startDate,
                                        LocalDateTime endDate){

        this.aggregations = getUserMetricsByResearcherLevel(viewUserPopulationList, userLoginIds, startDate, endDate);
    }

    @Override
    public List<String> getColumnList() {
        return columns;
    }

    @Override
    public List<UserMetricsLevelDto> getAggregations() {
        return aggregations;
    }

    @Override
    public Class<UserMetricsLevelDto> getDtoClass() {
        return dtoClass;
    }

    /**
     * Helper method for calculating user metrics when provided the User: Research Level aggregate.
     */
    private List<UserMetricsLevelDto> getUserMetricsByResearcherLevel(List<ViewUserPopulation> userResearchLevelList,
                                                                     List<Integer> userLoginIds,
                                                                     LocalDateTime startDate, LocalDateTime endDate) {
        List<UserMetricsLevelDto> userMetricsList = new ArrayList<>();
        for(ViewUserPopulation user : userResearchLevelList) {
            if(user.getUserResearchLevel() == null) {
                continue;
            }

            UserMetricsLevelDto dto = null;
            boolean updateListFlag = false;
            //Get metrics by aggregate if it exists, or create new if it does not exist
            if(!userMetricsList.isEmpty()) {
                dto = getUserMetricsLevelFromListByAggregate(userMetricsList, user.getUserResearchLevel());
                if(dto == null) {
                    dto = new UserMetricsLevelDto();
                    dto.setUserResearcherLevel(user.getUserResearchLevel());
                    dto.setRegisteredUsers(0);
                    dto.setActiveUsers(0);
                    dto.setWorkspaceCount(0);
                    updateListFlag = true;
                }
            }
            else {
                dto = new UserMetricsLevelDto();
                dto.setUserResearcherLevel(user.getUserResearchLevel());
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
     * @param aggregate This should be the "full" aggregate, example: User: Researcher Level
     */
    public static UserMetricsLevelDto getUserMetricsLevelFromListByAggregate(List<UserMetricsLevelDto> dtoList,
                                                                      String aggregate) {
        for(UserMetricsLevelDto dto : dtoList) {
            if(dto.getUserResearcherLevel().equals(aggregate)) {
                return dto;
            }
        }
        return null;
    }

}
