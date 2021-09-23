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

package fr.cnes.sonar.report.providers.securityhotspots;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.SecurityHotspot;

public class AbstractSecurityHotspotsProviderTest {

    @Test
    public void getEmptySecurityHotspotsTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Create empty list of security hotspots
        JsonObject paging = new JsonObject();
        paging.addProperty("total", 0);
        JsonObject hotspots = new JsonObject();
        hotspots.add("hotspots", new JsonArray());
        hotspots.add("paging", paging);

        SecurityHotspotsProviderWrapper provider = new SecurityHotspotsProviderWrapper();
        provider.setFakeHotspots(hotspots);

        List<SecurityHotspot> result = provider.getSecurityHotspotsByStatus("status");
        assertEquals(0, result.size());
    }

    @Test
    public void getSecurityHotspotsTest() throws BadSonarQubeRequestException, SonarQubeException {
        // Create list of security hotspots
        JsonObject hotspot1 = new JsonObject();
        hotspot1.addProperty("key", "key_1");
        JsonObject hotspot2 = new JsonObject();
        hotspot2.addProperty("key", "key_2");
        JsonArray hotspotsList = new JsonArray();
        hotspotsList.add(hotspot1);
        hotspotsList.add(hotspot2);
        JsonObject paging = new JsonObject();
        paging.addProperty("total", 502);
        JsonObject hotspots = new JsonObject();
        hotspots.add("hotspots", hotspotsList);
        hotspots.add("paging", paging);

        // Create security hotspot details
        JsonObject hotspotRule = new JsonObject();
        hotspotRule.addProperty("key", "rule_1");
        JsonArray comments = new JsonArray();
        comments.add(new JsonObject());
        JsonObject hotspotDetails = new JsonObject();
        hotspotDetails.add("rule", hotspotRule);
        hotspotDetails.add("comments", comments);
        hotspotDetails.addProperty("resolution", "RESOLVED");

        // Create rule details
        JsonObject rule = new JsonObject();
        rule.addProperty("severity", "MAJOR");
        rule.addProperty("langName", "Java");
        JsonObject ruleDetails = new JsonObject();
        ruleDetails.add("rule", rule);
        
        SecurityHotspotsProviderWrapper provider = new SecurityHotspotsProviderWrapper();
        provider.setFakeHotspots(hotspots);
        provider.setFakeHotspot(hotspotDetails);
        provider.setFakeRule(ruleDetails);

        List<SecurityHotspot> result = provider.getSecurityHotspotsByStatus("ANYTHING");
        assertEquals(4, result.size());
        for(SecurityHotspot sec: result) {
            assertNotSame("RESOLVED", sec.getResolution());
            assertSame("Java", sec.getLanguage());
            assertSame("MAJOR", sec.getSeverity());
            assertSame("rule_1", sec.getRule());
        }

        result = provider.getSecurityHotspotsByStatus("REVIEWED");
        assertEquals(4, result.size());
        for(SecurityHotspot sec: result) {
            assertSame("RESOLVED", sec.getResolution());
            assertSame("Java", sec.getLanguage());
            assertSame("MAJOR", sec.getSeverity());
            assertSame("rule_1", sec.getRule());
        }
    }

    /**
     * Test class in order to test the abstract provider class
     */
    class SecurityHotspotsProviderWrapper extends AbstractSecurityHotspotsProvider {

        JsonObject hotspots;
        JsonObject hotspot;
        JsonObject rule;

        public SecurityHotspotsProviderWrapper() {
            super("server", "token", "project", "branch");
        }

        /**
         * Sets the fake JsonObject that the API should return
         * 
         * @param pFake The fake JsonObject response from API
         */
        public void setFakeHotspots(JsonObject pFake) {
            this.hotspots = pFake;
        }

        public void setFakeHotspot(JsonObject pFake) {
            this.hotspot = pFake;
        }

        public void setFakeRule(JsonObject pFake) {
            this.rule = pFake;
        }

        /**
         * Wrapper methods to mock the API response
         */
        protected JsonObject getSecurityHotspotsAsJsonObject(final int page, final int maxPerPage, final String status)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.hotspots;
        }

        protected JsonObject getSecurityHotspotAsJsonObject(final String securityHotspotKey)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.hotspot;
        }

        protected JsonObject getRuleAsJsonObject(final String securityHotspotRule)
                throws BadSonarQubeRequestException, SonarQubeException {
            return this.rule;
        }

        /**
         * Wrapper public methods to call corresponding parent private methods
         */
        public List<SecurityHotspot> getSecurityHotspotsByStatus(final String status)
                throws BadSonarQubeRequestException, SonarQubeException {
            return getSecurityHotspotsByStatusAbstract(status);
        }

    }

}
