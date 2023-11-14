Tech stack:
·         Programming Language: Java

·         Web Framework: Spring Boot

Application Requirements:

1.       Done -> Service should expose a REST API to accept money transfers to other accounts. Money transfers should persist new balance of accounts

2.       Done -> Service should expose a REST API for getting the account details. You can disregard currencies at this time

Points to consider:

1.       Done -> Keep the design simple and to the point. The architecture should be scalable for adding new features

2.       Done -> Proper handling of concurrent transactions for the accounts (with unit tests)

3.       Done -> The datastore should run in-memory for the tests

4.       Done (integration + just a bit unit, total coverage > 80 %) -> Proper unit testing and decent coverage is a must

5.       Done -> Upload the code to a repository

6.       Disregard Currency or Rate Conversion

7.       Done ->  Improvise where details are not provided

Plus Points:

1.       Done (/v3/api-docs, /swagger-ui/index.html) -> Documentation of API (e.g. Swagger)

2.       Done (mvn install -> Successfully built image 'docker.io/library/money-transfers-service-gfl:0.0.1-SNAPSHOT') ->  Dockerized app