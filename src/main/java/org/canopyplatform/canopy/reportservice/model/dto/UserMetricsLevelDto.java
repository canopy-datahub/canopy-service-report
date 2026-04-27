package org.canopyplatform.canopy.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

import static org.canopyplatform.canopy.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

@Getter
@Setter
@JsonPropertyOrder({USER_LEVEL, REGISTERED_USERS, ACTIVE_USERS})
public class UserMetricsLevelDto extends UserMetricsAggregatesDto {
    @JsonProperty(USER_LEVEL)
    @CsvBindByName(column = USER_LEVEL)
    protected String userResearcherLevel;
}
