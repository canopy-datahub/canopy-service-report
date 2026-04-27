package org.canopyplatform.canopy.reportservice.repositories;

import org.canopyplatform.canopy.reportservice.model.MetricsReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetricsReportTypeRepository extends JpaRepository<MetricsReportType, Integer> {

    Optional<MetricsReportType> findByName(String name);

}
