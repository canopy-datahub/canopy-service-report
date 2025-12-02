package ex.org.project.reportservice.model;

import edu.stanford.bmir.radx.harmonization.metrics.lib.OrigTransformFilePairMetrics;
import ex.org.project.reportservice.util.HarmonizationCalculatorUtil;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "study_id")
    private String studyId;

    @Column(name = "center")
    private String center;

    @Column(name = "orig_variable_count")
    private Integer origVariableCount;

    @Column(name = "transform_variable_count")
    private Integer transformVariableCount;

    @Column(name = "harmonizable_tier_1_variable_count")
    private Integer harmonizableTier1VariableCount;

    @Column(name = "harmonized_tier_1_variable_count")
    private Integer harmonizedTier1VariableCount;

    @Column(name = "orig_variables")
    private String origVariables;

    @Column(name = "transform_variables")
    private String transformVariables;

    @Column(name = "harmonizable_tier_1_variables")
    private String harmonizableTier1Variables;

    @Column(name = "harmonized_tier_1_variables")
    private String harmonizedTier1Variables;

    public DatafileHarmonizationMetrics(Integer reportId, OrigTransformFilePairMetrics pairMetrics){
        this.reportId = reportId;
        this.originalFileName = pairMetrics.origFileName().orElse(null);
        this.transformFileName = pairMetrics.transformFileName().orElse(null);
        this.studyId = pairMetrics.studyId().value();
        this.center = pairMetrics.programId().name();
        this.origVariableCount = pairMetrics.nDataElementsOrig();
        this.transformVariableCount = pairMetrics.nDataElementsTransform();
        this.harmonizableTier1VariableCount = pairMetrics.nHarmonizableDataElementsTier1();
        this.harmonizedTier1VariableCount = pairMetrics.nHarmonizedDataElementsTier1();
        this.origVariables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.dataElementsOrig(), ",");
        this.transformVariables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.dataElementsTransform(), ",");
        this.harmonizableTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizableDataElementsTier1(), ",");
        this.harmonizedTier1Variables = HarmonizationCalculatorUtil.concatenateSet(
                pairMetrics.harmonizedDataElementsTier1(), ",");
    }
}
