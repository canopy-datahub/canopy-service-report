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

    public StudyHarmonizationMetricsDashboard(Integer id, Integer reportId, String studyPhs, String center,
                                              Integer origTransformPairsCount, Integer variableCount,
                                              Integer harmonizableTier1VariableCount,
                                              Integer harmonizedTier1VariableCount,
                                              String variables,
                                              String harmonizableTier1Variables,
                                              String harmonizedTier1Variables,
                                              String studyName) {
        super(id, reportId, studyPhs, center, origTransformPairsCount, variableCount, harmonizableTier1VariableCount,
          harmonizedTier1VariableCount, variables, harmonizableTier1Variables, harmonizedTier1Variables);
        this.studyName = studyName;
    }

}
