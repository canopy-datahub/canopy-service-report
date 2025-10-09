package ex.org.project.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionActivitiesMetricsDto {

    @JsonProperty("Center")
    @CsvBindByName(column = "Center")
    protected String center;
    @JsonProperty("Data Files Submitted")
    @CsvBindByName(column = "Data Files Submitted")
    protected Integer dataFilesSubmitted;
    @JsonProperty("Data Files Approved")
    @CsvBindByName(column = "Data Files Approved")
    protected Integer dataFilesApproved;
    @JsonProperty("Data Files Rejected")
    @CsvBindByName(column = "Data Files Rejected")
    protected Integer dataFilesRejected;
}
