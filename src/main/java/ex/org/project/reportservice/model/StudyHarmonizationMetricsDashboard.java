package ex.org.project.reportservice.model;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyHarmonizationMetricsDashboard extends StudyHarmonizationMetrics {

    @Column(name = "study_name")
    private String studyName;

    public StudyHarmonizationMetricsDashboard(Integer id, Integer reportId, String studyPhs, String dcc,
                                              Integer origTransformPairsCount, Integer variableCount,
                                              Integer harmonizableTier1VariableCount,
                                              Integer harmonizableTier2VariableCount,
                                              Integer harmonizableTier3VariableCount, Integer harmonizableTotal,
                                              Integer harmonizedTier1VariableCount,
                                              Integer harmonizedTier2VariableCount,
                                              Integer harmonizedTier3VariableCount, Integer harmonizedTotal,
                                              String variables, String harmonizableTier1Variables,
                                              String harmonizableTier2Variables, String harmonizableTier3Variables,
                                              String harmonizedTier1Variables, String harmonizedTier2Variables,
                                              String harmonizedTier3Variables, String studyName) {
        super(id, reportId, studyPhs, dcc, origTransformPairsCount, variableCount, harmonizableTier1VariableCount,
              harmonizableTier2VariableCount, harmonizableTier3VariableCount, harmonizableTotal,
              harmonizedTier1VariableCount, harmonizedTier2VariableCount, harmonizedTier3VariableCount,
              harmonizedTotal, variables, harmonizableTier1Variables, harmonizableTier2Variables,
              harmonizableTier3Variables, harmonizedTier1Variables, harmonizedTier2Variables, harmonizedTier3Variables);
        this.studyName = studyName;
    }

}
