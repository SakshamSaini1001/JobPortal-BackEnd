FROM maven:3.9.9 AS build
COPY . .
RUN mvn clean package -DskipTests
FROM openjdk:23-ea-1-jdk-slim
COPY --from=build /target/jobportal/0.0.1-SNAPSHOT.jar JobPoraal.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "JobPortal.jar"]