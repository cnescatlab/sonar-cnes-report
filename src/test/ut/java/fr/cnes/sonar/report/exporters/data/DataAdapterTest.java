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

import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.model.Report;

public class DataAdapterTest {

    /**
     * Check metrics are returned in the desired format
     */
    @Test
    public void getMetricsTest() {
        Report report = Mockito.mock(Report.class);
        List<Measure> measures = new ArrayList<>();
        measures.add(new Measure("lines", "200"));
        measures.add(new Measure("functions", "3"));
        measures.add(new Measure("classes", "1"));
        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("lines", "200"));
        expected.add(Arrays.asList("functions", "3"));
        expected.add(Arrays.asList("classes", "1"));

        Mockito.when(report.getMeasures()).thenReturn(measures);
        Assert.assertEquals(true, expected.equals(DataAdapter.getMetrics(report)));
    }

    /**
     * Check Quality Gate status is returned in the desired format
     */
    @Test
    public void getQualityGateStatusTest() {
        Report report = Mockito.mock(Report.class);
        Map<String, String> qualityGate = new HashMap<>();
        qualityGate.put("status1", "OK");
        qualityGate.put("status2", "KO");
        qualityGate.put("status3", "OK");
        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("status1", "OK"));
        expected.add(Arrays.asList("status2", "KO"));
        expected.add(Arrays.asList("status3", "OK"));

        Mockito.when(report.getQualityGateStatus()).thenReturn(qualityGate);
        Assert.assertEquals(true, expected.equals(DataAdapter.getQualityGateStatus(report)));
    }

    /**
     * Check volume metrics are returned in the desired format
     */
    @Test
    public void getVolumesTest() {
        Report report = Mockito.mock(Report.class);
        Project project = Mockito.mock(Project.class);
        Language java = new Language();
        java.setName("Java");
        Language python = new Language();
        python.setName("Python");
        Mockito.when(report.getProject()).thenReturn(project);
        Mockito.when(project.getLanguage("java")).thenReturn(java);
        Mockito.when(project.getLanguage("python")).thenReturn(python);
        List<Measure> measures = new ArrayList<>();
        measures.add(new Measure("ncloc", "123"));
        Mockito.when(report.getMeasures()).thenReturn(measures);

        // Missing one metric
        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList(""));
        expected.add(Arrays.asList("Total", "123"));
        Assert.assertEquals(expected, DataAdapter.getVolumes(report));

        // Metrics are all OK
        measures.add(new Measure("ncloc_language_distribution", "java=200;python=13456"));
        Mockito.when(report.getMeasures()).thenReturn(measures);
        expected = new ArrayList<>();
        expected.add(Arrays.asList("Java", "200"));
        expected.add(Arrays.asList("Python", "13456"));
        expected.add(Arrays.asList("Total", "123"));
        Assert.assertEquals(expected, DataAdapter.getVolumes(report));
    }

    /**
     * Check detailed technical debt is returned in the desired format
     */
    @Test
    public void getDetailedTechnicalDebtTest() {
        Report report = Mockito.mock(Report.class);
        List<Measure> measures = new ArrayList<>();

        // Test with existing technical debt effort
        measures.add(new Measure("reliability_remediation_effort", "504"));
        measures.add(new Measure("security_remediation_effort", "318"));
        measures.add(new Measure("sqale_index", "10715"));
        Mockito.when(report.getMeasures()).thenReturn(measures);

        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("1d 0h 24min", "0d 5h 18min", "22d 2h 35min", "24d 0h 17min"));
        Assert.assertEquals(expected, DataAdapter.getDetailedTechnicalDebt(report));

        // Test with no debt
        measures = new ArrayList<>();
        measures.add(new Measure("reliability_remediation_effort", "0"));
        measures.add(new Measure("security_remediation_effort", "0"));
        measures.add(new Measure("sqale_index", "0"));
        Mockito.when(report.getMeasures()).thenReturn(measures);

        expected = new ArrayList<>();
        expected.add(Arrays.asList("-", "-", "-", "-"));
        Assert.assertEquals(expected, DataAdapter.getDetailedTechnicalDebt(report));
    }

}
