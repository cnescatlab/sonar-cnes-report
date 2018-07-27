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
import fr.cnes.sonar.report.model.Language;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides languages
 * @author lequal
 */
public class LanguageProvider extends AbstractDataProvider {

    /**
     * Json's field containing the language's array
     */
    private static final String LANGUAGES_FIELD = "languages";

    /**
     * Map containing temporary memory of the data
     */
    private Map<String, Language> languages;

    /**
     * Complete constructor
     * @param url String representing the server address.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     */
    public LanguageProvider(final String url, final String token, final String project) {
        super(url, token, project);
        languages = new HashMap<>();
    }

    /**
     * Get the language corresponding to the given key
     * @param languageKey the key of the language
     * @return Language's name
     * @throws IOException when contacting the server
     * @throws BadSonarQubeRequestException when the server does not understand the request
     */
    public String getLanguage(final String languageKey)
            throws IOException, BadSonarQubeRequestException {
        if(languages.isEmpty()){
            this.getLanguages();
        }
        return this.languages.get(languageKey).getName();
    }

    /**
     * Get all the languages of SonarQube
     * @return a map with all the languages
     * @throws IOException when contacting the server
     * @throws BadSonarQubeRequestException when the server does not understand the request
     */
    public Map<String, Language> getLanguages() throws IOException, BadSonarQubeRequestException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        final JsonObject jo = request(String.format(getRequest(GET_LANGUAGES), getUrl()));
        final Language[] languagesList = getGson().fromJson(jo.get(LANGUAGES_FIELD),
                Language[].class);

        // put data in a map to access it faster
        this.languages = new HashMap<>();
        for(Language language : languagesList){
            this.languages.put(language.getKey(), language);
        }

        return this.languages;
    }


}
