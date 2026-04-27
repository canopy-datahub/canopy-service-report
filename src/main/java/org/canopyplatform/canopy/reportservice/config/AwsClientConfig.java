package org.canopyplatform.canopy.reportservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class AwsClientConfig {
    @Bean
    public S3TransferManager s3TransferManager(){
        return S3TransferManager.create();
    }

}
