package ex.org.project.reportservice.mapper;

import edu.stanford.bmir.radx.harmonization.metrics.lib.DataFileInput;
import ex.org.project.reportservice.model.DataFileInputEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DataFileInputMapper {

    @Mapping(source = "variableNames", target = "variableNames", qualifiedByName = "stringToList")
    @Mapping(source = "category", target = "category", qualifiedByName = "parseCategory")
    DataFileInput entityToDataFileInput(DataFileInputEntity inputEntity);

    List<DataFileInput> entitiesToDataFileInputs(List<DataFileInputEntity> inputEntities);

    @Named("stringToList")
    static List<String> stringToList(String variableNames) {
        if (variableNames == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(variableNames.split(";")).map(String::trim).toList();
    }

    @Named("parseCategory")
    static String parseCategory(String category) {
        return switch (category) {
            case "Tabular Data - Non-harmonized" -> "orig";
            case "Tabular Data - Harmonized" -> "transform";
            default -> "INVALID CATEGORY";
        };
    }

}
