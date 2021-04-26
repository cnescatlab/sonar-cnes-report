package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class SecurityHotspotsProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetToReviewSecurityHotspots() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        securityHotspotsProvider.getToReviewSecurityHotspots();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetReviewedSecurityHotspots() throws SonarQubeException, BadSonarQubeRequestException {
        SecurityHotspotsProvider securityHotspotsProvider = new SecurityHotspotsProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        securityHotspotsProvider.getReviewedSecurityHotspots();
    }
}