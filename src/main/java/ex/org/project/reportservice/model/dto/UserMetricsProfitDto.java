package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

import static ex.org.project.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

@Getter
@Setter
@JsonPropertyOrder({INSTITUTION_PROFIT, REGISTERED_USERS, ACTIVE_USERS})
public class UserMetricsProfitDto extends UserMetricsAggregatesDto {
    @JsonProperty(INSTITUTION_PROFIT)
    @CsvBindByName(column = INSTITUTION_PROFIT)
    protected String profitNotForProfit;
}
