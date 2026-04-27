package org.canopyplatform.canopy.reportservice.repositories;

import org.canopyplatform.canopy.reportservice.model.SubmissionActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface SubmissionActivityRepository extends JpaRepository<SubmissionActivity, Integer> {

    @Query(value = "select center1.*, center2.data_files_rejected from " +
            "(select b.center, " +
            "count(distinct study_id) filter (where study_initiated_date > :startDate and " +
            "study_initiated_date < :endDate) as studies_initiated, " +
            "count(distinct study_id) filter (where study_published_date > :startDate and " +
            "study_published_date < :endDate) as studies_published, " +
            "count(distinct data_file_id) filter (where  data_file_created_date > :startDate and " +
            "data_file_created_date < :endDate) as data_files_submitted, " +
            "count(distinct data_file_id) filter (where  data_file_approval_date > :startDate and " +
            "data_file_approval_date < :endDate) as data_files_approved " +
            "from view_submission_activity b " +
            "group by b.center) as center1 " +
            "left outer join " +
            "(select a.center, sum(file_rejected_count) as data_files_rejected from " +
            "(select center, data_submission_id, files_rejected_date, file_rejected_count " +
            "from view_submission_activity b " +
            "where file_rejected_count is not null and files_rejected_date > :startDate and " +
            "files_rejected_date < :endDate " +
            "group by center,data_submission_id, files_rejected_date,file_rejected_count) a " +
            "group by a.center) center2 on center1.center=center2.center", nativeQuery = true)
    List<Map<String, Object>> findDccActivityMetrics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "select study1.*, study2.data_files_rejected from " +
            "(select b.study_id, b.study_name, b.center, " +
            "count(distinct study_id) filter (where study_initiated_date > :startDate and " +
            "study_initiated_date < :endDate) as studies_initiated, " +
            "count(distinct study_id) filter (where study_published_date > :startDate and " +
            "study_published_date < :endDate) as studies_published, " +
            "count(distinct data_file_id) filter (where  data_file_created_date > :startDate and " +
            "data_file_created_date < :endDate) as data_files_submitted, " +
            "count(distinct data_file_id) filter (where  data_file_approval_date > :startDate and " +
            "data_file_approval_date < :endDate) as data_files_approved " +
            "from view_submission_activity b " +
            "group by  b.study_id, b.study_name, b.center) as study1 " +
            "left outer join " +
            "(select a.study_id, a.study_name, a.center, sum(file_rejected_count) as data_files_rejected from " +
            "(select study_id, study_name, center, data_submission_id, files_rejected_date, file_rejected_count " +
            "from view_submission_activity b " +
            "where file_rejected_count is not null and files_rejected_date > :startDate and " +
            "files_rejected_date < :endDate " +
            "group by study_id, study_name, center,data_submission_id, files_rejected_date,file_rejected_count) a " +
            "group by a.study_id, a.study_name, a.center) study2 on study1.study_id=study2.study_id", nativeQuery = true)
    List<Map<String, Object>> findStudyActivityMetrics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
