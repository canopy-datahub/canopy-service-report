package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.MetricsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricsReportRepository extends JpaRepository<MetricsReport, Integer> {

	List<MetricsReport> findByTypeName(String name);

	List<MetricsReport> findByIdInAndTypeName(List<Integer> ids, String name);
}
