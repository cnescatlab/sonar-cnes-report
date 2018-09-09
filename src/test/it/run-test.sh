#!/bin/bash -e

cp src/main/resources/template/code-analysis-template.docx src/main/resources/template/issues-template.xlsx target
cd target
java -jar sonar-cnes-report.jar --sonar.project.id cnesreport --sonar.url http://localhost:9000