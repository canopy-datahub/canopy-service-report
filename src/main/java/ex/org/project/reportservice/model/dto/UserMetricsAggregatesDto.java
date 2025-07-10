package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

import static ex.org.project.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

@Getter
@Setter
public class UserMetricsAggregatesDto extends UserMetricsDto {
    @JsonProperty(REGISTERED_USERS)
    @CsvBindByName(column = REGISTERED_USERS)
    protected Integer registeredUsers;
    @JsonProperty(ACTIVE_USERS)
    @CsvBindByName(column = ACTIVE_USERS)
    protected Integer activeUsers;
    @JsonProperty(WORKSPACE_COUNT)
    @CsvBindByName(column = WORKSPACE_COUNT)
    protected Integer workspaceCount;
}
