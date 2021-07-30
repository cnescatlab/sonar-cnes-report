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
/**
 * Contains common code for security hotspots providers
 */
public abstract class AbstractSecurityHotspotsProvider extends AbstractDataProvider {

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
     * @param status The status of security hotspots
     * @return List containing all the security hotspots
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<SecurityHotspot> getSecurityHotspotsByStatusAbstract(final String status)
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
            final JsonObject searchHotspotsResult = getSecurityHotspotsAsJsonObject(page, maxPerPage, status);
            // transform json to SecurityHotspot[]
            SecurityHotspot[] securityHotspotTemp = getGson().fromJson(searchHotspotsResult.get(HOTSPOTS),
                    SecurityHotspot[].class);
            // perform requests to get more information about each security hotspot
            for (SecurityHotspot securityHotspot : securityHotspotTemp) {
                final String securityHotspotKey = securityHotspot.getKey();
                final JsonObject showHotspotsResult = getSecurityHotspotAsJsonObject(securityHotspotKey);
                JsonObject rule = showHotspotsResult.get(RULE).getAsJsonObject();
                String key = rule.get(KEY).getAsString();
                Comment[] comments = getGson().fromJson(showHotspotsResult.get(COMMENTS), Comment[].class);
                securityHotspot.setRule(key);
                securityHotspot.setComments(comments);
                if(status.equals(REVIEWED)) {
                    String resolution = showHotspotsResult.get(RESOLUTION).getAsString();
                    securityHotspot.setResolution(resolution);
                }
                final String securityHotspotRule = securityHotspot.getRule();
                final JsonObject showRuleResult = getRuleAsJsonObject(securityHotspotRule);
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

    /**
     * Get a JsonObject from the response of a search hotspots request.
     * @param page The current page.
     * @param maxPerPage The maximum page size.
     * @param status The status of security hotspots.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getSecurityHotspotsAsJsonObject(final int page, final int maxPerPage, final String status)
            throws BadSonarQubeRequestException, SonarQubeException;

    /**
     * Get a JsonObject from the response of a search hotspots request.
     * @param securityHotspotKey The key of the security hotspot.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getSecurityHotspotAsJsonObject(final String securityHotspotKey)
            throws BadSonarQubeRequestException, SonarQubeException;
    
    /**
     * Get a JsonObject from the response of a show rule request.
     * @param securityHotspotRule The key of the rule.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getRuleAsJsonObject(final String securityHotspotRule)
            throws BadSonarQubeRequestException, SonarQubeException;
}