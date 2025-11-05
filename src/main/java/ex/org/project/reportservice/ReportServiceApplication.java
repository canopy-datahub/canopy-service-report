package ex.org.project.reportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(proxyBeanMethods = false)
@ComponentScan(basePackages = {
    "edu.stanford.bmir.radx.harmonization.metrics",
    "gov.nih.radx.reportservice",
    "ex.org.project.reportservice"
})
@EnableJpaRepositories(basePackages = {
    "ex.org.project.reportservice.repositories",  // Report service repositories
    "ex.org.project.datahub.auth.repository"      // Keycloak library repositories
})
@EntityScan(basePackages = {
    "ex.org.project.reportservice.model",         // Report service entities
    "ex.org.project.datahub.auth.model"           // Keycloak library entities
})
public class ReportServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReportServiceApplication.class, args);
  }

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

}
