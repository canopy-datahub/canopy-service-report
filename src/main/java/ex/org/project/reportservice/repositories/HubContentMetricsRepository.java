package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.HubContentMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HubContentMetricsRepository extends JpaRepository<HubContentMetrics, Integer> {


    List<HubContentMetrics> findByStudyStatusAndHasDataFileAndReportId(String studyStatus, Boolean hasDataFile, Integer reportId);

    @Query("SELECT new ex.org.project.reportservice.model.HubContentMetrics(hcm.dcc, CAST(COUNT (hcm.studyPhs) as integer),SUM(hcm.totalFileSize),"  +
            " CAST(SUM(hcm.totalFileCount) as integer),CAST(SUM(hcm.dataFileCount)as integer), CAST(SUM(hcm.origDataFileCount)as integer)," +
            "CAST(SUM(hcm.standardizedDataFileCount)as integer),CAST(SUM(hcm.metadataFileCount)as integer), CAST(SUM(hcm.dictionaryFileCount)as integer), " +
            "CAST(SUM(hcm.readmeFileCount)as integer),CAST(SUM(hcm.otherFileCount)as integer) ,CAST(COUNT(case when hcm.hasDataFile then 1 end)as integer)) " +
            "FROM HubContentMetrics hcm WHERE hcm.reportId = :reportId group by hcm.dcc")
    List<HubContentMetrics> findTotalFileSizeByDccAndReportId(Integer reportId);

    @Query(value = "Select distinct(report_id) from public.hub_content_metrics", nativeQuery = true)
    List<Integer> findDistinctReportId();
}
