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

## Added Camel Debebeziuum Postgresql

> wrapper around Debezium using Debezium Embedded, which enables Change Data Capture from PostgresSQL database using Debezium without the need for Kafka or Kafka Connect

For the sake of simplicity, Camel logic is built in the same project. But the document store could be seen as a legacy system. 

`CDCRouteBuyilder` shows how to capture a `Create` operation in the `documents` table. The created document data is parsed using [Apache Tika](https://tika.apache.org/) in order to get document metadata.




