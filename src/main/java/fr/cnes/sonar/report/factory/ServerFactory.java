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

package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.providers.SonarQubeInfoProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Construct the server instance information
 */
public class ServerFactory {

    /** List of SonarQube versions which are supported by cnesreport. */
    private static final List<String> SUPPORTED_VERSIONS = Arrays.asList(
            "8.9", "8.9.*");

    /** Url of the SonarQube server. */
    private String url;

    /** Token of the SonarQube user. */
    private String token;

    /**
     * Complete constructor.
     * @param pUrl Value for server.
     * @param pToken Value for token.
     */
    public ServerFactory(final String pUrl, final String pToken) {
        this.url = pUrl;
        this.token = pToken;
    }

    /**
     * Create a server instance from SonarQube API.
     * @return A complete report resources model.
     * @throws BadSonarQubeRequestException When a request to the server is not well-formed.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public SonarQubeServer create() throws BadSonarQubeRequestException, SonarQubeException {
        // the new report to return
        final SonarQubeServer server = new SonarQubeServer();

        // Set URL.
        server.setUrl(this.url);

        // instantiation of providers
        final SonarQubeInfoProvider infoProvider = new SonarQubeInfoProvider(server, this.token);

        // Set if the server is up or not.
        server.setStatus(infoProvider.getSonarQubeStatus());

        // If up, set server version.
        if(server.isUp()) {
            // Get SonarQube version.
            final String version = infoProvider.getSonarQubeVersion();
            // Check if version is supported.
            final boolean isSupported = SUPPORTED_VERSIONS.stream().parallel().anyMatch(version::matches);
            // Set version.
            server.setVersion(version, isSupported);
        }

        return server;
    }

}
