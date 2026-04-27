package org.canopyplatform.canopy.reportservice.controller;

import org.canopyplatform.canopy.reportservice.model.dto.*;
import org.canopyplatform.canopy.reportservice.auth.AccessRole;
import org.canopyplatform.canopy.reportservice.auth.core.KeycloakAuthenticationService;
import org.canopyplatform.canopy.reportservice.model.dto.*;
import org.canopyplatform.canopy.reportservice.service.AWSStorageService;
import org.canopyplatform.canopy.reportservice.service.HarmonizationMetricsCalculator;
import org.canopyplatform.canopy.reportservice.service.UserPopulationMetricsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import org.canopyplatform.canopy.reportservice.service.MetricsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportServiceController {

	private final MetricsService metricsService;
	private final UserPopulationMetricsService userPopulationMetricsService;
	private final HarmonizationMetricsCalculator harmonizationMetricsCalculator;
  private final KeycloakAuthenticationService authenticationService;
	private final AWSStorageService awsStorageService;

	@GetMapping("/hubContentReportDates")
	public ResponseEntity<List<ReportYearDTO>> getHubContentReportDates(@AuthenticationPrincipal Jwt jwt) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getHubContentWeeklyReportOptions());
	}

	@GetMapping("/hubContent")
	public ResponseEntity<HubContentAggMetricsResponse> getHubContent(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") Integer reportId) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.createReport(aggBy, reportId));
	}

	@GetMapping("/userActivities")
	public ResponseEntity<UserActivitiesDto> getUserActivitiesMetrics(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(defaultValue = "country") String dimension,
			@RequestParam(defaultValue = "30daysAgo") String startDate,
			@RequestParam(defaultValue = "today") String endDate) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getUserActivitiesMetrics(dimension, startDate, endDate));
	}

	@GetMapping("/userActivitiesCSV")
	public void getUserActivitiesMetricsCSV(
      @AuthenticationPrincipal Jwt jwt, HttpServletResponse response,
			@RequestParam(defaultValue = "country") String dimension,
			@RequestParam(defaultValue = "30daysAgo") String startDate,
			@RequestParam(defaultValue = "today") String endDate){
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		metricsService.getUserActivitiesMetricsCSV(response, dimension, startDate, endDate);
	}

	@GetMapping("/download/hubContent")
	public void exportHubContentToCSV(
      @AuthenticationPrincipal Jwt jwt, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") Integer reportId) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		metricsService.getHubContentReport(response, aggBy, reportId);
	}

    @GetMapping("/userMetricsByAggregate")
    public ResponseEntity<UserMetricsResponse> getUsersByInstitutionType(
			@AuthenticationPrincipal Jwt jwt,
            @RequestParam("aggBy") String aggBy,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
        return ResponseEntity.ok(userPopulationMetricsService.getUserMetricsByAggregate(aggBy, startDate, endDate));
    }

	@GetMapping("/userMetricsCSV")
	public void getUserPopulationMetricsCSV(
      @AuthenticationPrincipal Jwt jwt, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		userPopulationMetricsService.getUserPopulationMetricsCSV(response, aggBy, startDate, endDate);
	}


	@GetMapping("/submissionMetricsByAggregate")
	public ResponseEntity<SubmissionActivitiesMetricsResponse> getSubmissionActivitiesMetrics(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate){
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.submissionActivitiesMetrics(aggBy, startDate, endDate));
	}

	@GetMapping("/submissionMetricsCSV")
	public void getSubmissionActivitiesMetricsCSV(
      @AuthenticationPrincipal Jwt jwt, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		metricsService.submissionActivitiesMetricsCSV(response, aggBy, startDate, endDate);
	}

	@GetMapping("/getDataHarmonizationReportIds")
	public ResponseEntity<List<ReportYearDTO>> getHarmonizationReportIds(@AuthenticationPrincipal Jwt jwt) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getDataHarmonizationReportIds());
	}

	@GetMapping("/getHarmonizationMetrics")
	public ResponseEntity<HarmonizationMetricsResponse> getHarmonizationMetrics(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") int reportId){
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getHarmonizationMetrics(aggBy, reportId));
	}


	@GetMapping("/getHarmonizationMetricsCSV")
	public void getHarmonizationMetricsCSV(
      @AuthenticationPrincipal Jwt jwt, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") int reportId){
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		metricsService.getHarmonizationMetricsCSV(aggBy, response, reportId);
	}

	@GetMapping("/download/getWeeklyStudyByFileReport")
	public ResponseEntity<Object> getWeeklyStudyByFileReport(@AuthenticationPrincipal Jwt jwt) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.DATA_CURATOR));
		return awsStorageService.downloadWeeklyFileReport();
	}


	//Leaving here in case we need to manually run the job locally
	@PostMapping("/runHarmonizationMetricsJob")
	public ResponseEntity<Integer> runHarmonizationMetricsJob(@AuthenticationPrincipal Jwt jwt) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		Integer reportId = harmonizationMetricsCalculator.generateHarmonizationMetricsReport();
		return ResponseEntity.ok(reportId);
	}

	//Manual trigger for weekly file report upload
	@PostMapping("/uploadWeeklyFileReport")
	public ResponseEntity<String> uploadWeeklyFileReport(@AuthenticationPrincipal Jwt jwt) {
		authenticationService.checkAuth(jwt, List.of(AccessRole.OFFICER));
		metricsService.uploadWeeklyReportToS3();
		return ResponseEntity.ok("Weekly file report uploaded successfully");
	}
}
