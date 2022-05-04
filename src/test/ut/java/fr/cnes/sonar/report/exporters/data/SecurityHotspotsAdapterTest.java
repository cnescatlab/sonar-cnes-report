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

import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.model.SecurityHotspot;

public class SecurityHotspotsAdapterTest {

    /**
     * Check detailed list of Security Hotspot is correctly formatted
     */
    @Test
    public void getSecurityHotspotsByCategoryAndPriorityTest() {
        Report report = Mockito.mock(Report.class);
        List<SecurityHotspot> listIssues = new ArrayList<>();

        // Create some security hotspots
        SecurityHotspot secOk = new SecurityHotspot();
        secOk.setRule("rule");
        secOk.setSecurityCategory("log-injection");
        secOk.setVulnerabilityProbability("LOW");
        SecurityHotspot secOkBis = new SecurityHotspot();
        secOkBis.setRule("rule");
        secOkBis.setSecurityCategory("log-injection");
        secOkBis.setVulnerabilityProbability("LOW");
        listIssues.add(secOk);
        listIssues.add(secOkBis);

        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("LDAP Injection", "0", "0", "0"));
        expected.add(Arrays.asList("Object Injection", "0", "0", "0"));
        expected.add(Arrays.asList("Server-Side Request Forgery (SSRF)", "0", "0", "0"));
        expected.add(Arrays.asList("XML External Entity (XXE)", "0", "0", "0"));
        expected.add(Arrays.asList("Insecure Configuration", "0", "0", "0"));
        expected.add(Arrays.asList("XPath Injection", "0", "0", "0"));
        expected.add(Arrays.asList("Authentication", "0", "0", "0"));
        expected.add(Arrays.asList("Weak Cryptography", "0", "0", "0"));
        expected.add(Arrays.asList("Denial of Service (DoS)", "0", "0", "0"));
        expected.add(Arrays.asList("Log Injection", "2", "0", "0"));
        expected.add(Arrays.asList("Cross-Site Request Forgery (CSRF)", "0", "0", "0"));
        expected.add(Arrays.asList("Open Redirect", "0", "0", "0"));
        expected.add(Arrays.asList("Permission", "0", "0", "0"));
        expected.add(Arrays.asList("SQL Injection", "0", "0", "0"));
        expected.add(Arrays.asList("Encryption of Sensitive Data", "0", "0", "0"));
        expected.add(Arrays.asList("Traceability", "0", "0", "0"));
        expected.add(Arrays.asList("Buffer Overflow", "0", "0", "0"));
        expected.add(Arrays.asList("File Manipulation", "0", "0", "0"));
        expected.add(Arrays.asList("Code Injection (RCE)", "0", "0", "0"));
        expected.add(Arrays.asList("Cross-Site Scripting (XSS)", "0", "0", "0"));
        expected.add(Arrays.asList("Command Injection", "0", "0", "0"));
        expected.add(Arrays.asList("Path Traversal Injection", "0", "0", "0"));
        expected.add(Arrays.asList("HTTP Response Splitting", "0", "0", "0"));
        expected.add(Arrays.asList("Others", "0", "0", "0"));
        Mockito.when(report.getToReviewSecurityHotspots()).thenReturn(listIssues);
        Assert.assertEquals(expected, SecurityHotspotsAdapter.getSecurityHotspotsByCategoryAndPriority(report));
    }

    /**
     * Check summary Security Hotspot is correctly formatted
     */
    @Test
    public void getSecurityHotspots() {
        Report report = Mockito.mock(Report.class);
        List<SecurityHotspot> listIssues = new ArrayList<>();

        // No security hotpsots open in report
        List<List<String>> expected = new ArrayList<>();
        Mockito.when(report.getToReviewSecurityHotspots()).thenReturn(listIssues);
        Assert.assertEquals(expected, SecurityHotspotsAdapter.getSecurityHotspots(report));

        // Add some security hotspots
        SecurityHotspot secOk = new SecurityHotspot();
        secOk.setRule("rule");
        secOk.setSecurityCategory("log-injection");
        secOk.setVulnerabilityProbability("LOW");
        SecurityHotspot secOkBis = new SecurityHotspot();
        secOkBis.setRule("rule");
        secOkBis.setSecurityCategory("log-injection");
        secOkBis.setVulnerabilityProbability("LOW");
        SecurityHotspot secKo = new SecurityHotspot();
        secKo.setRule("not-in-profile");
        secKo.setSecurityCategory("CAT_2");
        secKo.setVulnerabilityProbability("HIGH");
        listIssues.add(secOk);
        listIssues.add(secKo);
        listIssues.add(secOkBis);
        Rule rule = new Rule();
        rule.setName("Rule");
        rule.setSeverity("MAJOR");
        Mockito.when(report.getToReviewSecurityHotspots()).thenReturn(listIssues);
        Mockito.when(report.getRule("rule")).thenReturn(rule);
        Mockito.when(report.getRule("not-in-profile")).thenReturn(null);

        expected = new ArrayList<>();
        expected.add(Arrays.asList("Log Injection", "Rule", "LOW", "MAJOR", "2"));
        Assert.assertEquals(expected, SecurityHotspotsAdapter.getSecurityHotspots(report));
    }
}
