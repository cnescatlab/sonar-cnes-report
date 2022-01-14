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

package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.plugin.tools.FileTools;
import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.exporters.md.MarkdownExporter;
import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Test the creation of files from an abstract report
 */
public class ExportersTest extends CommonTest {

    /**
     * Path to the export folder
     */
    private static final String TARGET = "./target/test";
    @Before
    public void prepare() throws IOException {
        File f = new File(TARGET);
        File emptyFolder = new File(TARGET + "/emptyfolder");
        File notEmptyFolder = new File(TARGET + "/notemptyfolder");
        File file = new File(TARGET + "/notemptyfolder/file.txt");
        f.mkdir();
        emptyFolder.mkdir();
        notEmptyFolder.mkdir();
        file.createNewFile();

    }
    /**
     * Assert that there are no exception in a normal use
     * of consolexporter
     * @throws Exception ...
     */
    @Test
    public void consoleExportTest() throws Exception {
        final ConsoleExporter ce = new ConsoleExporter();

        Assert.assertNull(ce.export(report, null, null));
    }

    /**
     * Assert that there are no exception in a normal use
     * of DocxExporter
     * @throws Exception ...
     */
    @Test
    public void docxExportTest() throws Exception {
        final DocXExporter de = new DocXExporter();

        Assert.assertNotNull(de.export(report, TARGET+"/test.docx", ""));
    }

    /**
     * Assert that there are no exception in a normal use
     * of XlsxExporter
     * @throws Exception ...
     */
    @Test
    public void xlsxExportTest() throws Exception {
        final XlsXExporter xe = new XlsXExporter();

        Assert.assertNotNull(xe.export(report, TARGET+"/test.xlsx", ""));
    }

    /**
     * Assert that there are no exception in a normal use
     * of JsonExporter
     * @throws Exception ...
     */
    @Test
    public void jsonExportTest() throws Exception {
        final JsonExporter je = new JsonExporter();

        Assert.assertNotNull(je.export("{field:'value'}", TARGET, "test.xml"));
    }

    /**
     * Assert that there are no exception in a normal use
     * of XmlExporter
     * @throws Exception ...
     */
    @Test
    public void xmlExportTest() throws Exception {
        final XmlExporter xe = new XmlExporter();

        Assert.assertNotNull(xe.export("<tag>value</tag>", TARGET, "test.json"));
    }

    /**
     * Assert that there are no exception in a normal use
     * of MardownExporter
     * @throws Exception ...
     */
    @Test
    public void mdExportTest() throws Exception {
        final MarkdownExporter xe = new MarkdownExporter();

        Assert.assertNotNull(xe.export(report, TARGET+"/test.md", "test.md"));
    }
    /**
     * Assert that there are no exception in a normal use
     * of CSVExporter
     * @throws Exception ...
     */
    @Test
    public void csvExportTest() throws Exception {
        final CSVExporter xe = new CSVExporter();

        Assert.assertNotNull(xe.export(report, TARGET+"/test.csv", "test.csv"));
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for DocxExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void docxExportBadDataTest() throws Exception {
        final DocXExporter de = new DocXExporter();

        de.export(1, TARGET+"/test.docx", "");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for XlsxExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void xlsxExportBadDataTest() throws Exception {
        final XlsXExporter xe = new XlsXExporter();

        xe.export(2, TARGET+"/test.xlsx", "");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for JsonExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void jsonExportBadDataTest() throws Exception {
        final JsonExporter je = new JsonExporter();

        je.export(3, TARGET, "test.xml");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for XmlExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void xmlExportBadDataTest() throws Exception {
        final XmlExporter xe = new XmlExporter();

        xe.export(4, TARGET, "test.json");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for CSVExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void csvExportBadDataTest() throws Exception {
        final CSVExporter xe = new CSVExporter();

        xe.export(4,TARGET+"test.csv", "test.csv");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for MarkdownExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void mdExportBadDataTest() throws Exception {
        final MarkdownExporter xe = new MarkdownExporter();

        xe.export(4,TARGET+"test.md", "test.md");
    }

    /**
     * Assert that we can delete a folder
     *
     */
    @Test
    public void testDeleteFolder(){
        FileTools.deleteFolder(new File(TARGET));
        File f = new File(TARGET);
        Assert.assertFalse(f.exists());
    }
}