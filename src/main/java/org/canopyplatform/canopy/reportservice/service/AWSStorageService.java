package org.canopyplatform.canopy.reportservice.service;

import org.canopyplatform.canopy.reportservice.exceptions.DocumentFileNotFoundException;
import org.canopyplatform.canopy.reportservice.exceptions.DownloadServiceReadWriteError;
import org.canopyplatform.canopy.reportservice.exceptions.DownloadServiceS3FileError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class AWSStorageService {

    private final S3TransferManager transferManager;
    private final String s3InitialUploadBucket;
    private final String weeklyReportKey;
    private  final String workingDirectory;
    private final String weeklyReportFileName;

    public AWSStorageService(S3TransferManager transferManager,
                             @Value("${ResourceBucket}") String s3InitialUploadBucket,
                             @Value("${s3.download-directory}") String workingDirectory,
                             @Value("${WeeklyReportPath}")String weeklyReportKey,
                             @Value("${WeeklyReportFileName}")String weeklyReportFileName
                             ){
        this.transferManager = transferManager;
        this.s3InitialUploadBucket = s3InitialUploadBucket;
        this.workingDirectory = workingDirectory;
        this.weeklyReportKey = weeklyReportKey;
        this.weeklyReportFileName = weeklyReportFileName;
        File dir = new File(workingDirectory);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    /**
     * Uploads report file to S3
     */
    public boolean uploadWeeklyFileReport(ByteArrayInputStream inputStream) {
        try {
            UploadRequest uploadRequest = UploadRequest.builder()
                    .putObjectRequest(
                            req -> req.bucket(s3InitialUploadBucket)
                                    .key(weeklyReportKey)
                                    .checksumAlgorithm(ChecksumAlgorithm.SHA256)
                    )
                    .requestBody(AsyncRequestBody.fromBytes(inputStream.readAllBytes())).build();

            Upload uploadResponse = transferManager.upload(uploadRequest);

            //once request is made, wait for response completion and set values
            PutObjectResponse completedResponse = uploadResponse.completionFuture().get().response();
            if (completedResponse == null) {
                return false;
            }
            return true;
        }  catch(CancellationException | ExecutionException | InterruptedException e){
            log.error("Error uploading file" + weeklyReportKey + "to AWS", e);
            return false;
        }
    }

    /**
     * Downloads report file to S3
     */
    public ResponseEntity<Object> downloadWeeklyFileReport() {
        File tempFile = new File(workingDirectory + weeklyReportFileName);

        DownloadFileRequest request =
                DownloadFileRequest.builder()
                        .getObjectRequest(b -> b.bucket(s3InitialUploadBucket).key(weeklyReportKey))
                        .destination(Paths.get(tempFile.getPath()))
                        .build();
        FileDownload download = transferManager.downloadFile(request);

        // Wait for the transfer to complete
        joinFileDownloadFuture(download, weeklyReportFileName);
        return openFileStreamResponse(tempFile,weeklyReportFileName);

    }

    private void joinFileDownloadFuture(FileDownload downloadFile, String fileName) {
        try {
            downloadFile.completionFuture().join();
        } catch(CancellationException | CompletionException e) {
            String errorMessage = String.format("Error retrieving file from S3: %s", fileName);
            log.error(errorMessage, e);
            throw new DownloadServiceS3FileError(errorMessage);
        }
    }

    private ResponseEntity<Object> openFileStreamResponse(File tempFile, String fileName) {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + "\"" + fileName + "\"");

            FileInputStream fileStream = new FileInputStream(tempFile);
            byte[] responseFile = fileStream.readAllBytes();
            fileStream.close();
            ResponseEntity<Object> response = ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(responseFile);
            tempFile.delete();
            return response;
        } catch (FileNotFoundException e) {
            log.error("Error reading contents of the file +" + fileName, e);
            tempFile.delete();
            throw new DocumentFileNotFoundException("Error reading file" + fileName);
        } catch (IOException e) {
            log.error("Error writing file to Response Entity" + fileName, e);
            tempFile.delete();
            throw new DownloadServiceReadWriteError("Error reading file or closing file stream"+ fileName);
        }
    }

}
