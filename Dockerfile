FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/sonar-cnes-report.jar

# cd /usr/local/export
WORKDIR /usr/local/export

# copy target/sonar-cnes-report.jar /usr/local/export/sonar-report.jar
COPY ${JAR_FILE} sonar-report.jar

# ENTRYPOINT [ "java", "-jar", "sonar-report.jar" ]