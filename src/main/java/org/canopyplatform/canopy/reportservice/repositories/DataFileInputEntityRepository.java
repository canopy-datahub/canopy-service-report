package org.canopyplatform.canopy.reportservice.repositories;

import org.canopyplatform.canopy.reportservice.model.DataFileInputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataFileInputEntityRepository extends JpaRepository<DataFileInputEntity, Integer> {

    @Query(nativeQuery = true,
            value = "select df.id, sf.file_name as file_name, vs.center as program, vs.study_id as study_id, "
                    + "ldfc.\"name\" as category, df.file_headers as variable_names from data_file df "
                    + "join data_submission ds on df.submission_id = ds.id "
                    + "join view_study vs on ds.study_id = vs.study_id "
                    + "join lkup_data_file_category ldfc on df.file_category_id = ldfc.id "
                    + "join s3_file sf on df.s3_file_id = sf.id "
                    + "where (ldfc.\"name\" = 'Tabular Data - Harmonized' or ldfc.\"name\" = 'Tabular Data - Non-harmonized')"
                    + "and df.is_current_version is true and sf.file_name ~ '.*(_v\\d+).*' "
                    + "and sf.file_name not like '%.json'"
    )
    List<DataFileInputEntity> findCurrentFiles();
}
