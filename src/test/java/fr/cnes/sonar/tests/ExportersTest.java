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

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
import org.junit.Test;

/**
 * Test the creation of files from an abstract report
 * @author lequal
 */
public class ExportersTest extends CommonTest {

    /**
     * Path to the export folder
     */
    private static final String TARGET = "./target";

    /**
     * Assert that there are no exception in a normal use
     * of DocxExporter
     * @throws Exception ...
     */
    @Test
    public void docxExportTest() throws Exception {
        final DocXExporter de = new DocXExporter();

        de.export(report, TARGET+"/test.docx", params.get("report.template"));
    }

    /**
     * Assert that there are no exception in a normal use
     * of XlsxExporter
     * @throws Exception ...
     */
    @Test
    public void xlsxExportTest() throws Exception {
        final XlsXExporter xe = new XlsXExporter();

        xe.export(report, TARGET+"/test.xlsx", params.get("issues.template"));
    }

    /**
     * Assert that there are no exception in a normal use
     * of JsonExporter
     * @throws Exception ...
     */
    @Test
    public void jsonExportTest() throws Exception {
        final JsonExporter je = new JsonExporter();

        je.export("{field:'value'}", TARGET, "test.xml");
    }

    /**
     * Assert that there are no exception in a normal use
     * of XmlExporter
     * @throws Exception ...
     */
    @Test
    public void xmlExportTest() throws Exception {
        final XmlExporter xe = new XmlExporter();

        xe.export("<tag>value</tag>", TARGET, "test.json");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for DocxExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void docxExportBadDataTest() throws Exception {
        final DocXExporter de = new DocXExporter();

        de.export(1, TARGET+"/test.docx", params.get("report.template"));
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for XlsxExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void xlsxExportBadDataTest() throws Exception {
        final XlsXExporter xe = new XlsXExporter();

        xe.export(2, TARGET+"/test.xlsx", params.get("issues.template"));
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

}
