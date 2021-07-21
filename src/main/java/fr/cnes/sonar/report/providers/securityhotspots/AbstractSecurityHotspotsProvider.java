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
import fr.cnes.sonar.report.model.Comment;
import fr.cnes.sonar.report.model.SecurityHotspot;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.hotspots.SearchRequest;
import org.sonarqube.ws.client.hotspots.ShowRequest;
import org.sonarqube.ws.Hotspots.SearchWsResponse;
import org.sonarqube.ws.Hotspots.ShowWsResponse;
import org.sonarqube.ws.Rules.ShowResponse;

/**
 * Contains common code for security hotspots providers
 */
public abstract class AbstractSecurityHotspotsProvider extends AbstractDataProvider {

    /**
     * Name of the request for getting security hotspots
     */
    private static final String GET_SECURITY_HOTSPOTS_REQUEST = "GET_SECURITY_HOTSPOTS_REQUEST";
    /**
     * Name of the request for getting a specific security hotspot
     */
    private static final String GET_SECURITY_HOTSPOT_REQUEST = "GET_SECURITY_HOTSPOT_REQUEST";
    /**
     * Name of the request for getting a specific rule
     */
    private static final String GET_RULE_REQUEST = "GET_RULE_REQUEST";
    /**
     * Field to search in json to get security hotspots
     */
    private static final String HOTSPOTS = "hotspots";
    /**
     * Field to search in json to get the security hotspot's resolution
     */
    private static final String RESOLUTION = "resolution";
    /**
     * Field to search in json to get the security hotspot's rule
     */
    private static final String RULE = "rule";
    /**
     * Field to search in json to get the security hotspot's comments
     */
    private static final String COMMENTS = "comment";
    /**
     * Field to search in json to get the security hotspot's severity
     */
    private static final String SEVERITY = "severity";
    /**
     * Field to search in json to get the security hotspot's language
     */
    private static final String LANGUAGE = "langName";
    /**
     * Value of the status parameter to get security hotspots to review
     */
    protected static final String TO_REVIEW = "TO_REVIEW";
    /**
     * Value of the status parameter to get reviewed security hotspots
     */
    protected static final String REVIEWED = "REVIEWED";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractSecurityHotspotsProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractSecurityHotspotsProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Generic getter for security hotspots depending on their status
     * @param isCalledInStandalone True if the method is called in standalone mode
     * @param status The status of security hotspots
     * @return List containing all the security hotspots
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<SecurityHotspot> getSecurityHotspotsByStatusAbstract(final boolean isCalledInStandalone, final String status)
            throws BadSonarQubeRequestException, SonarQubeException {
        // results variable
        final List<SecurityHotspot> res = new ArrayList<>();
        // stop condition
        boolean goOn = true;
        // current page
        int page = 1;
        // get maximum number of results per page
        final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));

        // search all security hotspots of the project
        while(goOn) {
            JsonObject searchHotspotsResult;
            if (isCalledInStandalone) {
                // prepare the request to get all the security hotspots
                final String searchHotspotsRequest = String.format(getRequest(GET_SECURITY_HOTSPOTS_REQUEST),
                        getServer(), getBranch(), page, getProjectKey(), maxPerPage, status);
                // perform the request to the server
                searchHotspotsResult = request(searchHotspotsRequest);
            } else {
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
                searchHotspotsResult = responseToJsonObject(searchWsResponse);
            }
            // transform json to SecurityHotspot[]
            SecurityHotspot[] securityHotspotTemp = getGson().fromJson(searchHotspotsResult.get(HOTSPOTS),
                    SecurityHotspot[].class);
            // perform requests to get more information about each security hotspot
            for (SecurityHotspot securityHotspot : securityHotspotTemp) {
                JsonObject showHotspotsResult;
                if (isCalledInStandalone) {
                    final String showHotspotRequest = String.format(getRequest(GET_SECURITY_HOTSPOT_REQUEST),
                            getServer(), securityHotspot.getKey());
                    showHotspotsResult = request(showHotspotRequest);
                } else {
                    final ShowRequest showHotspotRequest = new ShowRequest().setHotspot(securityHotspot.getKey());
                    final ShowWsResponse showHotspotResponse = getWsClient().hotspots().show(showHotspotRequest);
                    showHotspotsResult = responseToJsonObject(showHotspotResponse);
                }
                JsonObject rule = showHotspotsResult.get(RULE).getAsJsonObject();
                String key = rule.get(KEY).getAsString();
                Comment[] comments = getGson().fromJson(showHotspotsResult.get(COMMENTS), Comment[].class);
                securityHotspot.setRule(key);
                securityHotspot.setComments(comments);
                if(status.equals(REVIEWED)) {
                    String resolution = showHotspotsResult.get(RESOLUTION).getAsString();
                    securityHotspot.setResolution(resolution);
                }
                JsonObject showRuleResult;
                if (isCalledInStandalone) {
                    final String showRuleRequest = String.format(getRequest(GET_RULE_REQUEST), getServer(),
                            securityHotspot.getRule());
                    showRuleResult = request(showRuleRequest);
                } else {
                    final org.sonarqube.ws.client.rules.ShowRequest showRuleRequest =
                        new org.sonarqube.ws.client.rules.ShowRequest().setKey(securityHotspot.getRule());
                    final ShowResponse showRuleResponse = getWsClient().rules().show(showRuleRequest);
                    showRuleResult = responseToJsonObject(showRuleResponse);
                }
                JsonObject ruleContent = showRuleResult.get(RULE).getAsJsonObject();
                String severity = ruleContent.get(SEVERITY).getAsString();
                String language = ruleContent.get(LANGUAGE).getAsString();
                securityHotspot.setSeverity(severity);
                securityHotspot.setLanguage(language);
            }
            // add security hotspots to the final result
            res.addAll(Arrays.asList(securityHotspotTemp));
            // get total number of items
            JsonObject paging = searchHotspotsResult.get(PAGING).getAsJsonObject();
            int number = paging.get(TOTAL).getAsInt();
            // update stop condition and increment current page
            goOn = page*maxPerPage < number;
            page++;
        }

        // return the security hotspots
        return res;
    }
}