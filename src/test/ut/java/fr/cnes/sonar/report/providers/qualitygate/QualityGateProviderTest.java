package fr.cnes.sonar.report.providers.qualitygate;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class QualityGateProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetQualityGatesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        QualityGateProvider qualityGateProvider = new QualityGateProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        qualityGateProvider.getQualityGates();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetQualityGateStatusStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        QualityGateProvider qualityGateProvider = new QualityGateProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        qualityGateProvider.getQualityGateStatus();
    }


}