package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "PHS", "Study Name", "DCC", "Files", "Variables", "Harmonizable Variables (Tier 1)",
        "Harmonized Variables (Tier 1)", "Harmonizable Variables (Tier 2)", "Harmonized Variables (Tier 2)",
        "Total Harmonizable", "Total Harmonized"})
public class StudyHarmonizationMetricsDTO extends HarmonizationMetricsDTO {

    @JsonProperty("Files")
    @CsvBindByName(column = "Files")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer numberOfFiles;

    @JsonProperty("Variables")
    @CsvBindByName(column = "Variables Count")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer uniqueVariables;

    @CsvBindByName(column = "Variables")
    private String allVariables;

}
