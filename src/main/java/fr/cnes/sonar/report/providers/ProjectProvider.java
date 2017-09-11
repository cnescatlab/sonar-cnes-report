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
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;

import java.io.IOException;

/**
 * Provides basic project's information
 * @author lequal
 */
public class ProjectProvider extends AbstractDataProvider {

    private LanguageProvider languageProvider;

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public ProjectProvider(Params params, RequestManager singleton) throws UnknownParameterException {
        super(params, singleton);
        languageProvider = new LanguageProvider(params, singleton);
    }

    /**
     * Get the project corresponding to the given key
     * @param projectKey the key of the project
     * @return A simple project
     * @throws IOException when contacting the server
     * @throws BadSonarQubeRequestException when the server does not understand the request
     */
    public Project getProject(String projectKey) throws IOException, BadSonarQubeRequestException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        JsonObject jo = request(String.format(getRequest(GET_PROJECT_REQUEST),
                getUrl(), projectKey));

        // put json in a Project class
        Project project = (getGson().fromJson(jo, Project.class));

        // set language's name for profiles
        ProfileMetaData[] metaData = project.getQualityProfiles();
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

        return project;
    }
}
