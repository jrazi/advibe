# Stage 1: Build the application using Maven
FROM maven:3.9.9-amazoncorretto-21 AS builder

WORKDIR /app

# So we don't re-download a ton of dependencies each time we build the image.
COPY pom.xml ./

RUN mvn dependency:go-offline -B

# Rest of the application source code
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Use a lightweight alpine JRE as the final base image
FROM amazoncorretto:21-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xms512m -Xmx1024m" \
    APP_HOME="/app" \
    JAR_FILE="app.jar"

# Create and switch to a non-root user (security duh)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR $APP_HOME

COPY --from=builder /app/target/advibe-0.0.1-SNAPSHOT.jar $JAR_FILE

EXPOSE 8080

# Health check to monitor the application status
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
