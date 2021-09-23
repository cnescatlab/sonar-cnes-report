package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.providers.sonarqubeinfo.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class SonarQubeInfoProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetSonarQubeVersionStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        SonarQubeInfoProvider sonarQubeInfoProvider = new SonarQubeInfoProviderStandalone(sonarQubeServer, TOKEN);
        sonarQubeInfoProvider.getSonarQubeVersion();
    }

    @Test
    public void executeFaultyGetStatusStandalone() {
        SonarQubeInfoProvider sonarQubeInfoProvider = new SonarQubeInfoProviderStandalone(sonarQubeServer, TOKEN);
        final String actual = sonarQubeInfoProvider.getSonarQubeStatus();
        final String expected = "DOWN";
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetSonarQubeVersionPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        SonarQubeInfoProvider sonarQubeInfoProvider = new SonarQubeInfoProviderPlugin(wsClient);
        sonarQubeInfoProvider.getSonarQubeVersion();
    }

    @Test
    public void executeFaultyGetStatusPlugin() {
        SonarQubeInfoProvider sonarQubeInfoProvider = new SonarQubeInfoProviderPlugin(wsClient);
        final String actual = sonarQubeInfoProvider.getSonarQubeStatus();
        final String expected = "DOWN";
        assertEquals(expected, actual);
    }
}