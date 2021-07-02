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

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Languages;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides languages in standalone mode
 */
public class LanguageProviderStandalone extends AbstractLanguageProvider implements LanguageProvider {

    /**
     *  Field to retrieve languages list.
     */
    private static final String GET_LANGUAGES = "GET_LANGUAGES";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     */
    public LanguageProviderStandalone(final String pServer, final String pToken, final String pProject) {
        super(pServer, pToken, pProject);
    }

    @Override
    public Languages getLanguages() throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return the response as a json object
        // if there is an error on server side this method throws an exception
        final JsonObject jo = request(String.format(getRequest(GET_LANGUAGES), getServer()));
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
}
