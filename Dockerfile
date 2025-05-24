# --- Stage 1: Build the application using Maven (or Gradle) ---
# Use a base image with JDK and Maven/Gradle build tools
# Make sure the Java version (e.g., 17) matches your project's requirement
FROM eclipse-temurin:17-jdk AS builder

# Set the working directory
WORKDIR /workspace/app

# Copy Maven wrapper files (if you use ./mvnw)
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml to download dependencies first (layer caching)
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
# For Gradle, you would copy build.gradle, settings.gradle, gradlew, gradle/
# COPY build.gradle settings.gradle gradlew ./
# COPY gradle ./gradle
# RUN ./gradlew build --no-daemon or dependencies

# Copy the source code
COPY src src

# Build the application, creating the executable JAR. Skip tests for faster builds.
RUN ./mvnw package -DskipTests
# For Gradle: RUN ./gradlew build -x test --no-daemon


# --- Stage 2: Create the final, smaller runtime image ---
# Use a JRE (Java Runtime Environment) base image, which is smaller than a JDK image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the builder stage
# Adjust the path if your JAR is named differently or in a different subfolder of target/build
COPY --from=builder /workspace/app/target/*.jar app.jar
# For Gradle, it might be: COPY --from=builder /workspace/app/build/libs/*.jar app.jar

# Expose the port your Spring Boot application listens on (usually 8080)
EXPOSE 8080

# Define the command to run your application when the container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]