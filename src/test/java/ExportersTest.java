import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
import org.junit.Test;

/**
 * Test the creation of files from an abstract report
 * @author begarco
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
        DocXExporter de = new DocXExporter();

        de.export(report, params, TARGET, "test.docx");
    }

    /**
     * Assert that there are no exception in a normal use
     * of XlsxExporter
     * @throws Exception ...
     */
    @Test
    public void xlsxExportTest() throws Exception {
        XlsXExporter xe = new XlsXExporter();

        xe.export(report, params, TARGET, "test.xlsx");
    }

    /**
     * Assert that there are no exception in a normal use
     * of JsonExporter
     * @throws Exception ...
     */
    @Test
    public void jsonExportTest() throws Exception {
        JsonExporter je = new JsonExporter();

        je.export("{field:'value'}", params, TARGET, "test.xml");
    }

    /**
     * Assert that there are no exception in a normal use
     * of XmlExporter
     * @throws Exception ...
     */
    @Test
    public void xmlExportTest() throws Exception {
        XmlExporter xe = new XmlExporter();

        xe.export("<tag>value</tag>", params, TARGET, "test.json");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for DocxExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void docxExportBadDataTest() throws Exception {
        DocXExporter de = new DocXExporter();

        de.export(1, params, TARGET, "test.docx");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for XlsxExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void xlsxExportBadDataTest() throws Exception {
        XlsXExporter xe = new XlsXExporter();

        xe.export(2, params, TARGET, "test.xlsx");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for JsonExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void jsonExportBadDataTest() throws Exception {
        JsonExporter je = new JsonExporter();

        je.export(3, params, TARGET, "test.xml");
    }

    /**
     * Assert that there are bad data type exception in case
     * of using bad resource to export for XmlExporter
     * @throws Exception ...
     */
    @Test(expected = BadExportationDataTypeException.class)
    public void xmlExportBadDataTest() throws Exception {
        XmlExporter xe = new XmlExporter();

        xe.export(4, params, TARGET, "test.json");
    }

}
