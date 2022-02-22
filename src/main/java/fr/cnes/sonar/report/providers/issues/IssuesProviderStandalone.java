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

package fr.cnes.sonar.report.providers.issues;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Issue;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Provides issue items in standalone mode
 */
public class IssuesProviderStandalone extends AbstractIssuesProvider implements IssuesProvider {

    /**
     * Name of the request for getting issues
     */
    private static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";

    /**
     * Complete constructor.
     * 
     * @param pServer  SonarQube server.
     * @param pToken   String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch  The branch of the project to report.
     */
    public IssuesProviderStandalone(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    @Override
    public List<Issue> getIssues()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(CONFIRMED);
    }

    @Override
    public List<Issue> getUnconfirmedIssues()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(UNCONFIRMED);
    }

    /**
     * Get issues depending on their resolved status
     * 
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    private List<Issue> getIssuesByStatus(String confirmed)
            throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatusAbstract(confirmed);
    }

    @Override
    public List<Map<String, String>> getRawIssues() throws BadSonarQubeRequestException, SonarQubeException {
        return getRawIssuesAbstract();
    }

    @Override
    protected JsonObject getIssuesAsJsonObject(final int page, final int maxPerPage, final String confirmed)
            throws BadSonarQubeRequestException, SonarQubeException {
        // prepare the server to get all the issues
        final String request = String.format(getRequest(GET_ISSUES_REQUEST), getServer(), getProjectKey(),
                getMetrics(ISSUES_FACETS), maxPerPage, page, getMetrics(ISSUES_ADDITIONAL_FIELDS), confirmed,
                getBranch());
        // perform the request to the server
        return request(request);
    }
}
