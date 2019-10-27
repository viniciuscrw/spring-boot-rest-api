# Customer API

This is a microservice that can be used to perform CRUD operations concerning Customers management.

### Setting up your local environment

#### Requisites

Make sure the following itens are installed on your environment:
* Java 8
* Apache Maven
* Docker and Docker Compose

#### Running the application

Once you've cloned this repository on your machine, you can proceed with the following steps:
* Start your local dockerized PostgreSQL Database: 
    - From the repository root directory, execute:
    
         ` docker-compose up -d`
         
* Then you can run the application with:
  
     `mvn spring-boot:run`
     
#### Swagger UI

With the application running, you can visualize the API documentation in:
* http://localhost:8080/swagger-ui.hmtl

#### Postman

For test the API endpoints and the operations exposed by it, you can use this Postman Collection:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/b8dacca394e1404912b8)

        



