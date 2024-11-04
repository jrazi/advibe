# AdVibe - Real-Time Impression & Click Processing

## Overview
AdVibe is a high-performance service designed to process advertising events in real-time. Built with **Spring Boot** and **Kotlin**, AdVibe efficiently handles two primary event types:
- **Ad Impression**: Marks the start of an ad display.
- **Ad Click**: Occurs when a user interacts with an ad by clicking on it.

Leveraging **Kafka** for message queuing and **Cassandra** for distributed storage, AdVibe ensures scalability, reliability, and low-latency processing of millions of events per second.

## Adstream Module

### Flexible and Configurable Design
The Adstream module is the core component responsible for generating and managing advertising events. Its architecture emphasizes flexibility and configurability, allowing it to adapt to varying workloads and business requirements seamlessly.

### Statistical Modeling
Adstream employs robust statistical methods to accurately model user interactions and system behavior:
- **Click-Through Rate (CTR)**: Configurable to simulate the probability of a user clicking an ad.
- **Click Timing**: Utilizes the Weibull distribution to model the time between an ad impression and a user click, accommodating different engagement scenarios.
- **Network Latency Simulation**: Implements Gaussian distribution to realistically simulate network delays, ensuring accurate event processing under varying conditions.

### Efficient Data Handling with Cassandra
Adstream leverages Cassandra's strengths to manage high-volume advertising data:
- **Data Modeling**: Combines `app_id` and `ad_id` as the partition key to ensure related events are stored together, facilitating efficient upserts and merges.
- **Cluster Keys**: Incorporates `timestamp` to maintain chronological order within partitions, optimizing read and write operations.
- **Bloom Filters**: Configured with a false positive rate of 0.001 to minimize unnecessary disk reads, enhancing read performance.
- **Consistency Level**: Set to `local_quorum` to balance data consistency with system performance, ensuring reliable data availability.

### Modular Architecture
Adstream follows Domain-Driven Design (DDD) principles, organizing the codebase into distinct modules that encapsulate specific functionalities. This modular approach promotes maintainability, scalability, and ease of development.

### Observability and Monitoring
- **Health Checks**: Utilizes Spring Boot Actuator to monitor the health of Kafka and Cassandra integrations, enabling proactive issue detection and resolution.

## Technology Stack
- **Kotlin** (2.0)
- **Spring Boot** (3.3.4)
- **Kafka** for message queuing
- **Cassandra** 5.0 for distributed storage

## Requirements
- **Java 21**
- **Kafka 2.x**
- **Cassandra 5.0**

## Running the Application
1. **Set up Kafka and Cassandra** according to your environment specifications.
2. **Clone the repository** and navigate to the project directory:
    ```bash
    git clone https://github.com/your-repo/advibe.git
    cd advibe
    ```
3. **Build the project** using Maven:
    ```bash
    mvn clean install
    ```
4. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

## Testing
AdVibe includes both unit and integration tests to ensure reliability and performance. Run the tests using:
```bash
mvn test
```

## Configuration
All application configurations, including Kafka and Cassandra settings, are managed via the `application.yaml` file. This setup allows for easy adjustments and environment-specific configurations without modifying the codebase.

