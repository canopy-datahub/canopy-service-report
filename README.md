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
* GACredentials
    * This is your Google Analytics service account key json file as a String
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

In a specific instance, the 2 environment variables need to be set:
* spring_profiles_active
    * This should be set to '{environment}'
        * The current environments are dev, test, prod
* GACredentials
  * This is your Google Analytics service account key json file as a String
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