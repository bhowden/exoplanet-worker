# Use the official Maven image as the build stage
FROM maven:3.8.1-jdk-11-slim AS build

# Set the working directory in Docker
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml ./

# Download dependencies as specified in pom.xml
RUN mvn dependency:go-offline

# Copy the rest of the application code
COPY src ./src/

# Package the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as the runtime stage
FROM openjdk:11-jre-slim

# Set the working directory in Docker
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/exoplanet-worker-0.0.1-SNAPSHOT.jar ./app.jar

# Specify the command to run
CMD ["java", "-jar", "./app.jar"]
