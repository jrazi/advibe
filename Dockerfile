# Stage 1: Build the application
FROM maven:3.8.6-openjdk-21-slim AS build

WORKDIR /app

# Copy Maven files
COPY pom.xml mvnw .mvn/ /app/

# Download Maven dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src /app/src

# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Create the runtime container
FROM openjdk:21-jdk-slim

LABEL maintainer="javad.razigiglou@gmail.com"
LABEL version="0.0.1"
LABEL description="AdVibe - Real-Time Impression and Click Processing Service"

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/advibe-0.0.1-SNAPSHOT.jar /app/advibe.jar

# Expose the application port
EXPOSE 8080

# Health check to ensure the app is running
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1

# Environment variables for Kafka, Cassandra, and JVM settings
ENV KAFKA_HOST=kafka
ENV KAFKA_PORT=9092
ENV CASSANDRA_HOST=cassandra
ENV CASSANDRA_PORT=9042
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Add a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Set permissions
RUN chown -R spring:spring /app
USER spring:spring

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/advibe.jar"]
