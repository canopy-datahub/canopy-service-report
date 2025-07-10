package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "studyPhs", "studyName" })
public class StudyPhsDto extends HubContentAggMetricsDto {

	@JsonProperty("Study Name")
	@CsvBindByName(column = "Study Name")
	private String studyName;

	@JsonProperty("Study ID")
	@CsvBindByName(column = "Study ID")
	private String studyPhs;
}
