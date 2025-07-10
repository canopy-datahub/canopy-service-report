package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.StudyByFileReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeeklyHubContentRepository extends JpaRepository<StudyByFileReport, Integer> {

    List<StudyByFileReport> findAllByOrderByDccAsc();
}
