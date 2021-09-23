package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.CommonTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComponentsTest extends CommonTest {

    @Test
    public void createEmptyComponents() {
        Components components = new Components();
        assertTrue(components.getMetricStats().isEmpty());
    }

    @Test
    public void isCountableMetricTest() {
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Map<String,String> component = new HashMap<>();
        ComponentsWrapper components = new ComponentsWrapper();
        components.setComponentsList(componentsTest);

        // List is empty
        assertFalse(components.isCountableMetricPublic("Test"));

        // Metric should be excluded
        assertFalse(components.isCountableMetricPublic("Name"));

        // Map is null
        componentsTest.add(null);
        components.setComponentsList(componentsTest);
        assertFalse(components.isCountableMetricPublic("Test"));

        // Component value is null
        componentsTest.clear();
        component.put("Test", null);
        componentsTest.add(component);
        components.setComponentsList(componentsTest);
        assertFalse(components.isCountableMetricPublic("Test"));

        // Component is found and contains a valid value
        componentsTest.clear();
        component.clear();
        component.put("Name", "Test");
        component.put("Test", "Value");
        componentsTest.add(component);
        components.setComponentsList(componentsTest);
        assertFalse(components.isCountableMetricPublic("Test"));
    }

    @Test
    public void getMinMaxMedianMetricTest() {
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Map<String,String> component1 = new HashMap<>();
        Map<String,String> component2 = new HashMap<>();
        Map<String,String> component3 = new HashMap<>();
        ComponentsWrapper components = new ComponentsWrapper();
        components.setComponentsList(componentsTest);

        // Test data
        component1.put("Test", "42.42");
        component2.put("Test", null);
        component3.put("Test", "24");
        componentsTest.add(component1);
        componentsTest.add(component2);
        componentsTest.add(component3);
        components.setComponentsList(componentsTest);

        double expectedMin = 24;
        double expectedMax = 42.42;
        double expectedMedian = 33.2;
        assertEquals(expectedMin, components.getMinMetricPublic("Test"));
        assertEquals(expectedMax, components.getMaxMetricPublic("Test"));
        assertEquals(expectedMedian, components.getMedianMetricPublic("Test"));
    }

    @Test
    public void getMetricStatsTest() {
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Map<String,String> component1 = new HashMap<>();
        Map<String,String> component2 = new HashMap<>();
        Map<String,String> component3 = new HashMap<>();
        ComponentsWrapper components = new ComponentsWrapper();

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
        components.setComponentsList(componentsTest);

        Map<String, Double> expected = new HashMap<String, Double>();
        expected.put("minTest", 24.0);
        expected.put("maxTest", 42.42);
        expected.put("medianTest", 33.2);
        expected.put("minOther", 1.0);
        expected.put("maxOther", 3.0);
        expected.put("medianOther", 2.0);
        expected.put("minAlone", 0.0);
        expected.put("maxAlone", 0.0);
        expected.put("medianAlone", 0.0);

        assertEquals(expected,components.getMetricStats());
    }

    /**
     * Wrapper on Components for testing purposes
     */
    private class ComponentsWrapper extends Components {

        public boolean isCountableMetricPublic(String metric){ 
            return isCountableMetric(metric);
        }

        public double getMinMetricPublic(String metric){ 
            return getMinMetric(metric);
        }

        public double getMaxMetricPublic(String metric){ 
            return getMaxMetric(metric);
        }

        public double getMedianMetricPublic(String metric){ 
            return getMedianMetric(metric);
        }
    }
}