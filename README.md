# Getting Started
This is an exam for apply to PageConsulting as Senior Backend Developer.

## Build, Packaging and Running
Clone the repository to your local file system, go into the project location and execute the sentence below.

### On Unix:
`./mvnw clean package && java -jar target/customersatisfaction-0.0.1-SNAPSHOT.jar`

### On Windows:
`mvnw clean package && java -jar target/customersatisfaction-0.0.1-SNAPSHOT.jar`

And now go to Documentation section to see the API Docs on the web. 

## Test and verify

To see the coverage go to:

https://sonarcloud.io/dashboard?id=rrangelo_CustomerSatisfactionExam

## Instalation

To running on Docker

### On Unix
`./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=rrangelo/customer_satisfaction`

### On Windows
`mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=rrangelo/customer_satisfaction`

After you need execute this command:
`docker run -p 8080:8080 -t rrangelo/customersatisfaction`

## Documentation

There are 6 endpoints to manage the resources.

* Customer: /customer
  * Post: 
  * Get: 
  * Patch: 
* Satisfaction: /satisfaction
  * Post: 
  * Get: 
  * Patch: 

First that all, you need build and run the project.

Further information go to [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui) to see the technical documentation.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Jersey](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-jersey)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-mongodb)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

