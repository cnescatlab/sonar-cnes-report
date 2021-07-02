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

package fr.cnes.sonar.report.providers.component;

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for component providers
 */
public abstract class AbstractComponentProvider extends AbstractDataProvider {
    
    /**
     * Field to search in json to get components
     */
    protected static final String COMPONENTS = "components";
    /**
     * Property to get the list of components to exclude
     */
    protected static final String EXCLUDED_COMPONENTS = "components.excluded";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractComponentProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractComponentProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }
}