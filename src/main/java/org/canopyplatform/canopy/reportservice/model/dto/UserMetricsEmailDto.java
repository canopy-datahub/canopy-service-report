package org.canopyplatform.canopy.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static org.canopyplatform.canopy.reportservice.model.populationMetrics.UserPopulationMetricsColumns.*;

@Getter
@Setter
@JsonPropertyOrder({NAME, EMAIL, ORCID_ID, INSTITUTION, USER_LEVEL, JOB_TITLE, CREATED_AT, LAST_LOGIN})
@NoArgsConstructor
public class UserMetricsEmailDto extends UserMetricsDto {

    @JsonProperty(NAME)
    @CsvBindByName(column = NAME)
    protected String name;

    @JsonProperty(EMAIL)
    @CsvBindByName(column = EMAIL)
    protected String email;

    @JsonProperty(ORCID_ID)
    @CsvBindByName(column = ORCID_ID)
    protected String orcidId;

    @JsonProperty(INSTITUTION)
    @CsvBindByName(column = INSTITUTION)
    protected String institution;


    @JsonProperty(INSTITUTION_TYPE)
    @CsvBindByName(column = INSTITUTION_TYPE)
    protected String institutionType;

    @JsonProperty(USER_LEVEL)
    @CsvBindByName(column = USER_LEVEL)
    protected String userResearcherLevel;

    @JsonProperty(USER_LOCATION_STATE)
    @CsvBindByName(column = USER_LOCATION_STATE)
    protected String userState;

    @JsonProperty(USER_LOCATION_COUNTRY)
    @CsvBindByName(column = USER_LOCATION_COUNTRY)
    protected String userCountry;

    @JsonProperty(JOB_TITLE)
    @CsvBindByName(column = JOB_TITLE)
    protected String jobTitle;

    @JsonProperty(CREATED_AT)
    @CsvBindByName(column = CREATED_AT)
    protected String createdAt;

    @JsonProperty(LAST_LOGIN)
    @CsvBindByName(column = LAST_LOGIN)
    protected String lastLogin;

    @JsonProperty(TOTAL_LOGIN)
    @CsvBindByName(column = TOTAL_LOGIN)
    protected Integer totalLogin;

    @JsonProperty(INTERNAL_USER)
    @CsvBindByName(column = INTERNAL_USER)
    protected String internalUser;

    @JsonProperty(DOWNLOADED_DATA)
    @CsvBindByName(column = DOWNLOADED_DATA)
    protected String downloadedData;

    private String dateToString(LocalDateTime dateTime){
        if(dateTime == null){
            return "";
        }
        else {
            return dateTime.toString();
        }
    }
}
