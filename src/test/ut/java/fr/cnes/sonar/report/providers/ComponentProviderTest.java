package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.SonarQubeServer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void isCountableMetricTest() {
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Map<String,String> component = new HashMap<>();
        ComponentProviderWrapper componentProvider = new ComponentProviderWrapper(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        componentProvider.setComponentsList(componentsTest);

        // List is empty
        assertFalse(componentProvider.isCountableMetricPublic("Test"));

        // Metric should be excluded
        assertFalse(componentProvider.isCountableMetricPublic("Name"));

        // Map is null
        componentsTest.add(null);
        componentProvider.setComponentsList(componentsTest);
        assertFalse(componentProvider.isCountableMetricPublic("Test"));

        // Component value is null
        componentsTest.clear();
        component.put("Test", null);
        componentsTest.add(component);
        componentProvider.setComponentsList(componentsTest);
        assertFalse(componentProvider.isCountableMetricPublic("Test"));

        // Component is found and contains a valid value
        componentsTest.clear();
        component.clear();
        component.put("Name", "Test");
        component.put("Test", "Value");
        componentsTest.add(component);
        componentProvider.setComponentsList(componentsTest);
        assertFalse(componentProvider.isCountableMetricPublic("Test"));
    }

    @Test
    public void getMinMaxMetricTest() {
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Map<String,String> component1 = new HashMap<>();
        Map<String,String> component2 = new HashMap<>();
        Map<String,String> component3 = new HashMap<>();
        ComponentProviderWrapper componentProvider = new ComponentProviderWrapper(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        componentProvider.setComponentsList(componentsTest);

        // Test data
        component1.put("Test", "42.42");
        component2.put("Test", null);
        component3.put("Test", "24");
        componentsTest.add(component1);
        componentsTest.add(component2);
        componentsTest.add(component3);
        componentProvider.setComponentsList(componentsTest);

        double expectedMin = 24;
        double expectedMax = 42.42;
        assertEquals(expectedMin, componentProvider.getMinMetricPublic("Test"));
        assertEquals(expectedMax, componentProvider.getMaxMetricPublic("Test"));
    }

    @Test
    public void getMetricStatsTest() {
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Map<String,String> component1 = new HashMap<>();
        Map<String,String> component2 = new HashMap<>();
        Map<String,String> component3 = new HashMap<>();
        ComponentProviderWrapper componentProvider = new ComponentProviderWrapper(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);

        // Test data to match every possible case
        component1.put("Test", "42.42");
        component1.put("Other", "1");
        component2.put("Other", "3");
        component2.put("Test", null);
        component2.put("Name", "ToBeExcluded");
        component3.put("Test", "24");
        component3.put("Other", "2");
        component3.put("Alone", "0");
        componentsTest.add(component1);
        componentsTest.add(component2);
        componentsTest.add(component3);
        componentProvider.setComponentsList(componentsTest);

        Map<String, Double> expected = new HashMap<String, Double>();
        expected.put("minTest", 24.0);
        expected.put("maxTest", 42.42);
        expected.put("minOther", 1.0);
        expected.put("maxOther", 3.0);
        expected.put("minAlone", 0.0);
        expected.put("maxAlone", 0.0);

        assertEquals(expected,componentProvider.getMetricStats());
    }

    /**
     * Wrapper on ComponentProvider for testing purposes
     */
    private class ComponentProviderWrapper extends ComponentProvider {

        public ComponentProviderWrapper(final SonarQubeServer server, final String token, final String project,
            final String branch) {
            super(server, token, project, branch);
        }

        public void setComponentsList(ArrayList<Map<String,String>> componentsTest) {
            componentsList = componentsTest;
        }

        public boolean isCountableMetricPublic(String metric){ 
            return isCountableMetric(metric);
        }

        public double getMinMetricPublic(String metric){ 
            return getMinMetric(metric);
        }

        public double getMaxMetricPublic(String metric){ 
            return getMaxMetric(metric);
        }
    }

}