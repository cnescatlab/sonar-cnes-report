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
import fr.cnes.sonar.report.model.Languages;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.languages.ListRequest;

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
    public Languages getLanguages() throws BadSonarQubeRequestException, SonarQubeException {
        return getLanguagesAbstract();
    }

    @Override
    protected JsonObject getLanguagesAsJsonObject() {
        final ListRequest listRequest = new ListRequest();
        final String listResponse = getWsClient().languages().list(listRequest);
        return getGson().fromJson(listResponse, JsonObject.class);
    }
}