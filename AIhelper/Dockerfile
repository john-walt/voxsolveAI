# Step 1: Build the app
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY AIhelper /app/AIhelper
WORKDIR /app/AIhelper
RUN mvn clean package -DskipTests

# Step 2: Run the app
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/AIhelper/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]