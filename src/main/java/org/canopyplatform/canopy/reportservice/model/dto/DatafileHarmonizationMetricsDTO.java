package org.canopyplatform.canopy.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "File Name (Orig)", "File Name (Trans)", "Study ID", "Study Name", "Center", "Variables (Orig)",
        "Variables (Trans)",  "Harmonizable Variables (Tier 1)", "Harmonized Variables (Tier 1)"})
public class DatafileHarmonizationMetricsDTO extends HarmonizationMetricsDTO {
    @JsonProperty("File Name (Orig)")
    @CsvBindByName(column = "File Name (Orig)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String originalFileName;

    @JsonProperty("File Name (Trans)")
    @CsvBindByName(column = "File Name (Trans)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String transformFileName;

    @JsonProperty("Variables (Orig)")
    @CsvBindByName(column = "Variables Count (Orig)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer originalUniqueVariables;

    @JsonProperty("Variables (Trans)")
    @CsvBindByName(column = "Variables Count (Trans)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer transformUniqueVariables;

    @CsvBindByName(column = "Original Variables")
    private String origVariables;

    @CsvBindByName(column = "Transform Variables")
    private String transformVariables;
}
