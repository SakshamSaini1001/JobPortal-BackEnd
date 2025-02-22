FROM maven:3.9.9 AS build
COPY . .
RUN mvn clean package -DskipTests
FROM openjdk:21-jdk
COPY --from=build target/jobportal/0.0.1-SNAPSHOT.jar JobPortal.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "JobPortal.jar"]
