package fr.cnes.sonar.tests;/*
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

import fr.cnes.sonar.report.model.Report;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Check Report class
 */
public class ReportTest {

    /**
     * Simple text for testing messages
     */
    private static final String TEST_STRING = "This is a test string.";

    /**
     * Tested entity
     */
    private Report report;

    /**
     * Executed each time before running a single test
     */
    @Before
    public void prepare() {
        report = new Report();
    }

    /**
     * Check the default values
     */
    @Test
    public void defaultReportValuesTest() {
        assertEquals("", report.getProjectAuthor());
        assertEquals("", report.getProjectDate());
        assertEquals("", report.getProjectName());
        assertEquals("", report.getQualityProfilesFilename());
        assert(report.getRawIssues().isEmpty());
        assert(report.getQualityProfiles().isEmpty());
        assert(report.getFacets().isEmpty());
        assert(report.getIssues().isEmpty());
        assert(report.getMeasures().isEmpty());
    }

}
