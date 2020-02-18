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
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.StringManager;

/**
 * Provides basic project's information
 */
public class ProjectProvider extends AbstractDataProvider {

	/**
	 * Used to get language data for the projects
	 */
    private LanguageProvider languageProvider;

    /**
     * Complete constructor.
     * @param pServer SonarQube server..
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    public ProjectProvider(final SonarQubeServer pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
        languageProvider = new LanguageProvider(pServer, pToken, pProject);
    }

    /**
     * Get the project corresponding to the given key.
     * @param projectKey the key of the project.
     * @param branch the branch of the project.
     * @return A simple project.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public Project getProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        JsonObject jo = request(String.format(getRequest(GET_PROJECT_REQUEST),
                getServer().getUrl(), projectKey, branch));

        // put json in a Project class
        final Project project = (getGson().fromJson(jo, Project.class));
        ProfileMetaData[] metaData;

        if(server.getNormalizedVersion().matches("5.*|6.[012].*")) {
            // retrieve quality profiles for SQ 5.X versions
            jo = request(String.format(getRequest(GET_PROJECT_QUALITY_PROFILES_REQUEST),
                    getServer().getUrl(), projectKey));
            // set language's name for profiles
            metaData = (getGson().fromJson(jo.getAsJsonArray(PROFILES), ProfileMetaData[].class));
            project.setQualityProfiles(metaData);
        } else {
            // set language's name for profiles
            metaData = project.getQualityProfiles();
        }

        String languageName;
        for(ProfileMetaData it : metaData){
            languageName = languageProvider.getLanguage(it.getLanguage());
            it.setLanguageName(languageName);
        }
        project.setQualityProfiles(metaData);

        // check description nullity
        if(null==project.getDescription()) {
            project.setDescription(StringManager.EMPTY);
        }
        // check version nullity
        if(null==project.getVersion()) {
            project.setVersion(StringManager.EMPTY);
        }

        return project;
    }

    /**
     * Check if a project exists on a SonarQube instance.
     * @param projectKey the key of the project.
     * @param branch the branch of the project.
     * @return True if the project exists.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public boolean hasProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        final JsonObject jsonObject = request(String.format(getRequest(GET_PROJECT_REQUEST),
                getServer().getUrl(), projectKey, branch));

        // Retrieve project key if the project exists or null.
        final String project = jsonObject.get("key").getAsString();

        return project != null && project.equals(projectKey);
    }
}
