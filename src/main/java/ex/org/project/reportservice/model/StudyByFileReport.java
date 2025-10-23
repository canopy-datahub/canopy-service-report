package ex.org.project.reportservice.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Table(name = "view_current_hub_content_data")
@NoArgsConstructor
public class StudyByFileReport {

    @Id
    @Column(name = "id")
    private Integer fileId;

    @CsvBindByName(column = "Study Program")
    @CsvBindByPosition(position = 0)
    @Column(name = "dcc")
    private String dcc;

    @CsvBindByName(column = "Study PHS")
    @CsvBindByPosition(position = 1)
    @Column(name = "study_phs")
    private String studyPhs;

    @CsvBindByName(column = "Study Title")
    @CsvBindByPosition(position = 2)
    @Column(name = "study_title")
    private String studyTitle;

    @CsvBindByName(column = "Study Status")
    @CsvBindByPosition(position = 3)
    @Column(name = "study_status")
    private String studyStatus;

    @Getter(AccessLevel.NONE)
    @CsvBindByName(column = "Study Create Date")
    @CsvBindByPosition(position = 4)
    @Column(name = "study_create_date")
    private LocalDateTime studyCreatedDate;

    @CsvBindByName(column = "Submission ID")
    @Column(name = "submission_id")
    @CsvBindByPosition(position = 5)
    private Integer submissionId;

    @Getter(AccessLevel.NONE)
    @CsvBindByName(column = "Submission Create Date")
    @Column(name = "submission_created_date")
    @CsvBindByPosition(position = 6)
    private LocalDateTime submissionCreatedDate;

    @CsvBindByName(column = "Submission Status")
    @Column(name = "submission_status")
    @CsvBindByPosition(position = 7)
    private String submissionStatus;

    @CsvBindByName(column = "File Name")
    @CsvBindByPosition(position = 8)
    @Column(name = "file_name")
    private String fileName;

    @CsvBindByName(column = "File Version")
    @CsvBindByPosition(position = 9)
    @Column(name = "version_no")
    private String fileVersion;

    @CsvBindByName(column = "File Category")
    @CsvBindByPosition(position = 10)
    @Column(name = "file_category")
    private String fileCategory;

    @CsvBindByName(column = "File Status")
    @CsvBindByPosition(position = 11)
    @Column(name = "file_status")
    private String fileStatus;

    @Getter(AccessLevel.NONE)
    @CsvBindByName(column = "File Create Date")
    @CsvBindByPosition(position = 12)
    @Column(name = "file_create_date")
    private LocalDateTime fileCreatedAt;

    @CsvBindByName(column = "File Size")
    @CsvBindByPosition(position = 13)
    @Column(name = "file_size")
    private Integer fileSize;

    public String getStudyCreatedDate() {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = studyCreatedDate.format(myFormatObj);
        return formattedDate;
    }

    public String getFileCreatedAt() {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedDate = "";
        if(fileCreatedAt!=null){
            formattedDate =fileCreatedAt.format(myFormatObj);
        }
        return formattedDate;
    }

    public String getSubmissionCreatedDate() {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedDate = "";
        if(submissionCreatedDate!=null){
            formattedDate =submissionCreatedDate.format(myFormatObj);
        }
        return formattedDate;
    }
}
