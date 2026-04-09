package ex.org.project.reportservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "view_user_population")
@NoArgsConstructor
public class ViewUserPopulation {
    @Id
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "orcid_id")
    private String orcidId;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "user_researcher_level")
    private String userResearchLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLogin;

    @Column(name = "total_login")
    private Integer totalLogin;

    @Column(name = "last_download_at")
    private String lastDownloadAt;

    @Column(name = "user_institution_type")
    private String institutionType;

    @Column(name = "user_state")
    private String state;

    @Column(name = "user_country")
    private String country;

    @Column(name = "province_region")
    private String province;

    @Column(name = "is_for_profit")
    private Boolean isForProfit;

    @Column(name = "internal_user")
    private Boolean internalUser;

    @Column(name = "has_workbench")
    private Boolean hasWorkbench;

    @Column(name = "has_downloaded_data")
    private Boolean downloadedData;

    @Column(name = "workspace_count")
    private Integer workspaceCount;

    public String getForProfit(){
        if (isForProfit){
            return "Profit";
        }
        return "Not for Profit";
    }
}
