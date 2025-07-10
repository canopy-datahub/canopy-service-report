package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.DatafileHarmonizationMetrics;
import ex.org.project.reportservice.model.DatafileHarmonizationMetricsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatafileHarmonizationRepository extends JpaRepository<DatafileHarmonizationMetrics, Integer> {

    @Query(value = "select dhm.*, vs.title as study_name " +
            "from public.datafile_harmonization_metrics dhm left join public.view_study vs ON dhm.study_phs = vs.phs " +
            "where dhm.report_id = :reportId", nativeQuery = true)
    List<DatafileHarmonizationMetricsDashboard> findByReportId(@Param("reportId") int reportId);

}
