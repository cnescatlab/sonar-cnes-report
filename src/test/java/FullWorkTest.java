import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XlsXExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.model.Report;
import org.junit.Test;

/**
 * Test the creation of a docx file from a template
 * @author garconb
 */
public class FullWorkTest extends MasterTest {

    @Test
    public void completeProcessTest() throws Exception {
        // Files exporters
        DocXExporter docXExporter = new DocXExporter();
        XmlExporter profileExporter = new XmlExporter();
        JsonExporter gateExporter = new JsonExporter();
        XlsXExporter issuesExporter = new XlsXExporter();

        // Producing the report
        Report superReport = new ReportFactory(params).create();

        // Export all
        profileExporter.export(superReport.getQualityProfile().getConf(),params,superReport.getQualityProfile().getKey());
        gateExporter.export(superReport.getQualityGate().getConf(),params,superReport.getQualityGate().getName());
        docXExporter.export(superReport, params, "analysis-report.docx");
        issuesExporter.export(superReport, params, "issues-report.xlsx");
    }

}
