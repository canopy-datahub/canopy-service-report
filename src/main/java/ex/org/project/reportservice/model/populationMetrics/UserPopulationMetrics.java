package ex.org.project.reportservice.model.populationMetrics;

import ex.org.project.reportservice.model.ViewUserPopulation;
import ex.org.project.reportservice.model.dto.UserMetricsAggregatesDto;
import ex.org.project.reportservice.model.dto.UserMetricsDto;

import java.time.LocalDateTime;
import java.util.List;

public abstract class UserPopulationMetrics {

    private final String fileName = "User_Population_Metrics.csv";

    public abstract List<String> getColumnList();
    public abstract List<? extends UserMetricsDto> getAggregations();
    public abstract Class<?> getDtoClass();

    public String getFileName() {
        return this.fileName;
    }

    /**
     * This helper method does the actual incrementing of metrics for calculating the user population metrics.
     * It will check to see if the user was registered between the given startDate and endDate, and if so, increment
     * the registered and active users (If a user was registered they are automatically considered active).
     * If the user was not registered during the time period, it will check to see if the user was active, and if so,
     * increment the active user count.
     * This method does not return anything; it is incrementing values and adding objects to lists that the calling
     * methods have access to. The purpose of this method is to update those parameters, not return new values.
     */
    public static void incrementUserMetrics(ViewUserPopulation user, UserMetricsAggregatesDto dto, LocalDateTime startDate,
                                            LocalDateTime endDate, List<Integer> userLoginIds) {
        //Check to see if the user was registered within the time period
        LocalDateTime userRegistration = user.getCreatedAt();
        if(userRegistration.isAfter(startDate) && userRegistration.isBefore(endDate)) {
            //User was registered during the time period, increment both
            dto.setRegisteredUsers(dto.getRegisteredUsers() + 1);
            dto.setActiveUsers(dto.getActiveUsers() + 1);
            if (user.getWorkspaceCount() != null) {
                dto.setWorkspaceCount(dto.getWorkspaceCount() + user.getWorkspaceCount());
            }
        }
        else {
            //User was not registered, check if they were active, and increment active users if they were
            if(userLoginIds.contains(user.getId())) {
                dto.setActiveUsers(dto.getActiveUsers() + 1);
                if (user.getWorkspaceCount() != null) {
                    dto.setWorkspaceCount(dto.getWorkspaceCount() + user.getWorkspaceCount());
                }            }
        }
    }

}
