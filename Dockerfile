# --- Stage 1: Build the application using Maven ---
# Use a base image with JDK. eclipse-temurin is a good choice.
# Make sure the Java version (e.g., 17) matches your project's requirement (Spring Boot 3.x needs Java 17+).
FROM eclipse-temurin:17-jdk AS builder

# Set the working directory inside the builder stage
WORKDIR /workspace/app

# Copy the Maven wrapper scripts and configuration
# These files should be in the root of your project and committed to Git
COPY mvnw .
COPY .mvn .mvn

# Make the Maven wrapper script executable
# This is the fix for the "Permission denied" error
RUN chmod +x ./mvnw

# Copy the pom.xml to download dependencies first (leverages Docker layer caching)
COPY pom.xml .
# Download all project dependencies
RUN ./mvnw dependency:go-offline -B

# Copy your application's source code
COPY src src

# Build the application, creating the executable JAR.
# -DskipTests skips running tests during this Docker build to make it faster.
RUN ./mvnw package -DskipTests


# --- Stage 2: Create the final, smaller runtime image ---
# Use a JRE (Java Runtime Environment) base image, which is smaller than a JDK image.
# Alpine Linux based images are very small, which is good for production.
FROM eclipse-temurin:17-jre-alpine

# Set the working directory inside the final image
WORKDIR /app

# Copy only the executable JAR from the builder stage to the final image
# Ensure 'target/*.jar' matches the location and naming pattern of your built JAR file by Maven.
COPY --from=builder /workspace/app/target/*.jar app.jar

# Expose the port your Spring Boot application listens on (usually 8080)
EXPOSE 8080

# Define the command to run your application when the container starts
ENTRYPOINT ["java","-jar","/app/app.jar"]