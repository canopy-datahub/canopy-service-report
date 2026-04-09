package ex.org.project.reportservice.model.dto;

import java.time.Month;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportMonthDTO {
	Month month;
	List<ReportDateDTO> reports;
}
