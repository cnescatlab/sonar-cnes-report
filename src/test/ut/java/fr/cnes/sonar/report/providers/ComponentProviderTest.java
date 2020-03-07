package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class ComponentProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test
    public void createEmptyComponentProvider() {
        ComponentProvider componentProvider = new ComponentProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        assertTrue(componentProvider.getMetricStats().isEmpty());
    }

    @Test(expected = SonarQubeException.class)
    public void createFaultyComponentProvider() throws SonarQubeException, BadSonarQubeRequestException {
        ComponentProvider componentProvider = new ComponentProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        componentProvider.getComponents();
    }

}