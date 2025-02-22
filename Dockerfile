# Stage 1: Build the application
FROM maven:3.9.9 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar  # Copy JAR dynamically

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
