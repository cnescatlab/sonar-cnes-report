#!/bin/bash -e

cp src/resources/template/*template* target
cd target
java -jar sonar-cnes-report.jar --sonar.project.id cnesreport --sonar.url http://localhost:9000