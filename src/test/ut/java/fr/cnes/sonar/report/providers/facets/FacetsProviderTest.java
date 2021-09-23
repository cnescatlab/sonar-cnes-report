package fr.cnes.sonar.report.providers.facets;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class FacetsProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetFacetsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        FacetsProvider facetsProvider = new FacetsProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        facetsProvider.getFacets();
    }    

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetFacetsPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        FacetsProvider facetsProvider = new FacetsProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        facetsProvider.getFacets();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetTimeFacetsStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        FacetsProvider facetsProvider = new FacetsProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        facetsProvider.getTimeFacets();
    }    

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetTimeFacetsPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        FacetsProvider facetsProvider = new FacetsProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        facetsProvider.getTimeFacets();
    }
}