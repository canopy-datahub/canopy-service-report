package org.canopyplatform.canopy.reportservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@SuperBuilder
@Data
public abstract class HubContentAggMetricsDto {
	@JsonIgnore
	protected String type;

	@JsonProperty("Data Size")
	@CsvBindByName(column = "Data Size")
	protected Double totalFileSize;

	@JsonProperty("All Files")
	@CsvBindByName(column = "All Files")
	protected Integer totalFileCount;

	@JsonProperty("Data Files")
	@CsvBindByName(column = "Data Files")
	protected Integer dataFileCount;

	@JsonProperty("Orig Files")
	@CsvBindByName(column = "Orig Files")
	protected Integer origRawFileCount;

	@JsonProperty("Transform Files")
	@CsvBindByName(column = "Transform Files")
	protected Integer transformFilesCount;

	@JsonProperty("Meta Files")
	@CsvBindByName(column = "Meta Files")
	protected Integer metadataFileCount;

	@JsonProperty("Dictionary Files")
	@CsvBindByName(column = "Dictionary Files")
	protected Integer dictionaryFileCount;

	@JsonProperty("README Files")
	@CsvBindByName(column = "README Files")
	protected Integer readmeFileCount;

	@JsonProperty("Other Files")
	@CsvBindByName(column = "Other Files")
	protected Integer otherFileCount;

}
