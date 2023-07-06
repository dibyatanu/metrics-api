# mertics-api
###Building the project
####Tools required:
* Java 17
* Gradle 7.5 and above
* docker
* docker-compose
* Postgres Database (in docker)

####Prerequisite
* Docker Compose must be installed to run Postgres Data Base

###Build, run unit and integration test and  run application
* To spin up Postgress DB `$ docker-compose up -d`
* To stop Postgress DB `$ docker-compose stop`

####To build application
`$ ./gradlew clean build`

####To run unit and integration test
`$ ./gradlew test`

####To run application
`$ ./gradlew bootRun` (Note: Postgres DB  should up and running)
####Swagger UI
* http://localhost:8080/swagger-ui.html
####Spring Actuators
* Run the application `$ ./gradlew bootRun`
* Using terminal type `$ curl localhost:8080/actuator/health`




