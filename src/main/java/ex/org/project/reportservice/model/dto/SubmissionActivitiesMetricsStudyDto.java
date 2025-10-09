package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "PHS", "Study Name", "Center", "Data Files Submitted", "Data Files Approved", "Data Files Rejected" })
public class SubmissionActivitiesMetricsStudyDto extends SubmissionActivitiesMetricsDto {
    @JsonProperty("PHS")
    @CsvBindByName(column = "PHS")
    protected String phs;
    @JsonProperty("Study Name")
    @CsvBindByName(column = "Study Name")
    protected String studyName;
}
