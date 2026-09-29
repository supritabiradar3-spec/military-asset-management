# Stage 1: Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and resolve dependencies to leverage layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Production Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root system user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy compiled JAR from build stage
COPY --from=build /app/target/assetmanagement-0.0.1-SNAPSHOT.jar app.jar

# Render injects PORT dynamically; 8080 is documented default
EXPOSE 8080

# Execute Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
