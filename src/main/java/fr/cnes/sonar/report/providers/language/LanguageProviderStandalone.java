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
import fr.cnes.sonar.report.model.Languages;
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
        return getLanguagesAbstract();
    }

    @Override
    protected JsonObject getLanguagesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
        return request(String.format(getRequest(GET_LANGUAGES), getServer()));
    }
}
