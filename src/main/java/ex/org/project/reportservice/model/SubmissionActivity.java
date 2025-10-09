package ex.org.project.reportservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "view_submission_activity")
@NoArgsConstructor
public class SubmissionActivity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "study_id")
    private Integer studyId;

    @Column(name = "center")
    private String center;

    @Column(name = "study_phs")
    private String studyPhs;

    @Column(name = "study_name")
    private String studyName;

    @Column(name = "study_initiated_date")
    private LocalDateTime studyInitiatedDate;

    @Column(name = "study_published_date")
    private LocalDateTime studyPublishedDate;

    @Column(name = "data_file_id")
    private Integer dataFileId;

    @Column(name = "data_file_created_date")
    private LocalDateTime dataFileCreatedDate;

    @Column(name = "data_file_approval_date")
    private LocalDateTime dataFileApprovalDate;

    @Column(name = "data_file_reject_date")
    private LocalDateTime dataFileRejectDate;
}
