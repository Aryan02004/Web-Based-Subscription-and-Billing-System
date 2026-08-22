# --- Build stage ---
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -q -DskipTests clean package

# --- Runtime stage ---
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user
RUN useradd -r -u 1001 spring && chown -R spring:spring /app
USER spring


# Copy the built jar
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8080

ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
