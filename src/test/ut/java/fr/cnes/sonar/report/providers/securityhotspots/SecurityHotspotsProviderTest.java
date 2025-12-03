package fr.cnes.sonar.report.providers.securityhotspots;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class SecurityHotspotsProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetToReviewSecurityHotspotsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, ENABLE_ISSUES_MULTI_REQUESTS, MAX_URL_SIZE);
        securityHotspotsProvider.getToReviewSecurityHotspots();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetReviewedSecurityHotspotsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, ENABLE_ISSUES_MULTI_REQUESTS, MAX_URL_SIZE);
        securityHotspotsProvider.getReviewedSecurityHotspots();
    }

}