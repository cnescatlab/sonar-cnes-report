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

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.providers.AbstractDataProvider;
import fr.cnes.sonar.report.providers.language.LanguageProvider;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

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

    /**
     * Generic getter for the project corresponding to the given key.
     * @param projectKey the key of the project.
     * @param branch the branch of the project.
     * @return A simple project.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected Project getProjectAbstract(final String projectKey, final String branch)
            throws BadSonarQubeRequestException, SonarQubeException {
        final JsonObject jo = getProjectAsJsonObject(projectKey, branch);

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

    /**
     * Generic method to check if a project exists on a SonarQube instance.
     * @param projectKey the key of the project.
     * @param branch the branch of the project.
     * @return True if the project exists.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected boolean hasProjectAbstract(final String projectKey, final String branch)
            throws BadSonarQubeRequestException, SonarQubeException {
        final JsonObject jsonObject = getProjectAsJsonObject(projectKey, branch);

        // Retrieve project key if the project exists or null.
        final String project = jsonObject.get("key").getAsString();

        return project != null && project.equals(projectKey);
    }

    /**
     * Get a JsonObject from the response of a get component request.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getProjectAsJsonObject(final String projectKey, final String branch)
            throws BadSonarQubeRequestException, SonarQubeException;
}