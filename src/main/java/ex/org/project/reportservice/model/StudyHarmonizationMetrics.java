package ex.org.project.reportservice.model;

import edu.stanford.bmir.radx.harmonization.metrics.lib.StudyMetrics;
import ex.org.project.reportservice.util.HarmonizationCalculatorUtil;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "dcc")
    private String dcc;

    @Column(name = "orig_transform_pairs_count")
    private Integer origTransformPairsCount;

    @Column(name = "variable_count")
    private Integer variableCount;

    @Column(name = "harmonizable_tier_1_variable_count")
    private Integer harmonizableTier1VariableCount;

    @Column(name = "harmonizable_tier_2_variable_count")
    private Integer harmonizableTier2VariableCount;

    @Column(name = "harmonizable_tier_3_variable_count")
    private Integer harmonizableTier3VariableCount;

    @Column(name = "harmonizable_total")
    private Integer harmonizableTotal;

    @Column(name = "harmonized_tier_1_variable_count")
    private Integer harmonizedTier1VariableCount;

    @Column(name = "harmonized_tier_2_variable_count")
    private Integer harmonizedTier2VariableCount;

    @Column(name = "harmonized_tier_3_variable_count")
    private Integer harmonizedTier3VariableCount;

    @Column(name = "harmonized_total")
    private Integer harmonizedTotal;

    @Column(name = "variables")
    private String variables;

    @Column(name = "harmonizable_tier_1_variables")
    private String harmonizableTier1Variables;

    @Column(name = "harmonizable_tier_2_variables")
    private String harmonizableTier2Variables;

    @Column(name = "harmonizable_tier_3_variables")
    private String harmonizableTier3Variables;

    @Column(name = "harmonized_tier_1_variables")
    private String harmonizedTier1Variables;

    @Column(name = "harmonized_tier_2_variables")
    private String harmonizedTier2Variables;

    @Column(name = "harmonized_tier_3_variables")
    private String harmonizedTier3Variables;

    public StudyHarmonizationMetrics(Integer reportId, StudyMetrics studyMetrics){
        this.reportId = reportId;
        this.studyPhs = studyMetrics.studyId().value();
        this.dcc = HarmonizationCalculatorUtil.normalizeCapitalizedProgramName(studyMetrics.programId().name());
        this.origTransformPairsCount = studyMetrics.nOrigTransformFilePairs();
        this.variableCount = studyMetrics.nUniqueDataElements();
        this.harmonizableTier1VariableCount = studyMetrics.nUniqueHarmonizableDataElementsTier1();
        this.harmonizableTier2VariableCount = studyMetrics.nUniqueHarmonizableDataElementsTier2();
        this.harmonizableTier3VariableCount = studyMetrics.nUniqueHarmonizableDataElementsTier3();
        this.harmonizableTotal = studyMetrics.totalHarmonizable();
        this.harmonizedTier1VariableCount = studyMetrics.nUniqueHarmonizedDataElementsTier1();
        this.harmonizedTier2VariableCount = studyMetrics.nUniqueHarmonizedDataElementsTier2();
        this.harmonizedTier3VariableCount = studyMetrics.nUniqueHarmonizedDataElementsTier3();
        this.harmonizedTotal = studyMetrics.totalHarmonized();
        this.variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.uniqueDataElements(), ",");
        this.harmonizableTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizableDataElementsTier1(), ",");
        this.harmonizableTier2Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizableDataElementsTier2(), ",");
        this.harmonizableTier3Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizableDataElementsTier3(), ",");
        this.harmonizedTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizedDataElementsTier1(), ",");
        this.harmonizedTier2Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizedDataElementsTier2(), ",");
        this.harmonizedTier3Variables = HarmonizationCalculatorUtil.concatenateSet(
                studyMetrics.harmonizedDataElementsTier3(), ",");
    }
}
