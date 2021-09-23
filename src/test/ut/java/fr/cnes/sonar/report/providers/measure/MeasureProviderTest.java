package fr.cnes.sonar.report.providers.measure;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class MeasureProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetMeasuresStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        MeasureProvider measureProvider = new MeasureProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        measureProvider.getMeasures();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetMeasuresPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        MeasureProvider measureProvider = new MeasureProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        measureProvider.getMeasures();
    }
}