# Stage 1: Build the application using Maven
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Use a lightweight alpine JRE as the final base image
FROM amazoncorretto:21-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xms512m -Xmx1024m" \
    APP_HOME="/app" \
    JAR_FILE="app.jar"

# Install curl for health checks
RUN apk add --no-cache curl

# Create and switch to a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p $APP_HOME && chown -R appuser:appgroup $APP_HOME
USER appuser

WORKDIR $APP_HOME

COPY --from=builder /app/target/*.jar $JAR_FILE

EXPOSE 8080

# Health check to monitor the application status
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar $JAR_FILE"]
