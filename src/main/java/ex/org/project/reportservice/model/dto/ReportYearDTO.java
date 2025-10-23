package ex.org.project.reportservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReportYearDTO {
	Integer year;
	List<ReportMonthDTO> months;
}
