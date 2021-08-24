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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Issue;


public class AbstractIssuesProviderTest {

    /**
     *  Name of the property for the maximum number of results per page
     */
    protected static final String MAX_PER_PAGE_SONARQUBE = "MAX_PER_PAGE_SONARQUBE";
    /**
     * Correspond to the maximum number of issues that SonarQube allow
     * web api's users to collect.
     */
    private static final int MAXIMUM_ISSUES_LIMIT = 10000;
    
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

    @Test
    public void testIssuesWithCorrespondingRules() throws BadSonarQubeRequestException, SonarQubeException {
        // Creates a response from SonarQube with some issues matching rules
        JsonObject issue1 = new JsonObject();
        issue1.addProperty("key", "AXs6TiZb_DAZnba7Q_0P");
        issue1.addProperty("rule", "java:S112");
        JsonObject issue2 = new JsonObject();
        issue2.addProperty("key", "AXs6TiZb_DAZnba7Q_0Q");
        issue2.addProperty("rule", "java:S100");
        JsonArray issues = new JsonArray();
        issues.add(issue1);
        issues.add(issue2);

        JsonObject rule1 = new JsonObject();
        rule1.addProperty("key", "java:S112");
        rule1.addProperty("langName", "Java");
        JsonObject rule2 = new JsonObject();
        rule2.addProperty("key", "java:S100");
        rule2.addProperty("langName", "Java");
        JsonArray rules = new JsonArray();
        rules.add(rule1);
        rules.add(rule2);

        JsonObject response = new JsonObject();
        response.addProperty("total", 2);
        response.add("issues", issues);
        response.add("rules", rules);

        FakeIssuesProvider provider = new FakeIssuesProvider();
        provider.setFakeObject(response);

        // Call methods from abstract class & check result
        List<Issue> issuesByStatus = provider.getIssuesByStatus();
        List<Map<String,String>> rawIssues = provider.getRawIssues();
        assertEquals(2, issuesByStatus.size());
        assertEquals("AXs6TiZb_DAZnba7Q_0P", issuesByStatus.get(0).getKey());
        assertEquals("AXs6TiZb_DAZnba7Q_0Q", issuesByStatus.get(1).getKey());
        assertEquals("java:S112", issuesByStatus.get(0).getRule());
        assertEquals("java:S100", issuesByStatus.get(1).getRule());
        assertEquals("Java", issuesByStatus.get(0).getLanguage());
        assertEquals("Java", issuesByStatus.get(1).getLanguage());
        assertEquals(2, rawIssues.size());
    }

    @Test
    public void testIssueWithNoCorrespondingRule() throws BadSonarQubeRequestException, SonarQubeException {
        // Creates a response from SonarQube with some issues matching rules
        JsonObject issue = new JsonObject();
        issue.addProperty("key", "AXs6TiZb_DAZnba7Q_0P");
        issue.addProperty("rule", "java:S112");
        JsonArray issues = new JsonArray();
        issues.add(issue);

        JsonArray rules = new JsonArray();

        JsonObject response = new JsonObject();
        response.addProperty("total", 1);
        response.add("issues", issues);
        response.add("rules", rules);

        FakeIssuesProvider provider = new FakeIssuesProvider();
        provider.setFakeObject(response);

        // Call methods from abstract class & check result
        List<Issue> issuesByStatus = provider.getIssuesByStatus();
        assertEquals(1, issuesByStatus.size());
        assertEquals("AXs6TiZb_DAZnba7Q_0P", issuesByStatus.get(0).getKey());
        assertEquals("java:S112", issuesByStatus.get(0).getRule());
        assertEquals("", issuesByStatus.get(0).getLanguage());
    }

    @Test
    public void testMultiplePages() throws BadSonarQubeRequestException, SonarQubeException {
        // Creates a response from SonarQube with some issues matching rules
        JsonObject issue = new JsonObject();
        issue.addProperty("key", "AXs6TiZb_DAZnba7Q_0P");
        issue.addProperty("rule", "java:S112");
        JsonArray issues = new JsonArray();
        issues.add(issue);

        JsonObject rule = new JsonObject();
        rule.addProperty("key", "java:S112");
        rule.addProperty("langName", "Java");
        JsonArray rules = new JsonArray();
        rules.add(rule);

        FakeIssuesProvider provider = new FakeIssuesProvider();

        JsonObject response = new JsonObject();
        int maxPerPage = Integer.parseInt(provider.getProperty(MAX_PER_PAGE_SONARQUBE));
        int fakeNumberOfIssues = maxPerPage * 2;
        response.addProperty("total", fakeNumberOfIssues);
        response.add("issues", issues);
        response.add("rules", rules);
        provider.setFakeObject(response);

        // Call methods from abstract class & check result
        List<Issue> issuesByStatus = provider.getIssuesByStatus();
        List<Map<String,String>> rawIssues = provider.getRawIssues();
        assertEquals(2, issuesByStatus.size());
        assertEquals(issuesByStatus.get(0).getKey(), issuesByStatus.get(1).getKey());
        assertEquals(issuesByStatus.get(0).getRule(), issuesByStatus.get(1).getRule());
        assertEquals(issuesByStatus.get(0).getLanguage(), issuesByStatus.get(1).getLanguage());
        assertEquals(2, rawIssues.size());
    }

    @Test
    public void testTooManyIssues() throws BadSonarQubeRequestException, SonarQubeException {
        // Creates a response from SonarQube with some issues matching rules
        JsonObject issue = new JsonObject();
        issue.addProperty("key", "AXs6TiZb_DAZnba7Q_0P");
        issue.addProperty("rule", "java:S112");
        JsonArray issues = new JsonArray();
        issues.add(issue);

        JsonObject rule = new JsonObject();
        rule.addProperty("key", "java:S112");
        rule.addProperty("langName", "Java");
        JsonArray rules = new JsonArray();
        rules.add(rule);

        JsonObject response = new JsonObject();
        response.addProperty("total", MAXIMUM_ISSUES_LIMIT + 1);
        response.add("issues", issues);
        response.add("rules", rules);

        FakeIssuesProvider provider = new FakeIssuesProvider();
        provider.setFakeObject(response);

        // Call methods from abstract class & check result
        List<Issue> issuesByStatus = provider.getIssuesByStatus();
        List<Map<String,String>> rawIssues = provider.getRawIssues();
        assertTrue(issuesByStatus.size() <= MAXIMUM_ISSUES_LIMIT);
        assertTrue(rawIssues.size() <= MAXIMUM_ISSUES_LIMIT);
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

    /**
     * Call parent method to get properties
     */
    public String getProperty(String property) {
        return getRequest(MAX_PER_PAGE_SONARQUBE);
    }
}
