#!/bin/bash
set -ev

if [ $sonarqube != "none" ]; then
    mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package;
    chmod +x src/test/it/run-test.sh;mvn clean package;
    echo "Starting docker";
    docker run --name sonarqube_${sonarqube} -d -p 127.0.0.1:9000:9000/tcp sonarqube:${sonarqube};
    echo "Inject plugin";
    docker cp target/sonar-cnes-report.jar sonarqube_${sonarqube}:/opt/sonarqube/extensions/plugins/;
    docker exec sonarqube_${sonarqube} chmod 777 /opt/sonarqube/extensions/plugins/sonar-cnes-report.jar;
    docker restart sonarqube_${sonarqube};
    echo "Waiting 5 minutes for SonarQube...";
    sleep 300;
    mvn sonar:sonar -Dsonar.host.url=http://127.0.0.1:9000 -Dsonar.login=admin -Dsonar.password=admin -Dsonar.organization=default-organization -Dsonar.branch.name= ;
    src/test/it/run-test.sh;
    wget "http://localhost:9000/api/cnesreport/report?key=fr.cnes.sonar%3Acnesreport&author=travis-ci&token=noauth";
else 
    mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar;
fi