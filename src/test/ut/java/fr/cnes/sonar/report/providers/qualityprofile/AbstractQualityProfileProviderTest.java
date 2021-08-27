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

package fr.cnes.sonar.report.providers.qualityprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Rule;

public class AbstractQualityProfileProviderTest {

    @Test
    public void getQualityProfilesTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Set fake profiles
        JsonObject profile1 = new JsonObject();
        profile1.addProperty("key", "key 1");
        JsonObject profile2 = new JsonObject();
        profile2.addProperty("key", "key 2");
        JsonArray profiles = new JsonArray();
        profiles.add(profile1);
        profiles.add(profile2);
        JsonObject qualityProfiles = new JsonObject();
        qualityProfiles.add("profiles", profiles);

        // Set fake rules
        JsonObject rule1 = new JsonObject();
        rule1.addProperty("key", "rule_1");
        JsonObject rule2 = new JsonObject();
        rule2.addProperty("key", "rule_2");
        JsonArray rulesList = new JsonArray();
        rulesList.add(rule1);
        rulesList.add(rule2);

        // Set fake active rules
        JsonObject ruleActiveDetails = new JsonObject();
        ruleActiveDetails.addProperty("severity", "MAJOR");
        JsonArray ruleActives = new JsonArray();
        ruleActives.add(ruleActiveDetails);
        JsonObject actives = new JsonObject();
        actives.add("rule_2", ruleActives);
        // set fake rule API response
        JsonObject rules = new JsonObject();
        rules.add("rules", rulesList);
        rules.add("actives", actives);
        rules.addProperty("total", 502);
        // set fake projet API response
        JsonObject projects = new JsonObject();
        projects.add("results", new JsonArray());

        QualityProfileProviderWrapper provider = new QualityProfileProviderWrapper();
        provider.setFakeQualityProfiles(qualityProfiles);
        provider.setFakeXmlConf("random_fake_conf_as_string");
        provider.setFakeRules(rules);
        provider.setFakeProjects(projects);

        List<QualityProfile> result = provider.getQualityProfiles();
        assertEquals(2, result.size());

        for(QualityProfile qp: result) {
            for(Rule rule: qp.getRules()) {
                if(rule.getKey() == "rule_2") {
                    assertEquals("MAJOR", rule.getSeverity().toString());
                } else {
                    assertSame("", rule.getSeverity().toString());
                }
            }
        }
    }

    /**
     * Test class in order to test the abstract provider class
     */
    class QualityProfileProviderWrapper extends AbstractQualityProfileProvider {

        // Stores the fake JsonObject that the fake API will return
        private JsonObject fakeQualityProfiles;
        private String fakeXmlConf;
        private JsonObject fakeRules;
        private JsonObject fakeProjects;

        public QualityProfileProviderWrapper() {
            super("server", "token", "project");
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeQualityProfiles(JsonObject pFake) {
            this.fakeQualityProfiles = pFake;
        }

        public void setFakeXmlConf(String pFake) {
            this.fakeXmlConf = pFake;
        }

        public void setFakeRules(JsonObject pFake) {
            this.fakeRules = pFake;
        }

        public void setFakeProjects(JsonObject pFake) {
            this.fakeProjects = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getQualityProfilesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
            return this.fakeQualityProfiles;
        }

        protected String getQualityProfilesConfAsXml(final ProfileMetaData profileMetaData)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.fakeXmlConf;
        }

        protected JsonObject getQualityProfilesRulesAsJsonObject(final int page, final String profileKey)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.fakeRules;
        }

        protected JsonObject getQualityProfilesProjectsAsJsonObject(final ProfileMetaData profileMetaData)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.fakeProjects;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public List<QualityProfile> getQualityProfiles() throws BadSonarQubeRequestException, SonarQubeException {
            return getQualityProfilesAbstract();
        }

    }

}
