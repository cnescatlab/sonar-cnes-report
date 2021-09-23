package fr.cnes.sonar.report.providers.component;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class ComponentProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetComponentsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        ComponentProvider componentProvider = new ComponentProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        componentProvider.getComponents();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetComponentsPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        ComponentProvider componentProvider = new ComponentProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        componentProvider.getComponents();
    }
}