# Report Service

Spring Boot 3 microservice for Canopy. It is running on Java 17.

Report Service handles the retrieval of the various reports used on the metrics dashboard, including:
* Hub content
* Harmonization report IDs
* Harmonization outcomes
* Submission activities
* User population
* User activities
* The various CSV downloads for the aforementioned reports

# Install and Run

## Maven

### Local

There are a few environment variables that need to be set:
* db_username
* db_password
* spring_profiles_active
    * This should be set to 'local'
* GACredentials
    * This is your Google Analytics service account key JSON file as a String
* GA1PropertyId
    * This is the Canopy Google Analytics property ID
* ResourceBucket
    * S3 bucket name for storing reports
* WeeklyReportPath
    * Path to the weekly report files in the resource bucket
* WeeklyReportFileName
    * Name of the weekly report file to be processed

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

There are a few environment variables that need to be set in AWS Secrets Manager:
* dbuser
    * database username
* password
    * database password for dbuser
* host
    * hostname of database
* port
    * database port
* dbname
    * database name

In a specific instance, the 2 environment variables that need to be set are:
* spring_profiles_active
    * This should be set to '{environment}'
        * The current environments are dev, test, prod
* GACredentials
  * This is your Google Analytics service account key JSON file as a String
* GA1PropertyId
  * This is the Canopy Google Analytics property ID

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
```
