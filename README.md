# AdVibe - Real-Time Impression & Click Processing

## Overview
AdVibe is a service built using **Spring Boot** and **Kotlin** to process advertising events. The system handles two types of events:
- **Ad Impression**: Marks the start of an ad display.
- **Ad Click**: Occurs when a user clicks on an ad.

The service consumes these events from **Kafka** and stores them in **Cassandra**. It supports real-time updates to impression events when a corresponding click event occurs.

## Features
- **Event-driven architecture**: Uses Kafka for message processing.
- **Cassandra integration**: Stores and updates advertising events.

## Technology Stack
- **Kotlin** (1.9.25)
- **Spring Boot** (3.3.4)
- **Kafka** for message queuing
- **Cassandra** for distributed storage

## Requirements
- **Java 21**
- **Kafka 2.x**
- **Cassandra 3.x or 4.x**

## Running the Application

1. Set up **Kafka** and **Cassandra**.
2. Clone the repository and navigate to the project directory.
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing
Unit and integration tests are included. Use the following command to run the test suite:
```bash
mvn test
```

## Configuration
Application configurations, including Kafka and Cassandra settings, can be found in the `application.yml` file.
