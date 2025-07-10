package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.StudyHarmonizationMetrics;
import ex.org.project.reportservice.model.StudyHarmonizationMetricsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyHarmonizationRepository extends JpaRepository<StudyHarmonizationMetrics, Integer> {

    @Query(value = "select shm.*, vs.title as study_name " +
            "from public.study_harmonization_metrics shm left join public.view_study vs ON shm.study_phs = vs.phs " +
            "where shm.report_id = :reportId", nativeQuery = true)
    List<StudyHarmonizationMetricsDashboard> findByReportId(@Param("reportId") int reportId);

}
