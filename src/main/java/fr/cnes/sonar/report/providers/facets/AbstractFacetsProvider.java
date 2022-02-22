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

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import fr.cnes.sonar.report.utils.DateConverter;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Facets;
import fr.cnes.sonar.report.model.TimeFacet;
import fr.cnes.sonar.report.model.TimeFacets;
import fr.cnes.sonar.report.model.TimeValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for issues providers
 */
public abstract class AbstractFacetsProvider extends AbstractDataProvider {

    /**
     * Value of the field to get confirmed issues
     */
    protected static final String CONFIRMED = "false";
    /**
     * Parameter "facets" of the JSON response
     */
    private static final String FACETS = "facets";
    /**
     * Parameter "measures" of the JSON response
     */
    private static final String MEASURES = "measures";
    /**
     * Parameter "history" of the JSON response
     */
    private static final String HISTORY = "history";
    /**
     * Parameter "metric" of the JSON response
     */
    private static final String METRIC = "metric";
    /**
     * Parameter "page" of the facets request (only first page is needed)
     */
    private static final int FACETS_PAGE = 1;
    /**
     * Parameter "maxPerPage" of the facets request (only need first element)
     */
    protected static final int FACETS_MAX_PER_PAGE = 1;
    /**
     * Parameter "resolved" of the facets request (only unresolved elements needed)
     */
    protected static final String FACETS_STATUS = "false";
    /**
     * Name of the SonarQube metrics to retrieve 
     */
    protected static final String CHARTS_METRICS = "CHARTS_METRICS";
    /**
     * Name of the SonarQube project facets to retrieve 
     */
    protected static final String PROJECT_FACETS = "PROJECT_FACETS";
    

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractFacetsProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractFacetsProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Generic getter for all the stats on a project
     * @return A list of facets
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected Facets getFacetsAbstract() throws BadSonarQubeRequestException, SonarQubeException {
        JsonObject jo = getFacetsAsJsonObject(FACETS_PAGE);
        // put wanted resources in facets array
        Facet[] tmp = (getGson().fromJson(jo.get(FACETS), Facet[].class));        

        Facets facets = new Facets();
        facets.setFacets(Arrays.asList(tmp));

        return facets;
    }

    protected TimeFacets getTimeFacetsAbstract() throws BadSonarQubeRequestException, SonarQubeException {

        boolean goOn = true;
        int page = 1;
        final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));

        List<TimeFacet> result = new ArrayList<>();

        while(goOn) {
            JsonObject jo = getTimeFacetsAsJsonObject(page, maxPerPage);

            // Get the list of measures from the JSON Object
            JsonArray measures = jo.get(MEASURES).getAsJsonArray();

            // Extract TimeFacets from the JsonArray
            for (JsonElement metric : measures) {
                String facetName = metric.getAsJsonObject().get(METRIC).getAsString();
                List<TimeValue> values = extractTimeValuesFromJsonObject(metric.getAsJsonObject());
                TimeFacet timeFacet = new TimeFacet(facetName, values);
                result.add(timeFacet);
            }

            JsonObject paging = jo.get(PAGING).getAsJsonObject();
            int number = paging.get(TOTAL).getAsInt();
            goOn = page*maxPerPage < number;
            page++;
        }

        TimeFacets timeFacets = new TimeFacets();
        timeFacets.setTimeFacets(result);
       
        // return list of time facets
        return timeFacets;
    }
    
    /**
     * Extract a list of TimeValue from a history SonarQube JsonObject
     * @param metric the metric to extract data from
     * @return the list of TimeValue contained inside the JsonObject
     */
    protected List<TimeValue> extractTimeValuesFromJsonObject(JsonObject metric) {
        List<TimeValue> values = new ArrayList<>();

        // For each measure (date/value) inside the "history" attribute of the measure
        for(JsonElement history : metric.get(HISTORY).getAsJsonArray()) {
            JsonObject measure = history.getAsJsonObject();
            // Convert Date from SonarQube format to Excel
            double date = DateConverter.sonarQubeDateToExcelDate(
                measure.get("date").getAsString());
            // Get data as String because Word charts take Strings as input
            String value = measure.get("value").getAsString();
            TimeValue v = new TimeValue(date, value);
            values.add(v);
        }

        return values;
    }
    
   /**
     * Get a JsonObject from the response of a facets request.
     * @param page The current page.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getFacetsAsJsonObject(final int page) throws BadSonarQubeRequestException, SonarQubeException;

    /**
     * Get a JsonObject from the response of a time facets request.
     * @param page The current page.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getTimeFacetsAsJsonObject(final int page, final int maxPerPage)
            throws BadSonarQubeRequestException, SonarQubeException;
}