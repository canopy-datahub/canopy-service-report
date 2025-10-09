package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.DatafileHarmonizationMetrics;
import ex.org.project.reportservice.model.DatafileHarmonizationMetricsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatafileHarmonizationRepository extends JpaRepository<DatafileHarmonizationMetrics, Integer> {

    @Query(value = "select dhm.id, dhm.report_id, dhm.orig_file_name, dhm.transform_file_name, " +
            "dhm.study_phs, dhm.center, dhm.orig_variable_count, dhm.transform_variable_count, " +
            "dhm.harmonizable_tier_1_variable_count, dhm.harmonizable_tier_2_variable_count, " +
            "dhm.harmonizable_tier_3_variable_count, dhm.harmonizable_total, " +
            "dhm.harmonized_tier_1_variable_count, dhm.harmonized_tier_2_variable_count, " +
            "dhm.harmonized_tier_3_variable_count, dhm.harmonized_total, " +
            "dhm.harmonizable_tier_1_variables, dhm.harmonizable_tier_2_variables, " +
            "dhm.harmonizable_tier_3_variables, dhm.harmonized_tier_1_variables, " +
            "dhm.harmonized_tier_2_variables, dhm.harmonized_tier_3_variables, " +
            "dhm.orig_variables, dhm.transform_variables, vs.title as study_name " +
            "from public.datafile_harmonization_metrics dhm left join public.view_study vs ON dhm.study_phs = vs.phs " +
            "where dhm.report_id = :reportId", nativeQuery = true)
    List<DatafileHarmonizationMetricsDashboard> findByReportId(@Param("reportId") int reportId);

}
