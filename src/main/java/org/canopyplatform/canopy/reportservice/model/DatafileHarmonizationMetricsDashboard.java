package org.canopyplatform.canopy.reportservice.model;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class DatafileHarmonizationMetricsDashboard extends DatafileHarmonizationMetrics {

    @Column(name = "study_name")
    private String studyName;

    public DatafileHarmonizationMetricsDashboard(Integer id, Integer reportId, String originalFileName,
                                                 String transformFileName, String studyId, String center,
                                                 Integer origVariableCount, Integer transformVariableCount,
                                                 Integer harmonizableTier1VariableCount,
                                                 Integer harmonizedTier1VariableCount,
                                                 String harmonizableTier1Variables,
                                                 String harmonizedTier1Variables,
                                                 String origVariables,
                                                 String transformVariables,
                                                 String studyName) {
        super(id, reportId, originalFileName, transformFileName, studyId, center, origVariableCount,
            transformVariableCount, harmonizableTier1VariableCount, harmonizedTier1VariableCount,origVariables,
            transformVariables, harmonizableTier1Variables, harmonizedTier1Variables);
        this.studyName = studyName;
    }

}
