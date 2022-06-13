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

package fr.cnes.sonar.report;

import fr.cnes.sonar.plugin.tools.ZipFolder;
import fr.cnes.sonar.report.model.Report;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Check Report class
 */
public class ReportTest {
    /**
     * Define target folder for test
     */
    private String TARGET = "./target/zip";

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

        File zip = new File(TARGET+".zip");
        if(zip.exists()){
            zip.delete();
        }
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
        assert(report.getIssues().getIssuesList().isEmpty());
        assert(report.getMeasures().isEmpty());
    }

    @Test (expected = IllegalStateException.class)
    public void emptyExecuteTest() throws Exception{
        ReportCommandLine.execute(new String[0], null);
    }

    @Test
    public void zipFolderTest() throws IOException
    {
        File f = new File(TARGET);
        File emptyFolder = new File(TARGET + "/emptyfolder");
        File notEmptyFolder = new File(TARGET + "/notemptyfolder");
        File file = new File(TARGET + "/notemptyfolder/file.txt");

        f.mkdir();
        emptyFolder.mkdir();
        notEmptyFolder.mkdir();
        file.createNewFile();

        ZipFolder.pack(TARGET,TARGET+".zip");
        File zip = new File(TARGET+".zip");
        assertTrue(zip.exists());
    }
}
