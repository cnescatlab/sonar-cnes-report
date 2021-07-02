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

package fr.cnes.sonar.report.providers.project;

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import fr.cnes.sonar.report.providers.language.LanguageProvider;
import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for project providers
 */
public abstract class AbstractProjectProvider extends AbstractDataProvider {

    /**
     * The language provider
     */
    protected LanguageProvider languageProvider;

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     * @param pLanguageProvider The language provider.
     */
    protected AbstractProjectProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch, final LanguageProvider pLanguageProvider) {
        super(pServer, pToken, pProject, pBranch);
        this.languageProvider = pLanguageProvider;
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     * @param languageProvider The language provider.
     */
    protected AbstractProjectProvider(final WsClient wsClient, final String project, final String branch, final LanguageProvider languageProvider) {
        super(wsClient, project, branch);
        this.languageProvider = languageProvider;
    }
}