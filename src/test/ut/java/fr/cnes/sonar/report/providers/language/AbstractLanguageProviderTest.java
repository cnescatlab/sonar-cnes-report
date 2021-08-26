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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Languages;

public class AbstractLanguageProviderTest {

    @Test
    public void getEmptyLanguagesTest() throws BadSonarQubeRequestException, SonarQubeException {
        JsonObject languages = new JsonObject();
        languages.add("languages", new JsonArray());

        LanguageProviderWrapper provider = new LanguageProviderWrapper();
        provider.setFakeLanguages(languages);

        Languages result = provider.getLanguages();
        assertEquals(0, result.getLanguages().size());
    }

    @Test
    public void getLanguagesTest() throws BadSonarQubeRequestException, SonarQubeException {
        JsonObject language1 = new JsonObject();
        language1.addProperty("key", "language_1");
        language1.addProperty("name", "Java");
        JsonObject language2 = new JsonObject();
        language2.addProperty("key", "language_2");
        language2.addProperty("name", "Mova");
        JsonArray languageList = new JsonArray();
        languageList.add(language1);
        languageList.add(language2);
        JsonObject languages = new JsonObject();
        languages.add("languages", languageList);

        LanguageProviderWrapper provider = new LanguageProviderWrapper();
        provider.setFakeLanguages(languages);

        Languages result = provider.getLanguages();
        assertEquals(2, result.getLanguages().size());
        assertEquals("Java", result.getLanguage("language_1"));
        assertEquals("Mova", result.getLanguage("language_2"));
    }
    
    /**
     * Test class in order to test the abstract provider class
     */
    class LanguageProviderWrapper extends AbstractLanguageProvider {

        JsonObject languages;

        public LanguageProviderWrapper() {
            super("server", "token", "project");
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeLanguages(JsonObject pFake) {
            this.languages = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getLanguagesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
            return this.languages;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public Languages getLanguages() throws BadSonarQubeRequestException, SonarQubeException {
            return getLanguagesAbstract();
        }

    }

}
