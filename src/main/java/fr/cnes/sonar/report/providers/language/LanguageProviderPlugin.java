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
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Languages;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.languages.ListRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides languages in plugin mode
 */
public class LanguageProviderPlugin extends AbstractLanguageProvider implements LanguageProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     */
    public LanguageProviderPlugin(final WsClient wsClient, final String project) {
        super(wsClient, project);
    }

    @Override
    public Languages getLanguages() {
        // send a request to sonarqube server and return the response as a json object
        final ListRequest listRequest = new ListRequest();
        final String listResponse = getWsClient().languages().list(listRequest);
        final JsonObject jo = getGson().fromJson(listResponse, JsonObject.class);
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