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

package fr.cnes.sonar.report.providers.language;

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for language providers
 */
public abstract class AbstractLanguageProvider extends AbstractDataProvider {

    /**
     * Json's field containing the language's array
     */
    protected static final String LANGUAGES_FIELD = "languages";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     */
    protected AbstractLanguageProvider(final String pServer, final String pToken, final String pProject) {
        super(pServer, pToken, pProject);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     */
    protected AbstractLanguageProvider(final WsClient wsClient, final String project) {
        super(wsClient, project);
    }
}