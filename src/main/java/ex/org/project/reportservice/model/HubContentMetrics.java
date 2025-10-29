package ex.org.project.reportservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "hub_content_metrics")
@NoArgsConstructor
@AllArgsConstructor
public class HubContentMetrics {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "report_id")
	private Integer reportId;

	@Column(name = "center")
	private String center;

	@Column(name = "study_phs")
	private String studyPhs;

	@Transient
	private Integer countStudyPhs;

	@Column(name = "study_title")
	private String studyTitle;

	@Column(name = "study_status")
	private String studyStatus;

	@Column(name = "total_file_count")
	private Integer totalFileCount;

	@Column(name = "data_file_count")
	private Integer dataFileCount;

	@Column(name = "total_file_size")
	private Double totalFileSize;

	@Column(name = "orig_data_file_count")
	private Integer origDataFileCount;

	@Column(name = "standardized_data_file_count")
	private Integer standardizedDataFileCount;

	@Column(name = "metadata_file_count")
	private Integer metadataFileCount;

	@Column(name = "dictionary_file_count")
	private Integer dictionaryFileCount;

	@Column(name = "readme_file_count")
	private Integer readmeFileCount;

	@Column(name = "other_file_count")
	private Integer otherFileCount;

	@Column(name = "study_has_data_file")
	private Boolean hasDataFile;

	@Transient
	private Integer countStudyHasDataFile;

	public HubContentMetrics(String center, Integer countStudyPhs, Double totalFileSize, Integer totalFileCount,
													 Integer dataFileCount, Integer origDataFileCount, Integer standardizedDataFileCount,
													 Integer metadataFileCount, Integer dictionaryFileCount, Integer readmeFileCount, Integer otherFileCount,
													 Integer countStudyHasDataFile) {
		this.center = center;
		this.countStudyPhs = countStudyPhs;
		this.totalFileCount = totalFileCount;
		this.dataFileCount = dataFileCount;
		this.totalFileSize = totalFileSize;
		this.origDataFileCount = origDataFileCount;
		this.standardizedDataFileCount = standardizedDataFileCount;
		this.metadataFileCount = metadataFileCount;
		this.dictionaryFileCount = dictionaryFileCount;
		this.readmeFileCount = readmeFileCount;
		this.otherFileCount = otherFileCount;
		this.countStudyHasDataFile = countStudyHasDataFile;
	}

}
