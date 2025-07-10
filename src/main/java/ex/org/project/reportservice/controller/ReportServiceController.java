package ex.org.project.reportservice.controller;

import ex.org.project.reportservice.model.dto.*;
import ex.org.project.reportservice.auth.AccessRole;
import ex.org.project.reportservice.auth.UserAuthService;
import ex.org.project.reportservice.service.AWSStorageService;
import ex.org.project.reportservice.service.UserPopulationMetricsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import ex.org.project.reportservice.service.MetricsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportServiceController {

	private final MetricsService metricsService;
	private final UserPopulationMetricsService userPopulationMetricsService;
//	private final HarmonizationMetricsCalculator harmonizationMetricsCalculator;
	private final UserAuthService authService;
	private final AWSStorageService awsStorageService;

	@GetMapping("/hubContentReportDates")
	public ResponseEntity<List<ReportYearDTO>> getHubContentReportDates(@CookieValue(value="chocolateChip", required = false) String sessionId) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getHubContentWeeklyReportOptions());
	}

	@GetMapping("/hubContent")
	public ResponseEntity<HubContentAggMetricsResponse> getHubContent(
			@CookieValue(value="chocolateChip", required = false) String sessionId,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") Integer reportId) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.createReport(aggBy, reportId));
	}

	@GetMapping("/userActivities")
	public ResponseEntity<UserActivitiesDto> getUserActivitiesMetrics(
			@CookieValue(value="chocolateChip", required = false) String sessionId,
			@RequestParam(defaultValue = "country") String dimension,
			@RequestParam(defaultValue = "30daysAgo") String startDate,
			@RequestParam(defaultValue = "today") String endDate) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getUserActivitiesMetrics(dimension, startDate, endDate));
	}

	@GetMapping("/userActivitiesCSV")
	public void getUserActivitiesMetricsCSV(
			@RequestParam(value="sessionId", required = false) String sessionId, HttpServletResponse response,
			@RequestParam(defaultValue = "country") String dimension,
			@RequestParam(defaultValue = "30daysAgo") String startDate,
			@RequestParam(defaultValue = "today") String endDate){
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		metricsService.getUserActivitiesMetricsCSV(response, dimension, startDate, endDate);
	}

	@GetMapping("/download/hubContent")
	public void exportHubContentToCSV(
			@RequestParam(value="sessionId", required = false) String sessionId, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") Integer reportId) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		metricsService.getHubContentReport(response, aggBy, reportId);
	}

    @GetMapping("/userMetricsByAggregate")
    public ResponseEntity<UserMetricsResponse> getUsersByInstitutionType(
			@CookieValue(value="chocolateChip", required = false) String sessionId,
            @RequestParam("aggBy") String aggBy,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
        return ResponseEntity.ok(userPopulationMetricsService.getUserMetricsByAggregate(aggBy, startDate, endDate));
    }

	@GetMapping("/userMetricsCSV")
	public void getUserPopulationMetricsCSV(
			@RequestParam(value="sessionId", required = false) String sessionId, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		userPopulationMetricsService.getUserPopulationMetricsCSV(response, aggBy, startDate, endDate);
	}


	@GetMapping("/submissionMetricsByAggregate")
	public ResponseEntity<SubmissionActivitiesMetricsResponse> getSubmissionActivitiesMetrics(
			@CookieValue(value="chocolateChip", required = false) String sessionId,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate){
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.submissionActivitiesMetrics(aggBy, startDate, endDate));
	}

	@GetMapping("/submissionMetricsCSV")
	public void getSubmissionActivitiesMetricsCSV(
			@RequestParam(value="sessionId", required = false) String sessionId, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		metricsService.submissionActivitiesMetricsCSV(response, aggBy, startDate, endDate);
	}

	@GetMapping("/getDataHarmonizationReportIds")
	public ResponseEntity<List<ReportYearDTO>> getHarmonizationReportIds(@CookieValue(value="chocolateChip", required = false) String sessionId) {
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getDataHarmonizationReportIds());
	}

	@GetMapping("/getHarmonizationMetrics")
	public ResponseEntity<HarmonizationMetricsResponse> getHarmonizationMetrics(
			@CookieValue(value="chocolateChip", required = false) String sessionId,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") int reportId){
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		return ResponseEntity.ok(metricsService.getHarmonizationMetrics(aggBy, reportId));
	}


	@GetMapping("/getHarmonizationMetricsCSV")
	public void getHarmonizationMetricsCSV(
			@RequestParam(value="sessionId", required = false) String sessionId, HttpServletResponse response,
			@RequestParam("aggBy") String aggBy,
			@RequestParam("reportId") int reportId){
		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
		metricsService.getHarmonizationMetricsCSV(aggBy, response, reportId);
	}

	@GetMapping("/download/getWeeklyStudyByFileReport")
	public ResponseEntity<Object> getWeeklyStudyByFileReport(@RequestParam(value="sessionId", required = false) String sessionId) {
		authService.checkAuth(sessionId, List.of(AccessRole.DATA_CURATOR));
		return awsStorageService.downloadWeeklyFileReport();
	}


	//Leaving here in case we need to manually run the job locally
//	@PostMapping("/runHarmonizationMetricsJob")
//	public ResponseEntity<Void> runHarmonizationMetricsJob(@CookieValue("chocolateChip") String sessionId) {
//		authService.checkAuth(sessionId, List.of(AccessRole.OFFICER));
//		harmonizationMetricsCalculator.generateHarmonizationMetricsReport();
//		return ResponseEntity.ok().build();
//	}
}
