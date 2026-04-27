package org.canopyplatform.canopy.reportservice.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportYearDTO {
	Integer year;
	List<ReportMonthDTO> months;
}
