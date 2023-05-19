FROM bitnami/java:11-debian-11

LABEL org.opencontainers.image.source https://github.com/ivanmorenoj/sonar-cnes-report

ARG SONAR_CNES_REPORT_VERSION=4.2.0

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    jq python3-pip python3 curl wget openssh-client \
    git git-lfs sqlite3 && \
    pip3 install --upgrade grip && \
    apt-get clean all -y && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

RUN curl -fsSL \
    https://github.com/cnescatlab/sonar-cnes-report/releases/download/${SONAR_CNES_REPORT_VERSION}/sonar-cnes-report-${SONAR_CNES_REPORT_VERSION}.jar \
    -o /usr/local/bin/sonar-cnes-report.jar 
