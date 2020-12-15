# Columbia

Columbia is a web application to manage your AWS S3 Glacier “cold data” backups.
It is named after the [Columbia Icefield](https://en.wikipedia.org/wiki/Columbia_Icefield) glacier I visited in British Columbia, Canada.

## Development
The application requires Java 15 for development.
Prepare a database by opening a terminal in the **local/** folder and start `docker-compose up`.

Open a new terminal and run
```sh
mvn spring-boot:run -Dspring-boot.run.profiles=local
```