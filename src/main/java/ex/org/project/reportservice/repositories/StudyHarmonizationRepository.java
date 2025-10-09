package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.StudyHarmonizationMetrics;
import ex.org.project.reportservice.model.StudyHarmonizationMetricsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyHarmonizationRepository extends JpaRepository<StudyHarmonizationMetrics, Integer> {

    @Query(value = "select shm.id, shm.report_id, shm.study_phs, shm.center, " +
            "shm.orig_transform_pairs_count, shm.variable_count, " +
            "shm.harmonizable_tier_1_variable_count, shm.harmonizable_tier_2_variable_count, " +
            "shm.harmonizable_tier_3_variable_count, shm.harmonizable_total, " +
            "shm.harmonized_tier_1_variable_count, shm.harmonized_tier_2_variable_count, " +
            "shm.harmonized_tier_3_variable_count, shm.harmonized_total, " +
            "shm.variables, shm.harmonizable_tier_1_variables, " +
            "shm.harmonizable_tier_2_variables, shm.harmonizable_tier_3_variables, " +
            "shm.harmonized_tier_1_variables, shm.harmonized_tier_2_variables, " +
            "shm.harmonized_tier_3_variables, vs.title as study_name " +
            "from public.study_harmonization_metrics shm left join public.view_study vs ON shm.study_phs = vs.phs " +
            "where shm.report_id = :reportId", nativeQuery = true)
    List<StudyHarmonizationMetricsDashboard> findByReportId(@Param("reportId") int reportId);

}
