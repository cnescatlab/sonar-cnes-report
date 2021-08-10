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

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Languages;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for language providers
 */
public abstract class AbstractLanguageProvider extends AbstractDataProvider {

    /**
     * Json's field containing the language's array
     */
    private static final String LANGUAGES_FIELD = "languages";

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

    /**
     * Generic getter for all the languages of SonarQube
     * @return a map with all the languages
     * @throws BadSonarQubeRequestException when the server does not understand the request
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected Languages getLanguagesAbstract() throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return the response as a json object
        final JsonObject jo = getLanguagesAsJsonObject();
        final Language[] languagesList = getGson().fromJson(jo.get(LANGUAGES_FIELD),
                Language[].class);

        // put data in a Languages object
        Map<String, Language> languagesMap = new HashMap<>();
        for(Language language : languagesList){
            languagesMap.put(language.getKey(), language);
        }
        Languages languages = new Languages();
        languages.setLanguages(languagesMap);

        return languages;
    }

    /**
     * Get a JsonObject from the response of a get languages request.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getLanguagesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException;
}