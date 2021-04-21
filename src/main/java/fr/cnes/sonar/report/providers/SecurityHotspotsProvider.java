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

package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;

import fr.cnes.sonar.report.model.Comment;
import fr.cnes.sonar.report.model.SecurityHotspot;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides security hotspots items
 */
public class SecurityHotspotsProvider extends AbstractDataProvider {

    /**
     * Field to search in json to get security hotspots
     */
    private static final String HOTSPOTS = "hotspots";
    /**
     * Field to search in json to get the security hotspot's resolution
     */
    protected static final String RESOLUTION = "resolution";
    /**
     * Field to search in json to get the security hotspot's rule
     */
    protected static final String RULE = "rule";
    /**
     * Field to search in json to get the security hotspot's rule key
     */
    protected static final String KEY = "key";
    /**
     * Field to search in json to get the security hotspot's comments
     */
    protected static final String COMMENTS = "comment";
    /**
     * Field to search in json to get the security hotspot's severity
     */
    protected static final String SEVERITY = "severity";
    /**
     * Field to search in json to get the security hotspot's language
     */
    protected static final String LANGUAGE = "langName";
    /**
     * Value of the status parameter to get security hotspots to review
     */
    private static final String TO_REVIEW = "TO_REVIEW";
    /**
     * Value of the status parameter to get reviewed security hotspots
     */
    private static final String REVIEWED = "REVIEWED";

    /**
     * Complete constructor.
     * @param server SonarQube server.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public SecurityHotspotsProvider(final SonarQubeServer server, final String token, final String project,
            final String branch) {
        super(server, token, project, branch);
    }

    /**
     * Get security hotspots of a project with TO_REVIEW status
     * @return List containing all the security hotspots
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<SecurityHotspot> getToReviewSecurityHotspots()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatus(TO_REVIEW);
    }

    /**
     * Get security hotspots of a project with REVIEWED status
     * @return List containing all the security hotspots
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<SecurityHotspot> getReviewedSecurityHotspots()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatus(REVIEWED);
    }

    /**
     * Get security hotspots depending on their status
     * @param status The status of security hotspots
     * @return List containing all the security hotspots
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<SecurityHotspot> getSecurityHotspotsByStatus(String status)
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
            // prepare the request to get all the security hotspots
            final String searchHotspotsRequest = String.format(getRequest(GET_SECURITY_HOTSPOTS_REQUEST),
                    getServer().getUrl(), getBranch(), page, getProjectKey(), maxPerPage, status);
            // perform the request to the server
            final JsonObject searchHotspotsResult = request(searchHotspotsRequest);
            // transform json to SecurityHotspot[]
            SecurityHotspot[] securityHotspotTemp = getGson().fromJson(searchHotspotsResult.get(HOTSPOTS),
                    SecurityHotspot[].class);
            // perform requests to get more information about each security hotspot
            for (SecurityHotspot securityHotspot : securityHotspotTemp) {
                final String showHotspotRequest = String.format(getRequest(GET_SECURITY_HOTSPOTS_REQUEST),
                        getServer().getUrl(), securityHotspot.getKey());
                final JsonObject showHotspotsResult = request(showHotspotRequest);
                JsonObject rule = showHotspotsResult.get(RULE).getAsJsonObject();
                String key = rule.get(KEY).getAsString();
                Comment[] comments = getGson().fromJson(showHotspotsResult.get(COMMENTS), Comment[].class);
                securityHotspot.setRule(key);
                securityHotspot.setComments(comments);
                if(status.equals(REVIEWED)) {
                    String resolution = showHotspotsResult.get(RESOLUTION).getAsString();
                    securityHotspot.setResolution(resolution);
                }

                final String showRuleRequest = String.format(getRequest(GET_RULE_REQUEST), getServer().getUrl(),
                        securityHotspot.getRule());
                final JsonObject showRuleResult = request(showRuleRequest);
                String severity = showRuleResult.get(SEVERITY).getAsString();
                String language = showRuleResult.get(LANGUAGE).getAsString();
                securityHotspot.setSeverity(severity);
                securityHotspot.setLanguage(language);
            }
            // add security hotspots to the final result
            res.addAll(Arrays.asList(securityHotspotTemp));
            // get total number of items
            int number = searchHotspotsResult.get(TOTAL).getAsInt();
            // update stop condition and increment current page
            goOn = page*maxPerPage < number;
            page++;
        }

        // return the security hotspots
        return res;
    }
}