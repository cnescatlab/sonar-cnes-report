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

import com.google.gson.JsonObject;

/**
 * Provides issue items in standalone mode
 */
public class FacetsProviderStandalone extends AbstractFacetsProvider implements FacetsProvider {

    /**
     * Name of the request for getting facets
     */
    private static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";
    /**
     * Name of the request for getting the measures history
     */
    private static final String GET_MEASURES_HISTORY_REQUEST = "GET_MEASURES_HISTORY_REQUEST";
    /**
     * Name of the SonarQube "additionalFields" of the project facets request (no additional fields needed)
     */
    private static final String PROJECT_ADDITIONAL_FIELDS = "PROJECT_ADDITIONAL_FIELDS";

    /**
     * Complete constructor.
     * 
     * @param pServer  SonarQube server.
     * @param pToken   String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch  The branch of the project to report.
     */
    public FacetsProviderStandalone(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
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
    protected JsonObject getFacetsAsJsonObject(final int page) throws BadSonarQubeRequestException, SonarQubeException {
        // prepare the request
        final String request = String.format(getRequest(GET_ISSUES_REQUEST), getServer(), getProjectKey(),
                getMetrics(PROJECT_FACETS), FACETS_MAX_PER_PAGE, page, getMetrics(PROJECT_ADDITIONAL_FIELDS),
                FACETS_STATUS, getBranch());
        // contact the server to request the resources as json
        return request(request);
    }

    @Override
    protected JsonObject getTimeFacetsAsJsonObject(int page, int maxPerPage)
            throws BadSonarQubeRequestException, SonarQubeException {
        // prepare the request
        final String request = String.format(getRequest(GET_MEASURES_HISTORY_REQUEST), getServer(), getProjectKey(),
                getMetrics(CHARTS_METRICS), maxPerPage, page, getBranch());
        // perform the request to the server
        return request(request);
    }
}
