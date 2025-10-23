package ex.org.project.reportservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "metrics_report")
@NoArgsConstructor
@AllArgsConstructor
public class MetricsReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "report_date")
	private LocalDate reportDate;

	@ManyToOne
	@JoinColumn(name = "type_id")
	private MetricsReportType type;

}
