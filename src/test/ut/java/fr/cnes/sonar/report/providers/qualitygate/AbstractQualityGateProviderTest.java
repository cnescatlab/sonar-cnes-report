/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.providers.qualitygate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;

public class AbstractQualityGateProviderTest {

    @Test
    public void getQualityGatesEmptyTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Empty API response
        JsonObject empty = new JsonObject();
        empty.addProperty("default", "empty");
        empty.add("qualitygates", new JsonArray());

        // Call the wrapper to test
        QualityGateProviderWrapper provider = new QualityGateProviderWrapper();
        provider.setFakeQualityGates(empty);
        List<QualityGate> qualityGateList = provider.getQualityGates();
        assertEquals(0, qualityGateList.size());
    }

    @Test
    public void getQualityGatesWithCorrespondingDetailsTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Fake API response
        JsonObject qualityGate1 = new JsonObject();
        qualityGate1.addProperty("id", "test1");
        JsonObject qualityGate2 = new JsonObject();
        qualityGate2.addProperty("id", "test2");
        
        JsonArray qualityGateList = new JsonArray();
        qualityGateList.add(qualityGate1);
        qualityGateList.add(qualityGate2);
        
        JsonObject qualityGatesResponse = new JsonObject();
        qualityGatesResponse.addProperty("default", "test1");
        qualityGatesResponse.add("qualitygates", qualityGateList);

        // Call the wrapper to test
        QualityGateProviderWrapper provider = new QualityGateProviderWrapper();
        provider.setFakeQualityGates(qualityGatesResponse);
        provider.setFakeQualityGatesDetails(new JsonObject());
        List<QualityGate> list = provider.getQualityGates();
        assertEquals(2, list.size());
        assertFalse(list.get(0).getConf().isEmpty());
        assertFalse(list.get(1).getConf().isEmpty());
        assertTrue(list.get(0).isDefault());
        assertFalse(list.get(1).isDefault());
    }

    @Test(expected = UnknownQualityGateException.class)
    public void getProjectNoQualityGateTest() throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {
        // Empty API response
        JsonObject empty = new JsonObject();
        empty.addProperty("default", "empty");
        empty.add("qualitygates", new JsonArray());

        JsonObject qualityGateProperty = new JsonObject();
        qualityGateProperty.addProperty("key", "no_matching_key");
        JsonObject project = new JsonObject();
        project.add("qualityGate", qualityGateProperty);

        // Test with an empty list of quality gates
        QualityGateProviderWrapper provider = new QualityGateProviderWrapper();
        provider.setFakeQualityGates(empty);
        provider.setFakeQualityGatesDetails(new JsonObject());
        provider.setFakeProject(project);

        provider.getProjectQualityGate();
    }

    @Test(expected = UnknownQualityGateException.class)
    public void getProjectNoMatchingQualityGateTest() throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {
        // Fake API response
        JsonObject qualityGate1 = new JsonObject();
        qualityGate1.addProperty("id", "test1");
        JsonObject qualityGate2 = new JsonObject();
        qualityGate2.addProperty("id", "test2");
        
        JsonArray qualityGateList = new JsonArray();
        qualityGateList.add(qualityGate1);
        qualityGateList.add(qualityGate2);
        
        JsonObject qualityGatesResponse = new JsonObject();
        qualityGatesResponse.addProperty("default", "test1");
        qualityGatesResponse.add("qualitygates", qualityGateList);

        JsonObject qualityGateProperty = new JsonObject();
        qualityGateProperty.addProperty("key", "no_matching_key");
        JsonObject project = new JsonObject();
        project.add("qualityGate", qualityGateProperty);

        // Test with an empty list of quality gates
        QualityGateProviderWrapper provider = new QualityGateProviderWrapper();
        provider.setFakeQualityGates(qualityGatesResponse);
        provider.setFakeQualityGatesDetails(new JsonObject());
        provider.setFakeProject(project);

        provider.getProjectQualityGate();
    }

    @Test
    public void getProjectWithMatchingQualityGateTest() throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {
        // Fake API response
        JsonObject qualityGate1 = new JsonObject();
        qualityGate1.addProperty("id", "test1");
        JsonObject qualityGate2 = new JsonObject();
        qualityGate2.addProperty("id", "test2");
        JsonObject qualityGate3 = new JsonObject();
        qualityGate3.addProperty("id", "test3");
        
        JsonArray qualityGateList = new JsonArray();
        qualityGateList.add(qualityGate1);
        qualityGateList.add(qualityGate2);
        qualityGateList.add(qualityGate3);
        
        JsonObject qualityGatesResponse = new JsonObject();
        qualityGatesResponse.addProperty("default", "test1");
        qualityGatesResponse.add("qualitygates", qualityGateList);

        JsonObject qualityGateProperty = new JsonObject();
        qualityGateProperty.addProperty("key", "test2");
        JsonObject project = new JsonObject();
        project.add("qualityGate", qualityGateProperty);

        // Test with a list a matching quality gate
        QualityGateProviderWrapper provider = new QualityGateProviderWrapper();
        provider.setFakeQualityGates(qualityGatesResponse);
        provider.setFakeQualityGatesDetails(new JsonObject());
        provider.setFakeProject(project);

        QualityGate result = provider.getProjectQualityGate();
        assertEquals("test2", result.getId());
    }

    @Test
    public void getQualityGateStatusTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Create fake quality gate status
        JsonObject condition1 = new JsonObject();
        condition1.addProperty("status", "SUCCESS");
        condition1.addProperty("metricKey", "metric_1");
        condition1.addProperty("actualValue", "20");
        condition1.addProperty("errorThreshold", "10");
        condition1.addProperty("comparator", "LT");
        JsonObject condition2 = new JsonObject();
        condition2.addProperty("status", "ERROR");
        condition2.addProperty("metricKey", "metric_2");
        condition2.addProperty("actualValue", "100000");
        condition2.addProperty("errorThreshold", "10");
        condition2.addProperty("comparator", "GT");
        JsonArray conditions = new JsonArray();
        conditions.add(condition1);
        conditions.add(condition2);
        JsonObject status = new JsonObject();
        status.add("conditions", conditions);
        JsonObject qualityGateStatus = new JsonObject();
        qualityGateStatus.add("projectStatus", status);

        // Test with a successful quality gate
        QualityGateProviderWrapper provider = new QualityGateProviderWrapper();
        provider.setFakeQualityGateStatus(qualityGateStatus);

        Map<String,String> result = provider.getQualityGateStatus();
        assertEquals(2, result.size());
        for(Map.Entry<String,String> entry: result.entrySet()) {

            assertTrue(entry.getKey().endsWith("_TEST"));

            if (entry.getValue() == "SUCCESS") {
                assertEquals("SUCCESS", entry.getValue());
            } else {                
                assertEquals("ERROR (100000.0% is greater than 10%)", entry.getValue());
            }
        }
    }

    @Test
    public void getErrorExplanationTest() {
        QualityGateProviderWrapper qualityGateProvider = new QualityGateProviderWrapper();

        List<String> actual = new ArrayList<>();
        actual.add(qualityGateProvider.getErrorExplanationPublic("8", "test", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("5", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("4", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("3", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("2", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("30", "0", "GT", "WORK_DUR"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("50.314", "80", "LT", "PERCENT"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("10000", "5000", "GT", "MILLISEC"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("3", "0", "GT", "INT"));

        List<String> expected = new ArrayList<>();
        expected.add(" (8 is worse than test)");
        expected.add(" (E is worse than A)");
        expected.add(" (D is worse than A)");
        expected.add(" (C is worse than A)");
        expected.add(" (B is worse than A)");
        expected.add(" (0d 0h 30min is greater than 0d 0h 0min)");
        expected.add(" (50.3% is less than 80%)");
        expected.add(" (10000ms is greater than 5000ms)");
        expected.add(" (3 is greater than 0)");

        assertEquals(expected, actual);
    }

    /**
     * Wrapper on QualityGateProvider for testing purposes
     */
    private class QualityGateProviderWrapper extends AbstractQualityGateProvider {

        // Stores the fake JsonObject responses to mock the API
        private JsonObject fakeQualityGates;
        private JsonObject fakeQualityGatesDetails;
        private JsonObject fakeProject;
        private JsonObject fakeQualityGateStatus;

        public QualityGateProviderWrapper() {
            super("server", "token", "project", "branch");
        }

        /**
         * Sets fake JsonObjects that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeQualityGates(JsonObject pFake) {
            this.fakeQualityGates = pFake;
        }

        public void setFakeQualityGatesDetails(JsonObject pFake) {
            this.fakeQualityGatesDetails = pFake;
        }

        public void setFakeProject(JsonObject pFake) {
            this.fakeProject = pFake;
        }

        public void setFakeQualityGateStatus(JsonObject pFake) {
            this.fakeQualityGateStatus = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getQualityGatesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
            return fakeQualityGates;
        }

        protected JsonObject getQualityGatesDetailsAsJsonObject(final QualityGate qualityGate)
                throws BadSonarQubeRequestException, SonarQubeException {
            return fakeQualityGatesDetails;
        }

        protected JsonObject getProjectAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
            return fakeProject;
        }

        protected JsonObject getQualityGateStatusAsJsonObject()
                throws BadSonarQubeRequestException, SonarQubeException {
            return fakeQualityGateStatus;
        }

        protected JsonObject getMetricAsJsonObject(final String metricKey)
                throws BadSonarQubeRequestException, SonarQubeException {
            // Create fake metric response
            JsonObject metric = new JsonObject();
            metric.addProperty("name", metricKey + "_TEST");
            metric.addProperty("type", "PERCENT");
            JsonArray metrics = new JsonArray();
            metrics.add(metric);
            JsonObject responseMetric = new JsonObject();
            responseMetric.add("metrics", metrics);

            return responseMetric;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public List<QualityGate> getQualityGates() throws BadSonarQubeRequestException, SonarQubeException {
            return getQualityGatesAbstract();
        }

        public QualityGate getProjectQualityGate()
                throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {
            return getProjectQualityGateAbstract();
        }

        public Map<String, String> getQualityGateStatus() throws BadSonarQubeRequestException, SonarQubeException {
            return getQualityGateStatusAbstract();
        }

        public String getErrorExplanationPublic(String actualValue, String errorThreshold, String comparator,
                String type) {
            return getErrorExplanation(actualValue, errorThreshold, comparator, type);
        }
    }
}
