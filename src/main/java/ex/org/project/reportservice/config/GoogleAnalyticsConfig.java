package ex.org.project.reportservice.config;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import ex.org.project.reportservice.exceptions.AnalyticsClientException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "google-analytics")
public class GoogleAnalyticsConfig {

    private List<String> userAnalyticsFields;
    private String propertyId;
    private String credentials;

    @Bean
    public BetaAnalyticsDataClient getBetaAnalyticsDataClient() {
        // Check if credentials are actually provided (not empty)
        if (credentials == null || credentials.trim().isEmpty()) {
            log.warn("Google Analytics credentials not configured. GA features will be disabled.");
            return null;
        }
        
        log.info("Initializing Google Analytics client with provided credentials");
        try {
            InputStream credentialsStream = new ByteArrayInputStream(credentials.getBytes());
            var credentialsProvider = FixedCredentialsProvider
                    .create(
                            ServiceAccountCredentials.fromStream(credentialsStream));
            var settings = BetaAnalyticsDataSettings
                    .newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();
            return BetaAnalyticsDataClient.create(settings);
        } catch (IOException e) {
            String errorMessage = "Error initializing Google Analytics Client: " + e.getMessage();
            log.error(errorMessage, e);
            log.warn("Google Analytics will be disabled due to initialization error.");
            return null;
        }
    }
}
