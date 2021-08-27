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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.mockito.Mockito;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Languages;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.providers.language.LanguageProvider;
import fr.cnes.sonar.report.providers.language.LanguageProviderStandalone;

public class AbstractProjectProviderTest {

    @Test
    public void getEmptyProjectTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Create project data for the wrapper
        JsonObject project = new JsonObject();
        project.add("qualityProfiles", new JsonArray());

        ProjectProviderWrapper provider = new ProjectProviderWrapper(null);
        provider.setFakeProject(project);

        Project result = provider.getProject();
        assertNotNull(result.getDescription());
        assertNotNull(result.getVersion());
        assertEquals(0, result.getQualityProfiles().length);     
        assertEquals(0, result.getLanguages().size());         
    }

    @Test
    public void getProjectTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Mock language provider
        LanguageProvider languageProvider = Mockito.mock(LanguageProviderStandalone.class);
        Languages languages = Mockito.mock(Languages.class);
        Mockito.when(languageProvider.getLanguages()).thenReturn(languages);
        Mockito.when(languages.getLanguage("language_1")).thenReturn("Java");
        Mockito.when(languages.getLanguage("language_2")).thenReturn("Mova");

        // Create project data for the wrapper
        JsonObject qualityProfile_1 = new JsonObject();
        qualityProfile_1.addProperty("language", "language_1");
        JsonObject qualityProfile_2 = new JsonObject();
        qualityProfile_2.addProperty("language", "language_2");
        JsonArray qualityProfiles = new JsonArray();
        qualityProfiles.add(qualityProfile_1);
        qualityProfiles.add(qualityProfile_2);
        JsonObject project = new JsonObject();
        project.add("qualityProfiles", qualityProfiles);
        project.addProperty("description", "blablabla");
        project.addProperty("version", "1.2.3");

        ProjectProviderWrapper provider = new ProjectProviderWrapper(languageProvider);
        provider.setFakeProject(project);

        Project result = provider.getProject();
        assertEquals(2, result.getQualityProfiles().length);  
        assertEquals(2, result.getLanguages().size());    
        assertEquals("blablabla", result.getDescription());
        assertEquals("1.2.3", result.getVersion());
        
        for(Language lang: result.getLanguages()) {
            if(lang.getKey() == "language_1") {
                assertEquals("Java", lang.getName());
            } else {
                assertEquals("Mova", lang.getName());
            }
        }

        for(ProfileMetaData profile: result.getQualityProfiles()) {
            if(profile.getLanguage() == "language_1") {
                assertEquals("Java", profile.getLanguageName());
            } else {
                assertEquals("Mova", profile.getLanguageName());
            }
        }
    }

    @Test
    public void hasProjectTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Mock language provider
        LanguageProvider languageProvider = Mockito.mock(LanguageProviderStandalone.class);
        Languages languages = Mockito.mock(Languages.class);
        Mockito.when(languageProvider.getLanguages()).thenReturn(languages);
        Mockito.when(languages.getLanguage("language_1")).thenReturn("Language 1");

        // Create project data for the wrapper
        JsonObject project = new JsonObject();
        project.addProperty("key", "project_1");

        ProjectProviderWrapper provider = new ProjectProviderWrapper(languageProvider);
        provider.setFakeProject(project);

        Boolean result = provider.hasProject("project_1", "test");
        assertTrue(result);
        result = provider.hasProject("project_2", "test");
        assertFalse(result);

        JsonElement fakeProjectKey = Mockito.mock(JsonElement.class);
        Mockito.when(fakeProjectKey.getAsString()).thenReturn(null);
        project.add("key", fakeProjectKey);
        result = provider.hasProject("project_2", "test");
        assertFalse(result);
    }
    
    /**
     * Test class in order to test the abstract provider class
     */
    class ProjectProviderWrapper extends AbstractProjectProvider {

        JsonObject project;

        public ProjectProviderWrapper(final LanguageProvider pLanguageProvider) {
            super("server", "token", "project", "branch", pLanguageProvider);
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeProject(JsonObject pFake) {
            this.project = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getProjectAsJsonObject(final String projectKey, final String branch)
            throws BadSonarQubeRequestException, SonarQubeException {
            return this.project;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public Project getProject()
            throws BadSonarQubeRequestException, SonarQubeException {
            return getProjectAbstract("projectKey", "branch");
        }

        public boolean hasProject(final String projectKey, final String branch)
            throws BadSonarQubeRequestException, SonarQubeException {
            return hasProjectAbstract(projectKey, branch);
        }

    }

}
