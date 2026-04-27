package org.canopyplatform.canopy.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

import static org.canopyplatform.canopy.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

@Getter
@Setter
@JsonPropertyOrder({INSTITUTION_LOCATION, REGISTERED_USERS, ACTIVE_USERS})
public class UserMetricsLocationDto extends UserMetricsAggregatesDto {
    @JsonProperty(INSTITUTION_LOCATION)
    @CsvBindByName(column = INSTITUTION_LOCATION)
    protected String institutionLocation;
}
