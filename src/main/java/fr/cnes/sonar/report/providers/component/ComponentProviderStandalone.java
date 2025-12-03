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

import com.google.gson.JsonObject;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Components;

/**
 * Provides component items in standalone mode
 */
public class ComponentProviderStandalone extends AbstractComponentProvider implements ComponentProvider {

    /**
     * Name of the request for getting componentsList
     */
    private static final String GET_COMPONENTS_REQUEST = "GET_COMPONENTS_REQUEST";

    /**
     * Constructor.
     *
     * @param pServer  SonarQube server.
     * @param pToken   String representing the user token.
     * @param pProject The id of the component to report.
     * @param pProject The branch of the component to report.
     * @param pEnableIssuesMultiRequests Workaround SonarQube 10'000 issues limitation, by multiple requests.
     * @param pMaxUrlSize                SonarQube WebAPI max URL text-size.
     */
    public ComponentProviderStandalone(final String pServer, final String pToken, 
    		final String pProject, final String pBranch, 
            final boolean pEnableIssuesMultiRequests, final int pMaxUrlSize) {
    	super(pServer, pToken, pProject, pBranch, pEnableIssuesMultiRequests, pMaxUrlSize);
    }

    @Override
    public Components getComponents() throws BadSonarQubeRequestException, SonarQubeException {
        return getComponentsAbstract();
    }

    @Override
    protected JsonObject getComponentsAsJsonObject(final int page)
            throws BadSonarQubeRequestException, SonarQubeException {
        return request(String.format(getRequest(GET_COMPONENTS_REQUEST), getServer(), getProjectKey(),
                getMetrics(SHEETS_METRICS), page,
                getRequest(MAX_PER_PAGE_SONARQUBE), getBranch()));
    }
}
