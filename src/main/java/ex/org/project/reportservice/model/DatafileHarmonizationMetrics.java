package ex.org.project.reportservice.model;

import edu.stanford.bmir.radx.harmonization.metrics.lib.OrigTransformFilePairMetrics;
import ex.org.project.reportservice.util.HarmonizationCalculatorUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "datafile_harmonization_metrics")
@NoArgsConstructor
@AllArgsConstructor
public class DatafileHarmonizationMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "orig_file_name")
    private String originalFileName;

    @Column(name = "transform_file_name")
    private String transformFileName;

    @Column(name = "study_phs")
    private String studyPhs;

    @Column(name = "dcc")
    private String dcc;

    @Column(name = "orig_variable_count")
    private Integer origVariableCount;

    @Column(name = "transform_variable_count")
    private Integer transformVariableCount;

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

    @Column(name = "orig_variables")
    private String origVariables;

    @Column(name = "transform_variables")
    private String transformVariables;

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

    public DatafileHarmonizationMetrics(Integer reportId, OrigTransformFilePairMetrics pairMetrics){
        this.reportId = reportId;
        this.originalFileName = pairMetrics.origFileName().orElse(null);
        this.transformFileName = pairMetrics.transformFileName().orElse(null);
        this.studyPhs = pairMetrics.studyId().value();
        this.dcc = HarmonizationCalculatorUtil.normalizeCapitalizedProgramName(pairMetrics.programId().name());
        this.origVariableCount = pairMetrics.nDataElementsOrig();
        this.transformVariableCount = pairMetrics.nDataElementsTransform();
        this.harmonizableTier1VariableCount = pairMetrics.nHarmonizableDataElementsTier1();
        this.harmonizableTier2VariableCount = pairMetrics.nHarmonizableDataElementsTier2();
        this.harmonizableTier3VariableCount = pairMetrics.nHarmonizableDataElementsTier3();
        this.harmonizableTotal = pairMetrics.totalHarmonizable();
        this.harmonizedTier1VariableCount = pairMetrics.nHarmonizedDataElementsTier1();
        this.harmonizedTier2VariableCount = pairMetrics.nHarmonizedDataElementsTier2();
        this.harmonizedTier3VariableCount = pairMetrics.nHarmonizedDataElementsTier3();
        this.harmonizedTotal = pairMetrics.totalHarmonized();
        this.origVariables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.dataElementsOrig(), ",");
        this.transformVariables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.dataElementsTransform(), ",");
        this.harmonizableTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizableDataElementsTier1(), ",");
        this.harmonizableTier2Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizableDataElementsTier2(), ",");
        this.harmonizableTier3Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizableDataElementsTier3(), ",");
        this.harmonizedTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizedDataElementsTier1(), ",");
        this.harmonizedTier2Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizedDataElementsTier2(), ",");
        this.harmonizedTier3Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizedDataElementsTier3(), ",");
    }
}
