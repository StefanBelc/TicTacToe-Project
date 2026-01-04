FROM ubuntu:latest
LABEL authors="Stefan&Elena"

ENTRYPOINT ["top", "-b"]

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]