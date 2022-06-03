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
package fr.cnes.sonar.report.exporters.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Issues;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;

public class IssuesAdapterTest {

    /**
     * Test that the issues severities list is returned in reverse order
     */
    @Test
    public void getReversedIssuesSeveritiesTest() {
        List<String> expected = Arrays.asList("INFO", "MINOR", "MAJOR", "CRITICAL", "BLOCKER");
        Assert.assertEquals(expected, IssuesAdapter.getReversedIssuesSeverities());
    }

    /**
     * Test that summary issues status is correctly generated
     */
    @Test
    public void getTypesTest() {
        Report report = Mockito.mock(Report.class);
        Issues issues = Mockito.mock(Issues.class);
        List<Issue> mockedValue = new ArrayList<>();

        // No issues in report
        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("BUG", "0", "0", "0", "0", "0"));
        expected.add(Arrays.asList("VULNERABILITY", "0", "0", "0", "0", "0"));
        expected.add(Arrays.asList("CODE_SMELL", "0", "0", "0", "0", "0"));
        Mockito.when(report.getIssues()).thenReturn(issues);
        Mockito.when(issues.getIssuesList()).thenReturn(mockedValue);
        Assert.assertEquals(expected, IssuesAdapter.getTypes(report));

        // Add some issues
        Issue issueBugMajor = new Issue();
        issueBugMajor.setType("BUG");
        issueBugMajor.setSeverity("MAJOR");
        Issue issueSmellMinor = new Issue();
        issueSmellMinor.setType("CODE_SMELL");
        issueSmellMinor.setSeverity("MINOR");
        Issue issueSmellCritical = new Issue();
        issueSmellCritical.setType("CODE_SMELL");
        issueSmellCritical.setSeverity("CRITICAL");
        Issue issueSmellCritical2 = new Issue();
        issueSmellCritical2.setType("CODE_SMELL");
        issueSmellCritical2.setSeverity("CRITICAL");
        Issue issueVulnInfo = new Issue();
        issueVulnInfo.setType("VULNERABILITY");
        issueVulnInfo.setSeverity("INFO");
        mockedValue.add(issueBugMajor);
        mockedValue.add(issueSmellMinor);
        mockedValue.add(issueSmellCritical);
        mockedValue.add(issueSmellCritical2);
        mockedValue.add(issueVulnInfo);
        Mockito.when(issues.getIssuesList()).thenReturn(mockedValue);

        // Check if result is OK with some issues
        expected = new ArrayList<>();
        expected.add(Arrays.asList("BUG", "0", "0", "1", "0", "0"));
        expected.add(Arrays.asList("VULNERABILITY", "1", "0", "0", "0", "0"));
        expected.add(Arrays.asList("CODE_SMELL", "0", "1", "0", "2", "0"));
        Assert.assertEquals(expected, IssuesAdapter.getTypes(report));
    }

    /**
     * Test if issues detailed list is correctly generated
     */
    @Test
    public void getIssuesTest() {
        Report report = Mockito.mock(Report.class);
        Issues issues = Mockito.mock(Issues.class);
        Map<String, Long> mockedValue = new HashMap<>();

        // No facets in reports
        List<List<String>> expected = new ArrayList<>();
        Mockito.when(report.getIssues()).thenReturn(issues);
        Mockito.when(issues.getIssuesFacets()).thenReturn(mockedValue);
        Assert.assertEquals(expected, IssuesAdapter.getIssues(report));

        // Add some issues facets
        mockedValue.put("test", Long.valueOf(42));
        mockedValue.put("test-external", Long.valueOf(1));
        mockedValue.put("fake", Long.valueOf(234));
        Mockito.when(issues.getIssuesFacets()).thenReturn(mockedValue);

        // Create test Rule and mock methods
        Rule rule = new Rule();
        rule.setName("test-rule");
        rule.setHtmlDesc("<p>description</p>");
        rule.setType("CODE_SMELL");
        rule.setSeverity("MINOR");
        Issue issue = new Issue();
        issue.setRule("test-external");
        issue.setMessage("<p>Test description<p>");
        issue.setType("BUG");
        issue.setSeverity("MAJOR");
        Mockito.when(report.getIssues()).thenReturn(issues);
        Mockito.when(issues.getFirstIssueMatchingRule("test-external")).thenReturn(issue);
        Mockito.when(report.getRule("test")).thenReturn(rule);
        Mockito.when(report.getRule("test-external")).thenReturn(null);
        Mockito.when(report.getRule("fake")).thenReturn(null);

        // Check result
        expected.add(Arrays.asList("test-rule", "description", "CODE_SMELL", "MINOR", "42"));
        expected.add(Arrays.asList("fake", "?", "?", "?", "234"));
        expected.add(Arrays.asList("test-external", "Test description", "BUG", "MAJOR", "1"));
        Assert.assertEquals(expected, IssuesAdapter.getIssues(report));
    }
}