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

package fr.cnes.sonar.report.providers.sonarqubeinfo;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.util.logging.Level;

/**
 * Provides info about SonarQube system in standalone mode.
 */
public class SonarQubeInfoProviderStandalone extends AbstractDataProvider implements SonarQubeInfoProvider {

    /**
     *  Name of the request for getting SonarQube server information
     */
    private static final String GET_SONARQUBE_INFO_REQUEST =
            "GET_SONARQUBE_INFO_REQUEST";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     */
    public SonarQubeInfoProviderStandalone(final String pServer, final String pToken) {
        super(pServer, pToken, "");
    }

    /**
     * Get the SonarQube version.
     * @return String containing the SonarQube version.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    @Override
    public String getSonarQubeVersion() throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        final JsonObject jsonObject = request(String.format(getRequest(GET_SONARQUBE_INFO_REQUEST), getServer()));

        // extract data from json object and return it
        return jsonObject.get("version").getAsString();
    }

    /**
     * Get the SonarQube server status.
     * @return String containing the SonarQube status.
     */
    @Override
    public String getSonarQubeStatus() {
        // Represents status of SonarQube server to return.
        String status;
        try {
            // send a request to SonarQube server and return th response as a json object
            // if there is an error on server side this method throws an exception
            final String request = String.format(getRequest(GET_SONARQUBE_INFO_REQUEST), getServer());
            final JsonObject jsonObject = request(request);
            // extract data from json object and return it
            status = jsonObject.get("status").getAsString();
        } catch (final Exception e) {
            status = "DOWN";
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return status;
    }
}
