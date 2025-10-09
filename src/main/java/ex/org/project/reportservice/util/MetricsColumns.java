package ex.org.project.reportservice.util;

import java.util.Arrays;
import java.util.List;

public class MetricsColumns {

    public static final List<String> CENTER_COLUMN_NAMES = Arrays.asList("Center", "Total Studies", "Studies with Data",
                                                                      "Data Size", "All Files", "Data Files",
                                                                      "Orig Files", "Transform Files", "Meta Files",
                                                                      "Dictionary Files", "README Files",
                                                                      "Other Files");

    public static final List<String> STUDY_COLUMN_NAMES = Arrays.asList("Study ID", "Study Name", "Data Size",
                                                                        "All Files", "Data Files", "Orig Files",
                                                                        "Transform Files", "Meta Files",
                                                                        "Dictionary Files", "README Files",
                                                                        "Other Files");

    public static final List<String> SUBMISSION_DCC_COLUMN_NAMES = Arrays.asList("Center", "Studies Initiated",
                                                                                 "Studies Published",
                                                                                 "Data Files Submitted",
                                                                                 "Data Files Approved",
                                                                                 "Data Files Rejected");

    public static final List<String> SUBMISSION_STUDY_COLUMN_NAMES = Arrays.asList("PHS", "Study Name", "Center",
                                                                                   "Data Files Submitted",
                                                                                   "Data Files Approved",
                                                                                   "Data Files Rejected");

    public static final List<String> STUDY_HARMONIZATION_COLUMN_NAMES = Arrays.asList("PHS", "Study Name", "Center",
                                                                                      "Files", "Variables",
                                                                                      "Harmonizable Variables (Tier 1)",
                                                                                      "Harmonized Variables (Tier 1)",
                                                                                      "Harmonizable Variables (Tier 2)",
                                                                                      "Harmonized Variables (Tier 2)",
                                                                                      "Total Harmonizable",
                                                                                      "Total Harmonized");

    public static final List<String> DATASET_HARMONIZATION_COLUMN_NAMES = Arrays.asList("File Name (Orig)",
                                                                                        "File Name (Trans)", "PHS",
                                                                                        "Study Name", "Center",
                                                                                        "Variables (Orig)",
                                                                                        "Variables (Trans)",
                                                                                        "Harmonizable Variables (Tier 1)",
                                                                                        "Harmonized Variables (Tier 1)",
                                                                                        "Harmonizable Variables (Tier 2)",
                                                                                        "Harmonized Variables (Tier 2)",
                                                                                        "Total Harmonizable",
                                                                                        "Total Harmonized");

    public static final List<String> STUDY_HARMONIZATION_CSV_COLUMN_NAMES = Arrays.asList("PHS", "Study Name", "Center",
                                                                                          "Files", "Variables Count",
                                                                                          "Harmonizable Variables Count (Tier 1)",
                                                                                          "Harmonized Variables Count (Tier 1)",
                                                                                          "Harmonizable Variables Count (Tier 2)",
                                                                                          "Harmonized Variables Count (Tier 2)",
                                                                                          "Total Harmonizable",
                                                                                          "Total Harmonized",
                                                                                          "Variables",
                                                                                          "Harmonizable Variables (Tier 1)",
                                                                                          "Harmonized Variables (Tier 1)",
                                                                                          "Harmonizable Variables (Tier 2)",
                                                                                          "Harmonized Variables (Tier 2)");

    public static final List<String> DATASET_HARMONIZATION_CSV_COLUMN_NAMES = Arrays.asList("File Name (Orig)",
                                                                                            "File Name (Trans)", "PHS",
                                                                                            "Study Name", "Center",
                                                                                            "Variables Count (Orig)",
                                                                                            "Variables Count (Trans)",
                                                                                            "Harmonizable Variables Count (Tier 1)",
                                                                                            "Harmonized Variables Count (Tier 1)",
                                                                                            "Harmonizable Variables Count (Tier 2)",
                                                                                            "Harmonized Variables Count (Tier 2)",
                                                                                            "Total Harmonizable",
                                                                                            "Total Harmonized",
                                                                                            "Original Variables",
                                                                                            "Transform Variables",
                                                                                            "Harmonizable Variables (Tier 1)",
                                                                                            "Harmonized Variables (Tier 1)",
                                                                                            "Harmonizable Variables (Tier 2)",
                                                                                            "Harmonized Variables (Tier 2)");

    public static final List<String> WEEKLY_METRICS_COLUMN_NAMES = Arrays.asList("Center", "Study PHS",
                                                                                 "Study Title", "Study Status",
                                                                                 "Study Create Date", "Submission ID",
                                                                                 "Submission Create Date",
                                                                                 "Submission Status", "File Name",
                                                                                 "File Version", "File Category",
                                                                                 "File Create Date","File Status",
                                                                                 "File Size", "Tier-1 CDE",
                                                                                 "Non Tier-1 CDE");
}
