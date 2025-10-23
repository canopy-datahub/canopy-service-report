package ex.org.project.reportservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Month;
import java.util.List;

@Data
@AllArgsConstructor
public class ReportMonthDTO {
	Month month;
	List<ReportDateDTO> reports;
}
