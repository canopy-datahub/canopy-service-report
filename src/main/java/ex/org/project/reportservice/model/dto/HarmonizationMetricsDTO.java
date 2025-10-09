package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class HarmonizationMetricsDTO {

    @JsonProperty("PHS")
    @CsvBindByName(column = "PHS")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phs;

    @JsonProperty("Study Name")
    @CsvBindByName(column = "Study Name")
    private String studyName;

    @JsonProperty("Center")
    @CsvBindByName(column = "Center")
    private String center;

    @JsonProperty("Harmonizable Variables (Tier 1)")
    @CsvBindByName(column = "Harmonizable Variables Count (Tier 1)")
    private Integer uniqueHarmonizableVariablesT1;

    @JsonProperty("Harmonizable Variables (Tier 2)")
    @CsvBindByName(column = "Harmonizable Variables Count (Tier 2)")
    private Integer uniqueHarmonizableVariablesT2;

    @JsonProperty("Total Harmonizable")
    @CsvBindByName(column = "Total Harmonizable")
    private BigInteger harmonizable;

    @JsonProperty("Harmonized Variables (Tier 1)")
    @CsvBindByName(column = "Harmonized Variables Count (Tier 1)")
    private Integer uniqueHarmonizedVariablesT1;

    @JsonProperty("Harmonized Variables (Tier 2)")
    @CsvBindByName(column = "Harmonized Variables Count (Tier 2)")
    private Integer uniqueHarmonizedVariablesT2;

    @JsonProperty("Total Harmonized")
    @CsvBindByName(column = "Total Harmonized")
    private BigInteger harmonized;

    @CsvBindByName(column = "Harmonizable Variables (Tier 1)")
    private String harmonizableVariablesT1;

    @CsvBindByName(column = "Harmonizable Variables (Tier 2)")
    private String harmonizableVariablesT2;

    @CsvBindByName(column = "Harmonized Variables (Tier 1)")
    private String harmonizedVariablesT1;

    @CsvBindByName(column = "Harmonized Variables (Tier 2)")
    private String harmonizedVariablesT2;

}
