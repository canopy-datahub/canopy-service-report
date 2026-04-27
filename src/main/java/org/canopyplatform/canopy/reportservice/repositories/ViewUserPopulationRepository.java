package org.canopyplatform.canopy.reportservice.repositories;

import org.canopyplatform.canopy.reportservice.model.ViewUserPopulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewUserPopulationRepository extends JpaRepository<ViewUserPopulation, Integer> {

    List<ViewUserPopulation> findByCreatedAtLessThan(LocalDateTime endDate);

}
