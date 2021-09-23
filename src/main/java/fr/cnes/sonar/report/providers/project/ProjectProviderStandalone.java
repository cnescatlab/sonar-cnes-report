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

import com.google.gson.JsonObject;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.providers.language.LanguageProvider;

/**
 * Provides basic project's information in standalone mode
 */
public class ProjectProviderStandalone extends AbstractProjectProvider implements ProjectProvider {

    /**
     * Complete constructor.
     * @param pServer SonarQube server..
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     * @param pLanguageProvider The language provider.
     */
    public ProjectProviderStandalone(final String pServer, final String pToken, final String pProject,
            final String pBranch, final LanguageProvider pLanguageProvider) {
        super(pServer, pToken, pProject, pBranch, pLanguageProvider);
    }

    @Override
    public Project getProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException {
        return getProjectAbstract(projectKey, branch);
    }

    @Override
    public boolean hasProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException {
        return hasProjectAbstract(projectKey, branch);
    }

    @Override
    protected JsonObject getProjectAsJsonObject(final String projectKey, final String branch)
            throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        return request(String.format(getRequest(GET_PROJECT_REQUEST), getServer(), projectKey, branch));
    }
}
