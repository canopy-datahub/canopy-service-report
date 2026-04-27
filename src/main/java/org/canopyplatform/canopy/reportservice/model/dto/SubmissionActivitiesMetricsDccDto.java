package org.canopyplatform.canopy.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "Center", "Studies Initiated", "Studies Published", "Data Files Submitted", "Data Files Approved",
        "Data Files Rejected" })
public class SubmissionActivitiesMetricsDccDto extends SubmissionActivitiesMetricsDto {
    @JsonProperty("Studies Initiated")
    @CsvBindByName(column = "Studies Initiated")
    protected Integer studiesInitiated;
    @JsonProperty("Studies Published")
    @CsvBindByName(column = "Studies Published")
    protected Integer studiesPublished;
}
