package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.providers.measure.*;

import org.junit.Test;

public class MeasureProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetMeasures() throws SonarQubeException, BadSonarQubeRequestException {
        MeasureProvider measureProvider = new MeasureProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        measureProvider.getMeasures();
    }    
}