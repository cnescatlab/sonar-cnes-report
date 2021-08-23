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

package fr.cnes.sonar.report.providers.issues;

import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AbstractIssuesProviderTest {
    
    @Test
    public void testNoIssues() throws BadSonarQubeRequestException, SonarQubeException {
        // Creates an empty response from SonarQube
        JsonArray issues = new JsonArray();
        JsonArray rules = new JsonArray();
        JsonObject empty = new JsonObject();
        empty.addProperty("total", 0);
        empty.add("issues", issues);
        empty.add("rules", rules);

        FakeIssuesProvider provider = new FakeIssuesProvider();
        provider.setFakeObject(empty);

        // Call methods from abstract class & check result
        List<Issue> issuesByStatus = provider.getIssuesByStatus();
        List<Map<String,String>> rawIssues = provider.getRawIssues();
        assertEquals(0, issuesByStatus.size());
        assertEquals(0, rawIssues.size());
    }

}

/**
 * Test class in order to test the abstract provider class
 */
class FakeIssuesProvider extends AbstractIssuesProvider {

    // Stores the fake JsonObject that the fake API will return
    private JsonObject fakeObject;

    public FakeIssuesProvider() {
        super("server", "token", "project", "branch");
    }

    /**
     * Sets the fake JsonObject that the API should return
     * @param pFake The fake JsonObject response from API
     */ 
    public void setFakeObject(JsonObject pFake) {
        this.fakeObject = pFake;
    }

    /**
     * Fake implementation of interface
     */
    public List<Issue> getIssuesByStatus() throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatusAbstract("confirmed");
    }

    /**
     * Fake implementation of interface
     */
    public List<Map<String,String>> getRawIssues() throws BadSonarQubeRequestException, SonarQubeException {
        return getRawIssuesAbstract();
    }

    /**
     * Implements a fake method to return the response from API
     */
    public JsonObject getIssuesAsJsonObject(final int page, final int maxPerPage, final String confirmed) throws BadSonarQubeRequestException, SonarQubeException {
        return this.fakeObject;
    }
}
