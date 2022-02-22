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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;

public class RuleComparatorTest {

    /**
     * Test the comparator when rules are empty
     */
    @Test
    public void emptyRulesTest() {
        Report report = Mockito.mock(Report.class);
        Map<String, Long> mockedValue = new HashMap<>();
        Map<String, Long> sortedItems = new TreeMap<>();
        boolean first = true;

        // First rule is empty
        mockedValue.put("", Long.valueOf(1));
        mockedValue.put("test", Long.valueOf(2));
        sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(mockedValue);

        // Check if empty rule comes first
        for (Map.Entry<String, Long> item : sortedItems.entrySet()) {
            if (first) {
                first = false;
                Assert.assertTrue(item.getKey() == "" && item.getValue() == Long.valueOf(1));
            } else {
                Assert.assertTrue(item.getKey() == "test" && item.getValue() == Long.valueOf(2));
            }
        }

        // Second rule is empty
        mockedValue = new HashMap<>();
        mockedValue.put("test", Long.valueOf(2));
        mockedValue.put("", Long.valueOf(1));
        sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(mockedValue);

        // Check if empty rule also comes first
        first = true;
        for (Map.Entry<String, Long> item : sortedItems.entrySet()) {
            if (first) {
                first = false;
                Assert.assertTrue(item.getKey() == "" && item.getValue() == Long.valueOf(1));
            } else {
                Assert.assertTrue(item.getKey() == "test" && item.getValue() == Long.valueOf(2));
            }
        }
    }

    /**
     * Test the comparator when rules are null
     */
    @Test
    public void nullRulesTest() {
        Report report = Mockito.mock(Report.class);
        Map<String, Long> mockedValue = new HashMap<>();
        Map<String, Long> sortedItems = new TreeMap<>();
        Rule rule = new Rule();
        rule.setKey("KEY");
        rule.setType("TYPE");
        rule.setSeverity("SEVERITY");
        boolean first = true;

        // One rule is null
        mockedValue.put("fake", Long.valueOf(1));
        mockedValue.put("test", Long.valueOf(2));
        Mockito.when(report.getRule("test")).thenReturn(rule);
        Mockito.when(report.getRule("fake")).thenReturn(null);
        sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(mockedValue);

        // Check if empty rule test comes first
        for (Map.Entry<String, Long> item : sortedItems.entrySet()) {
            if (first) {
                first = false;
                Assert.assertTrue(item.getKey() == "test" && item.getValue() == Long.valueOf(2));
            } else {
                Assert.assertTrue(item.getKey() == "fake" && item.getValue() == Long.valueOf(1));
            }
        }
    }

    /**
     * Test the comparator when rules are not empty nor null, but of different types
     */
    @Test
    public void rulesTypeTest() {
        Report report = Mockito.mock(Report.class);
        Map<String, Long> mockedValue = new HashMap<>();
        Map<String, Long> sortedItems = new TreeMap<>();
        Rule ruleSmell = new Rule();
        ruleSmell.setType("CODE_SMELL");
        Rule ruleVuln = new Rule();
        ruleVuln.setType("VULNERABILITY");
        Rule ruleBug = new Rule();
        ruleBug.setType("BUG");
        boolean first = true;
        boolean second = false;

        // Add rules with different types
        mockedValue.put("smell", Long.valueOf(1));
        mockedValue.put("vuln", Long.valueOf(2));
        mockedValue.put("bug", Long.valueOf(3));
        Mockito.when(report.getRule("smell")).thenReturn(ruleSmell);
        Mockito.when(report.getRule("vuln")).thenReturn(ruleVuln);
        Mockito.when(report.getRule("bug")).thenReturn(ruleBug);
        sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(mockedValue);

        // Check if rules are ordered by type
        for (Map.Entry<String, Long> item : sortedItems.entrySet()) {
            if (first) {
                first = false;
                second = true;
                Assert.assertTrue(item.getKey() == "bug" && item.getValue() == Long.valueOf(3));
            } else if (second && !first) {
                second = false;
                Assert.assertTrue(item.getKey() == "smell" && item.getValue() == Long.valueOf(1));
            } else {
                Assert.assertTrue(item.getKey() == "vuln" && item.getValue() == Long.valueOf(2));
            }
        }
    }

    /**
     * Test the comparator when rules are not empty nor null, of same type but
     * different severities
     */
    @Test
    public void rulesSeveritiesTest() {
        Report report = Mockito.mock(Report.class);
        Map<String, Long> mockedValue = new HashMap<>();
        Map<String, Long> sortedItems = new TreeMap<>();
        Rule ruleMajor = new Rule();
        ruleMajor.setType("CODE_SMELL");
        ruleMajor.setSeverity("MAJOR");
        Rule ruleBlocker = new Rule();
        ruleBlocker.setType("CODE_SMELL");
        ruleBlocker.setSeverity("BLOCKER");
        Rule ruleCritical = new Rule();
        ruleCritical.setType("CODE_SMELL");
        ruleCritical.setSeverity("CRITICAL");
        boolean first = true;
        boolean second = false;

        // Add rules with different types
        mockedValue.put("major", Long.valueOf(1));
        mockedValue.put("blocker", Long.valueOf(2));
        mockedValue.put("critical", Long.valueOf(3));
        Mockito.when(report.getRule("major")).thenReturn(ruleMajor);
        Mockito.when(report.getRule("blocker")).thenReturn(ruleBlocker);
        Mockito.when(report.getRule("critical")).thenReturn(ruleCritical);
        sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(mockedValue);

        // Check if rules are ordered by type
        for (Map.Entry<String, Long> item : sortedItems.entrySet()) {
            if (first) {
                first = false;
                second = true;
                Assert.assertTrue(item.getKey() == "blocker" && item.getValue() == Long.valueOf(2));
            } else if (second && !first) {
                second = false;
                Assert.assertTrue(item.getKey() == "critical" && item.getValue() == Long.valueOf(3));
            } else {
                Assert.assertTrue(item.getKey() == "major" && item.getValue() == Long.valueOf(1));
            }
        }
    }

    /**
     * Test the comparator when rules are not empty nor null, of same type and
     * severity but different keys
     */
    @Test
    public void rulesKeyTest() {
        Report report = Mockito.mock(Report.class);
        Map<String, Long> mockedValue = new HashMap<>();
        Map<String, Long> sortedItems = new TreeMap<>();
        Rule ruleBType = new Rule();
        ruleBType.setKey("ruleBType");
        ruleBType.setType("CODE_SMELL");
        ruleBType.setSeverity("MAJOR");
        Rule ruleVType = new Rule();
        ruleVType.setKey("ruleVType");
        ruleVType.setType("CODE_SMELL");
        ruleVType.setSeverity("MAJOR");
        Rule ruleCType = new Rule();
        ruleCType.setKey("ruleCType");
        ruleCType.setType("CODE_SMELL");
        ruleCType.setSeverity("MAJOR");
        boolean first = true;
        boolean second = false;

        // Add rules with different types
        mockedValue.put("ruleBType", Long.valueOf(1));
        mockedValue.put("ruleVType", Long.valueOf(2));
        mockedValue.put("ruleCType", Long.valueOf(3));
        Mockito.when(report.getRule("ruleBType")).thenReturn(ruleBType);
        Mockito.when(report.getRule("ruleVType")).thenReturn(ruleVType);
        Mockito.when(report.getRule("ruleCType")).thenReturn(ruleCType);
        sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(mockedValue);

        // Check if rules are ordered by type
        for (Map.Entry<String, Long> item : sortedItems.entrySet()) {
            if (first) {
                first = false;
                second = true;
                Assert.assertTrue(item.getKey() == "ruleBType" && item.getValue() == Long.valueOf(1));
            } else if (second && !first) {
                second = false;
                Assert.assertTrue(item.getKey() == "ruleCType" && item.getValue() == Long.valueOf(3));
            } else {
                Assert.assertTrue(item.getKey() == "ruleVType" && item.getValue() == Long.valueOf(2));
            }
        }
    }

}
