
# Stage 1: Build the application using Maven and Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy Maven configuration first to improve dependency caching
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests


# Stage 2: Run the application using a smaller Java runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Install curl for the Docker health check
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Create a non-root user for improved security
RUN useradd --system --uid 10001 appuser

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the Spring Boot application port
EXPOSE 8080

# Run the application as a non-root user
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
