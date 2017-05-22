import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import org.junit.Test;

/**
 * Test the creation of a docx file from a template
 * @author garconb
 */
public class DocXExporterTest extends MasterTest {

    @Test
    public void variousTries() throws Exception {
        DocXExporter de = new DocXExporter();

        de.export(report, params, "analysis-report.docx");
    }

}
