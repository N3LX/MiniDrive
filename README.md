# MiniDrive

MiniDrive is a simple REST API works similarly to cloud services like Google Drive or Dropbox.

Application uses:
- Spring Boot
- Spring Security with my own implementation of JWT Authentication
- Liquibase
- Lombok
- For tests, Mockito and Rest Assured

## Requirements

After cloning the repository make sure that you have following applications installed:

- Docker
- docker-compose
- JDK 17 or higher
- Gradle

Alternatively, you can provide your own Postgres database instead of the that was included as a docker-compose stack, then Docker tools are not required.

## How to run/stop

*This is minimal required effort to run the application, if you want to use it on your own I strongly encourage check all application.yml properties and changing any secrets and storage locations. Database credentials can be changed directory in database/docker-compose.yml*

*Note: PGAdmin for debugging is available at http://localhost:5050/, credentials are included in database/docker-compose.yml*

1. Spin up docker-compose.yml from /database directory
2. Open application.yml in src/main/resources and ensure that rootDirAbsolutePath is set correctly depending on your filesystem and desired location
3. Run the java project via :bootRun gradle task


## API endpoints

Application comes with Swagger UI available at http://localhost:8080/swagger-ui/index.html *(assuming default configuration was used)*

File Insomnia_Collection is a collection exported from Insomnia which is an alternative to Postman which does not offer scratchpad/collections in free version anymore. Feel free to import it to your application or simply open it - data is stored in JSON and should be decently readable.

A preview of available endpoints can be seen below:

![Frontend demo](media/1.png)