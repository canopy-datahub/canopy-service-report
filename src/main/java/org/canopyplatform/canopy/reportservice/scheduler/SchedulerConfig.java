package org.canopyplatform.canopy.reportservice.scheduler;

import org.canopyplatform.canopy.reportservice.service.HarmonizationMetricsCalculator;
import org.canopyplatform.canopy.reportservice.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final HarmonizationMetricsCalculator harmonizationMetricsCalculator;
    private final MetricsService metricsService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduleFixedIntervalTask() {
        harmonizationMetricsCalculator.generateHarmonizationMetricsReport();
    }

    @Scheduled(cron = "0 0 12 * * MON")
    public void uploadWeeklyFileReport(){ metricsService.uploadWeeklyReportToS3(); }
}
