package fr.cnes.sonar.report.providers.securityhotspots;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class SecurityHotspotsProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetToReviewSecurityHotspotsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        securityHotspotsProvider.getToReviewSecurityHotspots();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetReviewedSecurityHotspotsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        securityHotspotsProvider.getReviewedSecurityHotspots();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetToReviewSecurityHotspotsPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        securityHotspotsProvider.getToReviewSecurityHotspots();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetReviewedSecurityHotspotsPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        securityHotspotsProvider.getReviewedSecurityHotspots();
    }
}