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

package fr.cnes.sonar.report.providers.securityhotspots;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.SecurityHotspot;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.hotspots.SearchRequest;
import org.sonarqube.ws.client.hotspots.ShowRequest;
import org.sonarqube.ws.Hotspots.SearchWsResponse;
import org.sonarqube.ws.Hotspots.ShowWsResponse;
import org.sonarqube.ws.Rules.ShowResponse;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * Provides security hotspots items in plugin mode
 */
public class SecurityHotspotsProviderPlugin extends AbstractSecurityHotspotsProvider implements SecurityHotspotsProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public SecurityHotspotsProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public List<SecurityHotspot> getToReviewSecurityHotspots() throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatus(TO_REVIEW);
    }

    @Override
    public List<SecurityHotspot> getReviewedSecurityHotspots() throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatus(REVIEWED);
    }

    /**
     * Get security hotspots depending on their status
     * @param status The status of security hotspots
     * @return List containing all the security hotspots
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    private List<SecurityHotspot> getSecurityHotspotsByStatus(String status) throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatusAbstract(status);
    }

    @Override
    protected JsonObject getSecurityHotspotsAsJsonObject(final int page, final int maxPerPage, final String status) {
        // prepare the request to get all the security hotspots
        final String p = String.valueOf(page);
        final String ps = String.valueOf(maxPerPage);
        final SearchRequest searchRequest = new SearchRequest()
                                                .setBranch(getBranch())
                                                .setP(p)
                                                .setProjectKey(getProjectKey())
                                                .setPs(ps)
                                                .setStatus(status);
        // perform the request to the server
        final SearchWsResponse searchWsResponse = getWsClient().hotspots().search(searchRequest);
        // transform response to JsonObject
        return responseToJsonObject(searchWsResponse);
    }

    @Override
    protected JsonObject getSecurityHotspotAsJsonObject(final String securityHotspotKey) {
        final ShowRequest showHotspotRequest = new ShowRequest().setHotspot(securityHotspotKey);
        final ShowWsResponse showHotspotResponse = getWsClient().hotspots().show(showHotspotRequest);
        return responseToJsonObject(showHotspotResponse);
    }

    @Override
    protected JsonObject getRuleAsJsonObject(final String securityHotspotRule)
            throws BadSonarQubeRequestException, SonarQubeException {
        final org.sonarqube.ws.client.rules.ShowRequest showRuleRequest =
                new org.sonarqube.ws.client.rules.ShowRequest().setKey(securityHotspotRule);
        final ShowResponse showRuleResponse = getWsClient().rules().show(showRuleRequest);
        return responseToJsonObject(showRuleResponse);
    }
}