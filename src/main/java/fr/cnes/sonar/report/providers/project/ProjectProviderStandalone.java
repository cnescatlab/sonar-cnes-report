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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.utils.StringManager;
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
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        JsonObject jo = request(String.format(getRequest(GET_PROJECT_REQUEST),
                getServer(), projectKey, branch));

        // put json in a Project class
        final Project project = (getGson().fromJson(jo, Project.class));
        ProfileMetaData[] metaData;

        // set language's name for profiles and add each language to the project languages list
        metaData = project.getQualityProfiles();        
        String languageName;
        Map<String, Language> languages = new HashMap<>();
        for(ProfileMetaData it : metaData){
            String languageKey = it.getLanguage();

            languageName = languageProvider.getLanguages().getLanguage(languageKey);
            it.setLanguageName(languageName);

            Language language = new Language();
            language.setKey(languageKey);
            language.setName(languageName);
            languages.put(languageKey, language);
        }
        project.setQualityProfiles(metaData);
        project.setLanguages(languages);

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

    @Override
    public boolean hasProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        final JsonObject jsonObject = request(String.format(getRequest(GET_PROJECT_REQUEST),
                getServer(), projectKey, branch));

        // Retrieve project key if the project exists or null.
        final String project = jsonObject.get("key").getAsString();

        return project != null && project.equals(projectKey);
    }
}
