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

import fr.cnes.sonar.report.model.SecurityHotspot;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * Provides security hotspots items in standalone mode
 */
public class SecurityHotspotsProviderStandalone extends AbstractSecurityHotspotsProvider implements SecurityHotspotsProvider {

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
     * Complete constructor.
     * @param server SonarQube server.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public SecurityHotspotsProviderStandalone(final String server, final String token, final String project,
            final String branch) {
        super(server, token, project, branch);
    }

    @Override
    public List<SecurityHotspot> getToReviewSecurityHotspots()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatus(TO_REVIEW);
    }

    @Override
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
    private List<SecurityHotspot> getSecurityHotspotsByStatus(String status)
            throws BadSonarQubeRequestException, SonarQubeException {
        return getSecurityHotspotsByStatusAbstract(status);
    }

    @Override
    protected JsonObject getSecurityHotspotsAsJsonObject(final int page, final int maxPerPage, final String status)
            throws BadSonarQubeRequestException, SonarQubeException {
        return request(String.format(getRequest(GET_SECURITY_HOTSPOTS_REQUEST), getServer(), getBranch(), page,
                getProjectKey(), maxPerPage, status));
    }

    @Override
    protected JsonObject getSecurityHotspotAsJsonObject(final String securityHotspotKey)
            throws BadSonarQubeRequestException, SonarQubeException {
        return request(String.format(getRequest(GET_SECURITY_HOTSPOT_REQUEST), getServer(), securityHotspotKey));
    }

    @Override
    protected JsonObject getRuleAsJsonObject(final String securityHotspotRule)
            throws BadSonarQubeRequestException, SonarQubeException {
        return request(String.format(getRequest(GET_RULE_REQUEST), getServer(), securityHotspotRule));
    }
}