package ex.org.project.reportservice.model.populationMetrics;

import ex.org.project.reportservice.model.ViewUserPopulation;
import ex.org.project.reportservice.model.dto.UserMetricsProfitDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ex.org.project.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

public class UserMetricsByProfit extends UserPopulationMetrics {

    //if columns need changed, also change in the dto object
    private final List<String> columns = List.of(INSTITUTION_PROFIT, REGISTERED_USERS, ACTIVE_USERS,WORKSPACE_COUNT);
    private final List<UserMetricsProfitDto> aggregations;
    private static final Class<UserMetricsProfitDto> dtoClass = UserMetricsProfitDto.class;

    public UserMetricsByProfit(List<ViewUserPopulation> viewUserPopulationList,
                                        List<Integer> userLoginIds, LocalDateTime startDate,
                                        LocalDateTime endDate){

        this.aggregations = getUserMetricsByProfitNotForProfit(viewUserPopulationList, userLoginIds, startDate, endDate);
    }

    @Override
    public List<String> getColumnList() {
        return columns;
    }

    @Override
    public List<UserMetricsProfitDto> getAggregations() {
        return aggregations;
    }

    @Override
    public Class<UserMetricsProfitDto> getDtoClass() {
        return dtoClass;
    }

    /**
     * Helper method for calculating user metrics when provided the Institution: Profit/Not for Profit aggregate.
     */
    private List<UserMetricsProfitDto> getUserMetricsByProfitNotForProfit(List<ViewUserPopulation> forProfitList,
                                                                         List<Integer> userLoginIds,
                                                                         LocalDateTime startDate,
                                                                         LocalDateTime endDate) {
        List<UserMetricsProfitDto> userMetricsList = new ArrayList<>();
        for(ViewUserPopulation user : forProfitList) {
            if(user.getIsForProfit() == null) {
                continue;
            }

            UserMetricsProfitDto dto = null;
            boolean updateListFlag = false;
            //Get metrics by aggregate if it exists, or create new if it does not exist
            if(!userMetricsList.isEmpty()) {
                dto = getUserMetricsProfitFromListByAggregate(userMetricsList, user.getForProfit());
                if(dto == null) {
                    dto = new UserMetricsProfitDto();
                    dto.setProfitNotForProfit(user.getForProfit());
                    dto.setRegisteredUsers(0);
                    dto.setActiveUsers(0);
                    dto.setWorkspaceCount(0);
                    updateListFlag = true;
                }
            }
            else {
                dto = new UserMetricsProfitDto();
                dto.setProfitNotForProfit(user.getForProfit());
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
     * @param aggregate This should be the "full" aggregate, example: Institution: Profit/Not for Profit
     */
    public static UserMetricsProfitDto getUserMetricsProfitFromListByAggregate(List<UserMetricsProfitDto> dtoList,
                                                                        String aggregate) {
        for(UserMetricsProfitDto dto : dtoList) {
            if(dto.getProfitNotForProfit().equals(aggregate)) {
                return dto;
            }
        }
        return null;
    }

}
