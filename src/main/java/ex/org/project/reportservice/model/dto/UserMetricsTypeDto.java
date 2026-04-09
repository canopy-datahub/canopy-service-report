package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

import static ex.org.project.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

@Getter
@Setter
@JsonPropertyOrder({INSTITUTION_TYPE, REGISTERED_USERS, ACTIVE_USERS})
public class UserMetricsTypeDto extends UserMetricsAggregatesDto {
    @JsonProperty(INSTITUTION_TYPE)
    @CsvBindByName(column = INSTITUTION_TYPE)
    protected String institutionType;
}
