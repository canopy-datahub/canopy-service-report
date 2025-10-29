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
@JsonPropertyOrder({ "Center", "Total of Studies", "Studies with Data" })
public class CenterDto extends HubContentAggMetricsDto {

	@JsonProperty("Center")
	@CsvBindByName(column = "Center")
	private String center;

	@JsonProperty("Total Studies")
	@CsvBindByName(column = "Total Studies")
	private Integer totalStudies;

	@JsonProperty("Studies with Data")
	@CsvBindByName(column = "Studies with Data")
	private Integer studiesWithData;
}

