FROM bitnami/java:11-debian-11

LABEL org.opencontainers.image.source https://github.com/ivanmorenoj/sonar-cnes-report

ARG SONAR_CNES_REPORT_VERSION=4.2.0

RUN curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | \
      dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg && \
    chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" > /etc/apt/sources.list.d/github-cli.list && \
    apt-get update && \
    apt-get install -y \
      jq python3-pip python3 curl wget openssh-client \
      git git-lfs sqlite3 bc gh && \
    pip3 install --upgrade \
      grip csv2md python-sonarqube-api && \
    apt-get clean all -y && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

RUN curl -fsSL \
    https://github.com/cnescatlab/sonar-cnes-report/releases/download/${SONAR_CNES_REPORT_VERSION}/sonar-cnes-report-${SONAR_CNES_REPORT_VERSION}.jar \
    -o /usr/local/bin/sonar-cnes-report.jar 
