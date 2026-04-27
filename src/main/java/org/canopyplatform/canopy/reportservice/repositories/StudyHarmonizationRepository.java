package org.canopyplatform.canopy.reportservice.repositories;

import org.canopyplatform.canopy.reportservice.model.StudyHarmonizationMetrics;
import org.canopyplatform.canopy.reportservice.model.StudyHarmonizationMetricsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyHarmonizationRepository extends JpaRepository<StudyHarmonizationMetrics, Integer> {

    @Query(value = "select shm.id, shm.report_id, shm.study_id, shm.center, " +
            "shm.orig_transform_pairs_count, shm.variable_count, " +
            "shm.harmonizable_tier_1_variable_count," +
            "shm.harmonized_tier_1_variable_count, " +
            "shm.variables, shm.harmonizable_tier_1_variables, " +
            "shm.harmonized_tier_1_variables, " +
            "vs.title as study_name " +
            "from public.study_harmonization_metrics shm left join public.view_study vs ON shm.study_id = vs.study_id " +
            "where shm.report_id = :reportId", nativeQuery = true)
    List<StudyHarmonizationMetricsDashboard> findByReportId(@Param("reportId") int reportId);

}
