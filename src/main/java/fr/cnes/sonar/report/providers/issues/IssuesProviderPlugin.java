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
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import org.sonarqube.ws.client.WsClient;

import java.util.List;
import java.util.Map;

/**
 * Provides issue items in plugin mode
 */
public class IssuesProviderPlugin extends AbstractIssuesProvider implements IssuesProvider {

    

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public IssuesProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public List<Issue> getIssues() throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(CONFIRMED);
    }

    @Override
    public List<Issue> getUnconfirmedIssues() throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(UNCONFIRMED);
    }

    /**
     * Get issues depending on their resolved status
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    private List<Issue> getIssuesByStatus(String confirmed) throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatusAbstract(false, confirmed);
    }

    @Override
    public List<Map<String,String>> getRawIssues() throws BadSonarQubeRequestException, SonarQubeException {
        return getRawIssuesAbstract(false);
    }

    @Override
    public List<Facet> getFacets() throws BadSonarQubeRequestException, SonarQubeException {
        return getFacetsAbstract(false);
    }
}