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

package fr.cnes.sonar.report.providers.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Measure;

public class AbstractMeasureProviderTest {

    @Test
    public void getEmptyMeasuresTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Create empty measures
        JsonObject component = new JsonObject();
        component.add("measures", new JsonArray());
        JsonObject measures = new JsonObject();
        measures.add("component", component);

        MeasureProviderWrapper provider = new MeasureProviderWrapper();
        provider.setFakeMeasures(measures);

        List<Measure> result = provider.getMeasures();
        assertEquals(0, result.size());
    }

    @Test
    public void getMeasuresTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Create measures to retrieve
        JsonObject measure1 = new JsonObject();
        measure1.addProperty("metric", "metric_1");
        measure1.addProperty("value", "value_1");
        JsonObject measure2 = new JsonObject();
        measure2.addProperty("metric", "metric_2");
        measure2.addProperty("value", "value_2");
        JsonArray measuresList = new JsonArray();
        measuresList.add(measure1);
        measuresList.add(measure2);
        JsonObject component = new JsonObject();
        component.add("measures", measuresList);
        JsonObject measures = new JsonObject();
        measures.add("component", component);

        MeasureProviderWrapper provider = new MeasureProviderWrapper();
        provider.setFakeMeasures(measures);

        List<Measure> result = provider.getMeasures();
        assertEquals(2, result.size());
    }

    /**
     * Test class in order to test the abstract provider class
     */
    class MeasureProviderWrapper extends AbstractMeasureProvider {

        JsonObject measures;

        public MeasureProviderWrapper() {
            super("server", "token", "project", "branch");
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeMeasures(JsonObject pFake) {
            this.measures = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getMeasuresAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
            return this.measures;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public List<Measure> getMeasures() throws BadSonarQubeRequestException, SonarQubeException {
            return getMeasuresAbstract();
        }

    }
}
