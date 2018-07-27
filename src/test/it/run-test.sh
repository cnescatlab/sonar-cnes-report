#!/bin/bash -e

cp src/main/resources/template/code-analysis-template.docx src/main/resources/template/issues-template.xlsx target
cd target
java -jar cnesreport.jar -p fr.cnes.sonar:cnesreport -s http://localhost:9000
