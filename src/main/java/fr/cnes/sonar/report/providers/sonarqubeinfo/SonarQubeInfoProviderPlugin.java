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

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import org.sonarqube.ws.client.WsClient;

import java.util.logging.Level;

/**
 * Provides info about SonarQube system in plugin mode.
 */
public class SonarQubeInfoProviderPlugin extends AbstractDataProvider implements SonarQubeInfoProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     */
    public SonarQubeInfoProviderPlugin(final WsClient wsClient) {
        super(wsClient);
    }

    @Override
    public String getSonarQubeVersion() {
        return getWsClient().system().status().getVersion();
    }

    @Override
    public String getSonarQubeStatus() {
        String status;
        try {
            status = getWsClient().system().status().getStatus().toString();
        } catch (Exception e) {
            status = "DOWN";
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return status;
    }
}