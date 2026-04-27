package org.canopyplatform.canopy.reportservice.service;

import edu.stanford.bmir.radx.harmonization.metrics.lib.*;
import org.canopyplatform.canopy.reportservice.exceptions.HarmonizationReportException;
import org.canopyplatform.canopy.reportservice.repositories.*;
import org.canopyplatform.canopy.reportservice.mapper.DataFileInputMapper;
import org.canopyplatform.canopy.reportservice.model.DataFileInputEntity;
import org.canopyplatform.canopy.reportservice.model.DatafileHarmonizationMetrics;
import org.canopyplatform.canopy.reportservice.model.MetricsReport;
import org.canopyplatform.canopy.reportservice.model.MetricsReportType;
import org.canopyplatform.canopy.reportservice.model.StudyHarmonizationMetrics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class HarmonizationMetricsCalculator {

    private final DataFileInputEntityRepository dataFileInputEntityRepository;
    private final DataFileInputMapper dataFileInputMapper;
    private final MetricsCalculator metricsCalculator;
    private final MetricsReportTypeRepository metricsReportTypeRepository;
    private final MetricsReportRepository metricsReportRepository;
    private final DatafileHarmonizationRepository datafileHarmonizationRepository;
    private final StudyHarmonizationRepository studyHarmonizationRepository;

    @Transactional
    public Integer generateHarmonizationMetricsReport() {
        List<DataFileInputEntity> dataFileInputEntities = dataFileInputEntityRepository.findCurrentFiles();
        List<DataFileInput> dataFileInputs = dataFileInputMapper.entitiesToDataFileInputs(dataFileInputEntities);
        MetricsReport metricsReport = computeMetricsReport(dataFileInputs);
        Integer reportId = saveMetricsReport(metricsReport);
        return reportId;
    }

    private MetricsReport computeMetricsReport(List<DataFileInput> dataFileInputs) {
        try {
            return metricsCalculator.computeHarmonizationMetrics(dataFileInputs);
        }
        catch(InvalidProgramIdException | InvalidOrigTransformCategoryException | NoVersionNumberException |
              InvalidHarmonizationTierException e) {
            String errorMessage = "Error calculating harmonization metrics via library: ";
            log.error(errorMessage, e);
            throw new HarmonizationReportException(String.format("%s %s", errorMessage, e.getMessage()));
        }
    }

    private Integer saveMetricsReport(MetricsReport metricsReport) {
        Integer reportId = createNewMetricsReportEntity(metricsReport);
        List<StudyHarmonizationMetrics> studyMetrics = getStudyHarmonizationMetrics(reportId, metricsReport);
        List<DatafileHarmonizationMetrics> datafileMetrics = getDatafileHarmonizationMetrics(reportId, metricsReport);
        studyHarmonizationRepository.saveAll(studyMetrics);
        datafileHarmonizationRepository.saveAll(datafileMetrics);
        return reportId;
    }

    private Integer createNewMetricsReportEntity(MetricsReport metricsReport) {
        MetricsReportType reportType = metricsReportTypeRepository.findByName("harmonization")
                .orElseThrow(() -> new HarmonizationReportException("Could not find harmonization report type"));
        var metricsReportEntity = new MetricsReport();
        metricsReportEntity.setReportDate(metricsReport.date());
        metricsReportEntity.setType(reportType);
        metricsReportEntity = metricsReportRepository.save(metricsReportEntity);
        return metricsReportEntity.getId();
    }

    private List<StudyHarmonizationMetrics> getStudyHarmonizationMetrics(Integer reportId, MetricsReport metricsReport) {
        return metricsReport.studyMetrics()
                .stream()
                .map(study -> new StudyHarmonizationMetrics(reportId, study))
                .toList();
    }

    private List<DatafileHarmonizationMetrics> getDatafileHarmonizationMetrics(Integer reportId, MetricsReport metricsReport) {
        return metricsReport.pairMetrics()
                .stream()
                .map(pair -> new DatafileHarmonizationMetrics(reportId, pair))
                .toList();
    }

    private void writeMetricsReportToFile(MetricsReport metricsReport) {
        try {
            FileWriter fileWriter = new FileWriter("Harmonization-Metrics.txt");
            fileWriter.write(metricsReport.date().format(DateTimeFormatter.ISO_LOCAL_DATE));
            fileWriter.write("\n");
            fileWriter.write(StringUtils.repeat('-', 20));
            fileWriter.write("\n");
            fileWriter.write("File Pair Metrics:");
            for(var pairMetric : metricsReport.pairMetrics()) {
                fileWriter.write(String.valueOf(pairMetric));
                fileWriter.write("\n");
            }
            fileWriter.write(StringUtils.repeat('-', 20));
            fileWriter.write("Study Metrics:");
            for(var studyMetric : metricsReport.studyMetrics()) {
                fileWriter.write(String.valueOf(studyMetric));
                fileWriter.write("\n");
            }
            fileWriter.write("\n");
        }
        catch(IOException e) {
            log.error(e.getMessage());
        }
    }

}
