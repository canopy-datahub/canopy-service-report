package org.canopyplatform.canopy.reportservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.canopyplatform.canopy.reportservice.model.MetricsReport;

@Repository
public interface MetricsReportRepository extends JpaRepository<MetricsReport, Integer> {

	List<MetricsReport> findByTypeName(String name);

	List<MetricsReport> findByIdInAndTypeName(List<Integer> ids, String name);
}
