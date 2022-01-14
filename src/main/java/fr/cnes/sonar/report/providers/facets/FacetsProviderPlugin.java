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

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facets;
import fr.cnes.sonar.report.model.TimeFacets;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.issues.SearchRequest;
import org.sonarqube.ws.client.measures.SearchHistoryRequest;
import org.sonarqube.ws.Issues.SearchWsResponse;
import org.sonarqube.ws.Measures.SearchHistoryResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * Provides issue items in plugin mode
 */
public class FacetsProviderPlugin extends AbstractFacetsProvider implements FacetsProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public FacetsProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public Facets getFacets() throws BadSonarQubeRequestException, SonarQubeException {
        return getFacetsAbstract();
    }

    @Override
    public TimeFacets getTimeFacets() throws BadSonarQubeRequestException, SonarQubeException {
        return getTimeFacetsAbstract();
    }

    @Override
    protected JsonObject getFacetsAsJsonObject(final int page) {
        // prepare the request
        final List<String> projects = new ArrayList<>(Arrays.asList(getProjectKey()));
        final List<String> facets = new ArrayList<>(Arrays.asList(getMetrics(PROJECT_FACETS).split(",")));
        final String maxPerPage = String.valueOf(FACETS_MAX_PER_PAGE);
        final String pageIndex = String.valueOf(page);
        final SearchRequest searchRequest = new SearchRequest()
                                                .setProjects(projects)
                                                .setResolved(CONFIRMED)
                                                .setFacets(facets)
                                                .setPs(maxPerPage)
                                                .setP(pageIndex)
                                                .setBranch(getBranch());
        // perform the request to the server
        final SearchWsResponse searchWsResponse = getWsClient().issues().search(searchRequest);
        // transform response to JsonObject
        return responseToJsonObject(searchWsResponse);
    }

    @Override
    protected JsonObject getTimeFacetsAsJsonObject(int page, int pMaxPerPage) {
        final List<String> metricKeys = new ArrayList<>(Arrays.asList(getMetrics(CHARTS_METRICS)));

        // prepare the request
        final String maxPerPage = String.valueOf(pMaxPerPage);
        final String pageIndex = String.valueOf(page);
        final SearchHistoryRequest searchHistoryRequest = new SearchHistoryRequest()
                                                                .setComponent(getProjectKey())
                                                                .setMetrics(metricKeys)
                                                                .setPs(maxPerPage)
                                                                .setP(pageIndex)
                                                                .setBranch(getBranch());
        // perform the request to the server
        final SearchHistoryResponse searchHistoryResponse = getWsClient().measures().searchHistory(searchHistoryRequest);
        // transform response to JsonObject
        return responseToJsonObject(searchHistoryResponse);
    }
}
