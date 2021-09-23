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

package fr.cnes.sonar.report.providers.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Components;

public class AbstractComponentProviderTest {

    @Test
    public void getEmptyComponentsTest() throws BadSonarQubeRequestException, SonarQubeException {
        // create fake components object
        JsonObject paging = new JsonObject();
        paging.addProperty("total", 0);
        JsonObject components = new JsonObject();
        components.add("components", new JsonArray());
        components.add("paging", paging);

        ComponentProviderWrapper provider = new ComponentProviderWrapper();
        provider.setFakeComponents(components);

        Components result = provider.getComponents();
        assertEquals(0, result.getComponentsList().size());
    }

    @Test
    public void getComponentsTest() throws BadSonarQubeRequestException, SonarQubeException {
        // create fake components object
        JsonObject paging = new JsonObject();
        paging.addProperty("total", 502);
        JsonObject component1 = new JsonObject();
        JsonObject component2 = new JsonObject();
        JsonArray componentsList = new JsonArray();
        componentsList.add(component1);
        componentsList.add(component2);
        JsonObject components = new JsonObject();
        components.add("components", componentsList);
        components.add("paging", paging);

        ComponentProviderWrapper provider = new ComponentProviderWrapper();
        provider.setFakeComponents(components);

        Components result = provider.getComponents();
        assertEquals(4, result.getComponentsList().size());
    }

    /**
     * Test class in order to test the abstract provider class
     */
    class ComponentProviderWrapper extends AbstractComponentProvider {

        JsonObject components;

        public ComponentProviderWrapper() {
            super("server", "token", "project", "branch");
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeComponents(JsonObject pFake) {
            this.components = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getComponentsAsJsonObject(final int page)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.components;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public Components getComponents() throws BadSonarQubeRequestException, SonarQubeException {
            return getComponentsAbstract();
        }

    }

}
