package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class QualityGateProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetQualityGates() throws SonarQubeException, BadSonarQubeRequestException {
        QualityGateProvider qualityGateProvider = new QualityGateProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        qualityGateProvider.getQualityGates();
    }

}