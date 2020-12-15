# Columbia

![CI build](https://github.com/mthmulders/columbia/workflows/CI%20build/badge.svg)
[![SonarCloud quality gate](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_columbia&metric=alert_status)](https://sonarcloud.io/dashboard?id=mthmulders_columbia)
[![SonarCloud vulnerability count](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_columbia&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=mthmulders_columbia)
[![SonarCloud technical debt](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_columbia&metric=sqale_index)](https://sonarcloud.io/dashboard?id=mthmulders_columbia)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=mthmulders/columbia)](https://dependabot.com)
[![Mutation testing badge](https://img.shields.io/endpoint?style=plastic&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmthmulders%2Fcolumbia%2Fmain)](https://dashboard.stryker-mutator.io/reports/github.com/mthmulders/columbia/main)
[![](https://img.shields.io/github/license/mthmulders/columbia.svg)](./LICENSE)

Columbia is a web application to manage your AWS S3 Glacier “cold data” backups.
It is named after the [Columbia Icefield](https://en.wikipedia.org/wiki/Columbia_Icefield) glacier in British Columbia, Canada.

## Development
The application requires Java 15 for development.
Prepare a database by opening a terminal in the **local/** folder and start `docker-compose up`.

Open a new terminal and run
```sh
mvn spring-boot:run -Dspring-boot.run.profiles=local
```