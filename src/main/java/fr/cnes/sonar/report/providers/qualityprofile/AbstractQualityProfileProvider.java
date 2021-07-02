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

package fr.cnes.sonar.report.providers.qualityprofile;

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for quality profile providers
 */
public abstract class AbstractQualityProfileProvider extends AbstractDataProvider {

    /**
     * Field to search in json to get results' values
     */
    protected static final String RESULTS = "results";
    /**
     * Field to search in json to get profiles
     */
    protected static final String PROFILES = "profiles";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractQualityProfileProvider(final String pServer, final String pToken, final String pProject) {
        super(pServer, pToken, pProject);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractQualityProfileProvider(final WsClient wsClient, final String project) {
        super(wsClient, project);
    }
}