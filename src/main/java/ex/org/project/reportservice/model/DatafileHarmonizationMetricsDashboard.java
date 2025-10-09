package ex.org.project.reportservice.model;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class DatafileHarmonizationMetricsDashboard extends DatafileHarmonizationMetrics {

    @Column(name = "study_name")
    private String studyName;

    public DatafileHarmonizationMetricsDashboard(Integer id, Integer reportId, String originalFileName,
                                                 String transformFileName, String studyPhs, String center,
                                                 Integer origVariableCount, Integer transformVariableCount,
                                                 Integer harmonizableTier1VariableCount,
                                                 Integer harmonizableTier2VariableCount,
                                                 Integer harmonizableTier3VariableCount, Integer harmonizableTotal,
                                                 Integer harmonizedTier1VariableCount,
                                                 Integer harmonizedTier2VariableCount,
                                                 Integer harmonizedTier3VariableCount, Integer harmonizedTotal,
                                                 String harmonizableTier1Variables, String harmonizableTier2Variables,
                                                 String harmonizableTier3Variables, String harmonizedTier1Variables,
                                                 String harmonizedTier2Variables, String harmonizedTier3Variables,
                                                 String origVariables, String transformVariables, String studyName) {
        super(id, reportId, originalFileName, transformFileName, studyPhs, center, origVariableCount,
              transformVariableCount, harmonizableTier1VariableCount, harmonizableTier2VariableCount,
              harmonizableTier3VariableCount, harmonizableTotal, harmonizedTier1VariableCount,
              harmonizedTier2VariableCount, harmonizedTier3VariableCount, harmonizedTotal,origVariables,
              transformVariables, harmonizableTier1Variables, harmonizableTier2Variables,
              harmonizableTier3Variables, harmonizedTier1Variables, harmonizedTier2Variables, harmonizedTier3Variables);
        this.studyName = studyName;
    }

}
