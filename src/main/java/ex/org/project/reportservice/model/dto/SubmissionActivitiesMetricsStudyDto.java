package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "Study ID", "Study Name", "Center", "Data Files Submitted", "Data Files Approved", "Data Files Rejected" })
public class SubmissionActivitiesMetricsStudyDto extends SubmissionActivitiesMetricsDto {
    @JsonProperty("Study ID")
    @CsvBindByName(column = "Study ID")
    protected String studyId;
    @JsonProperty("Study Name")
    @CsvBindByName(column = "Study Name")
    protected String studyName;
}
