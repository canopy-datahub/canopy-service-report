# ReportService

Spring Boot 3 microservice for Data Hub 3.0. It is running on Java 17.

Report Service handles the retrieval of the various reports used on the metrics dashboard, including: 
* Hub content
* Harmonization report IDs
* Harmonization outcomes
* Submission activities
* User population
* User activies
* The various csv downloads for the aforementioned reports

# Install and Run

## Maven

### Local

There are a few environment variable that need to be set:
* db_username
* db_password
* spring_profiles_active
    * This should be set to 'local'
* DATAHUB_KEYCLOAK_ISSUER_URI
    * Keycloak issuer URI (e.g., http://localhost:8180/realms/datahub)
* DATAHUB_KEYCLOAK_JWK_SET_URI
    * Keycloak JWK set URI (e.g., http://localhost:8180/realms/datahub/protocol/openid-connect/certs)
* DATAHUB_KEYCLOAK_REALM
    * Keycloak realm name (typically 'datahub')
* DATAHUB_KEYCLOAK_CLIENT_ID
    * Keycloak client ID (typically 'datahub-backend')
* ResourceBucket
    * S3 bucket name for storing reports
* WeeklyReportPath
    * Path to the weekly report files in the resource bucket
* WeeklyReportFileName
    * Name of the weekly report file to be processed

For dev profile, also set:
* DATAHUB_SPRING_MANAGEMENT_USER
    * Spring management username
* DATAHUB_SPRING_MANAGEMENT_PASSWORD
    * Spring management password

Optional environment variables for Google Analytics:
* GACredentials
    * This is your Google Analytics service account key json file as a String
    * If not configured, Google Analytics features will be disabled
* GA1PropertyId
    * This is the 1.0 Data Hub Google Analytics property id

I typically just set these via Java environment variables in IntelliJ.

Once the environment variables are set:
```
mvn clean install
```
Once all classes are generated, you can run the application with maven or via the application context.
```
mvn spring-boot:run
```

### Cloud

If running a cloud configuration locally, AWS CLI needs to be installed and configured.

There are a few environment variable that need to be set in AWS Secrets Manager:
* dbuser
    * Open Search hostname / url
* password
    * database password for dbuser
* host
    * hostname of database
* port
    * database port
* dbname
    * database name

In a specific instance, the following environment variables need to be set:
* spring_profiles_active
    * This should be set to '{environment}'
        * The current environments are dev, test, prod
* DATAHUB_KEYCLOAK_ISSUER_URI
    * Keycloak issuer URI (environment-specific)
* DATAHUB_KEYCLOAK_JWK_SET_URI
    * Keycloak JWK set URI (environment-specific)
* DATAHUB_KEYCLOAK_REALM
    * Keycloak realm name (typically 'datahub')
* DATAHUB_KEYCLOAK_CLIENT_ID
    * Keycloak client ID (typically 'datahub-backend')

For dev profile, also set:
* DATAHUB_SPRING_MANAGEMENT_USER
    * Spring management username
* DATAHUB_SPRING_MANAGEMENT_PASSWORD
    * Spring management password

Optional environment variables for Google Analytics:
* GACredentials
  * This is your Google Analytics service account key json file as a String
  * If not configured, Google Analytics features will be disabled
* GA1PropertyId
  * This is the 1.0 Data Hub Google Analytics property id

Once the environment variables are set:
```
mvn clean install 
```
Once all classes are generated, you can run the application with maven or via the application context.
```
mvn spring-boot:run
```

### Endpoint

The base endpoint for this service is:
```
{{hostname}}/api/report/v1/