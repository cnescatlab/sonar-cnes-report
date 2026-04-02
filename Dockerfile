# syntax=docker/dockerfile:1

ARG DOCKERHUB_REGISTRY=docker.io

FROM $DOCKERHUB_REGISTRY/maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package

FROM $DOCKERHUB_REGISTRY/openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/sonar-cnes-report-*.jar ./sonar-cnes-report.jar

ENTRYPOINT ["java", "-jar", "sonar-cnes-report.jar"]