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

package fr.cnes.sonar.report.providers.facets;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facets;
import fr.cnes.sonar.report.model.TimeFacets;
import fr.cnes.sonar.report.model.TimeValue;

public class AbstractFacetsProviderTest {

    @Test
    public void extractTimeValuesFromJsonObjectEmptyTest() {
        FacetsProviderWrapper fakeProvider = new FacetsProviderWrapper();

        JsonObject jo = new JsonObject();
        jo.add("history", new JsonArray());               
        assertEquals(0, fakeProvider.fakeExtractTimeValuesFromJsonObject(jo).size());
    }

    @Test
    public void extractTimeValuesFromJsonObjectNormalTest() {
        FacetsProviderWrapper fakeProvider = new FacetsProviderWrapper();
        
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

    @Test
    public void getEmptyFacets() throws BadSonarQubeRequestException, SonarQubeException {
        // create fake facets object
        JsonObject facets = new JsonObject();
        facets.add("facets", new JsonArray());
        FacetsProviderWrapper provider = new FacetsProviderWrapper();
        provider.setFakeFacets(facets);
        Facets result = provider.getFacets();
        assertEquals(0, result.getFacetValues("test").size());
    }

    @Test
    public void getFacets() throws BadSonarQubeRequestException, SonarQubeException {
        // create fake facets object
        JsonObject value = new JsonObject();
        value.addProperty("count", 25);
        value.addProperty("val", "MAJOR");
        JsonArray values = new JsonArray();
        values.add(value);
        JsonObject facet = new JsonObject();
        facet.addProperty("property", "severities");
        facet.add("values", values);
        JsonArray facetsList = new JsonArray();
        facetsList.add(facet);
        JsonObject facets = new JsonObject();
        facets.add("facets", facetsList);

        FacetsProviderWrapper provider = new FacetsProviderWrapper();
        provider.setFakeFacets(facets);
        Facets result = provider.getFacets();
        assertEquals(0, result.getFacetValues("test").size());
        assertEquals(1, result.getFacetValues("severities").size());
        assertEquals("MAJOR", result.getFacetValues("severities").get(0).getVal());
        assertEquals(25, result.getFacetValues("severities").get(0).getCount());
    }

    @Test
    public void getEmptyTimeFacets() throws BadSonarQubeRequestException, SonarQubeException {
        // create fake facets object
        JsonObject paging = new JsonObject();
        paging.addProperty("total", 0);
        JsonObject facets = new JsonObject();
        facets.add("measures", new JsonArray());
        facets.add("paging", paging);
        
        FacetsProviderWrapper provider = new FacetsProviderWrapper();
        provider.setFakeTimeFacets(facets);
        TimeFacets result = provider.getTimeFacets();
        assertEquals(0, result.getFacetValues("test").size());
    }

    @Test
    public void getTimeFacets() throws BadSonarQubeRequestException, SonarQubeException {
        JsonObject value1 = new JsonObject();
        value1.addProperty("date", "2021-08-11T12:19:49+0000");
        value1.addProperty("value", "5");
        JsonObject value2 = new JsonObject();
        value2.addProperty("date", "2021-08-11T12:19:49+0000");
        value2.addProperty("value", "300.8");
        JsonArray values = new JsonArray();
        values.add(value1);
        values.add(value2);
        JsonObject facet = new JsonObject();
        facet.addProperty("metric", "nb_issues");
        facet.add("history", values);
        JsonArray facetsList = new JsonArray();
        facetsList.add(facet);
        JsonObject paging = new JsonObject();
        paging.addProperty("total", 501);
        JsonObject facets = new JsonObject();
        facets.add("measures", facetsList);
        facets.add("paging", paging);

        FacetsProviderWrapper provider = new FacetsProviderWrapper();
        provider.setFakeTimeFacets(facets);
        TimeFacets result = provider.getTimeFacets();
        assertEquals(0, result.getFacetValues("test").size());
        assertEquals(2, result.getFacetValues("nb_issues").size());
        assertEquals(44419.59709490741, result.getFacetValues("nb_issues").get(0).getDate(), 0.1);
        assertEquals("5", result.getFacetValues("nb_issues").get(0).getValue());
        assertEquals(44419.59709490741, result.getFacetValues("nb_issues").get(1).getDate(), 0.1);
        assertEquals("300.8", result.getFacetValues("nb_issues").get(1).getValue());
    }

    /**
     * Fake class to test inherited protected method
     */
    private class FacetsProviderWrapper extends AbstractFacetsProvider {

        JsonObject facets;
        JsonObject timeFacets;

        public FacetsProviderWrapper() {
            super("server", "token", "project", "branch");
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeFacets(JsonObject pFake) {
            this.facets = pFake;
        }

        public void setFakeTimeFacets(JsonObject pFake) {
            this.timeFacets = pFake;
        }

        // Implement necessary abstract method
        protected JsonObject getFacetsAsJsonObject(final int page) throws BadSonarQubeRequestException, SonarQubeException {
            return this.facets;
        }
        protected JsonObject getTimeFacetsAsJsonObject(final int page, final int maxPerPage) throws BadSonarQubeRequestException, SonarQubeException {
            return this.timeFacets;
        }
    
        // Wrapper for protected method to test
        public List<TimeValue> fakeExtractTimeValuesFromJsonObject(JsonObject jo) {
            return extractTimeValuesFromJsonObject(jo);
        }

        public Facets getFacets() throws BadSonarQubeRequestException, SonarQubeException {
            return getFacetsAbstract();
        }

        public TimeFacets getTimeFacets() throws BadSonarQubeRequestException, SonarQubeException {
            return getTimeFacetsAbstract();
        }
    }
    
}
