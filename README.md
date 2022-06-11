## Description

This repository contains an [Spring Boot application](https://spring.io/projects/spring-boot) containerized using [jib maven plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)
allowing to *store and serve files* according to the following api spec:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://www.getpostman.com/collections/f10e51611f0ba516b4d2)

# Run the example.

```bash
$ git clone https://github.com/oalles/document-store
$ cd document-store
$ mvn clean install
$ docker-compose -f deployment/docker-compose.yaml up
```
# Next Steps
The goal is to be able to build an integration using Debezium Embedded engine so we can express some pipeline logic when a document has been inserted. 



