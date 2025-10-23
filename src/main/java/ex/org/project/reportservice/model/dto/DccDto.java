package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString(callSuper = true)
@JsonPropertyOrder({ "DCC", "Total of Studies", "Studies with Data" })
public class DccDto extends HubContentAggMetricsDto {

	@JsonProperty("DCC")
	@CsvBindByName(column = "DCC")
	private String dcc;

	@JsonProperty("Total Studies")
	@CsvBindByName(column = "Total Studies")
	private Integer totalStudies;

	@JsonProperty("Studies with Data")
	@CsvBindByName(column = "Studies with Data")
	private Integer studiesWithData;
}
