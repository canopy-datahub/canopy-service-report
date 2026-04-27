package org.canopyplatform.canopy.reportservice.repositories;

import org.canopyplatform.canopy.reportservice.model.StudyByFileReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeeklyHubContentRepository extends JpaRepository<StudyByFileReport, Integer> {

    List<StudyByFileReport> findAllByOrderByCenterAsc();
}
