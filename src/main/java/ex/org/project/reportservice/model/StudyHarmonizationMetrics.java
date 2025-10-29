package ex.org.project.reportservice.model;

import edu.stanford.bmir.radx.harmonization.metrics.lib.StudyMetrics;
import ex.org.project.reportservice.util.HarmonizationCalculatorUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "study_harmonization_metrics")
@NoArgsConstructor
@AllArgsConstructor
public class StudyHarmonizationMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "study_phs")
    private String studyPhs;

    @Column(name = "center")
    private String center;

    @Column(name = "orig_transform_pairs_count")
    private Integer origTransformPairsCount;

    @Column(name = "variable_count")
    private Integer variableCount;

    @Column(name = "harmonizable_tier_1_variable_count")
    private Integer harmonizableTier1VariableCount;

    @Column(name = "harmonized_tier_1_variable_count")
    private Integer harmonizedTier1VariableCount;

    @Column(name = "variables")
    private String variables;

    @Column(name = "harmonizable_tier_1_variables")
    private String harmonizableTier1Variables;

    @Column(name = "harmonized_tier_1_variables")
    private String harmonizedTier1Variables;

    public StudyHarmonizationMetrics(Integer reportId, StudyMetrics studyMetrics){
        this.reportId = reportId;
        this.studyPhs = studyMetrics.studyId().value();
        this.center = studyMetrics.programId().toString();
        this.origTransformPairsCount = studyMetrics.nOrigTransformFilePairs();
        this.variableCount = studyMetrics.nUniqueDataElements();
        this.harmonizableTier1VariableCount = studyMetrics.nUniqueHarmonizableDataElementsTier1();
        this.harmonizedTier1VariableCount = studyMetrics.nUniqueHarmonizedDataElementsTier1();
        this.variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.uniqueDataElements(), ",");
        this.harmonizableTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizableDataElementsTier1(), ",");
        this.harmonizedTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizedDataElementsTier1(), ",");
    }
}
