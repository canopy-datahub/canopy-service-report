package ex.org.project.reportservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ex.org.project.reportservice.model.MetricsReport;

@Repository
public interface MetricsReportRepository extends JpaRepository<MetricsReport, Integer> {
	
	List<MetricsReport> findByTypeName(String name);

	List<MetricsReport> findByIdInAndTypeName(List<Integer> ids, String name);
}
