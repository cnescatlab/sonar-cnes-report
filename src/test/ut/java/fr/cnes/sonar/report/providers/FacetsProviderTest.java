package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.providers.facets.*;
import fr.cnes.sonar.report.model.TimeValue;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FacetsProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    /**
     * Fake class to test inherited protected method
     */
    private class FakeAbstractFacetsProvider extends AbstractFacetsProvider {
        public FakeAbstractFacetsProvider() {
            super("server", "token", "project", "branch");
        }

        // Implement necessary abstract method
        protected JsonObject getFacetsAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
            return new JsonObject();
        }
        protected JsonObject getTimeFacetsAsJsonObject(final int page, final int maxPerPage) throws BadSonarQubeRequestException, SonarQubeException {
            return new JsonObject();
        }
    
        // Wrapper for protected method to test
        public List<TimeValue> fakeExtractTimeValuesFromJsonObject(JsonObject jo) {
            return extractTimeValuesFromJsonObject(jo);
        }
    }

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

    @Test
    public void extractTimeValuesFromJsonObjectEmptyTest() {
        FakeAbstractFacetsProvider fakeProvider = new FakeAbstractFacetsProvider();

        JsonObject jo = new JsonObject();
        jo.add("history", new JsonArray());               
        assertEquals(0, fakeProvider.fakeExtractTimeValuesFromJsonObject(jo).size());
    }

    @Test
    public void extractTimeValuesFromJsonObjectNormalTest() {
        FakeAbstractFacetsProvider fakeProvider = new FakeAbstractFacetsProvider();
        
        JsonObject value1 = new JsonObject();
        value1.addProperty("date", "2021-08-11T12:19:49+0000");
        value1.addProperty("value", "5");
        JsonObject value2 = new JsonObject();
        value2.addProperty("date", "2021-08-11T12:19:49+0000");
        value2.addProperty("value", "300.8");
        JsonArray valuesList = new JsonArray();
        valuesList.add(value1);
        valuesList.add(value2);

        JsonObject jo = new JsonObject();
        jo.add("history", valuesList);              
        assertEquals(2, fakeProvider.fakeExtractTimeValuesFromJsonObject(jo).size());
    }
}