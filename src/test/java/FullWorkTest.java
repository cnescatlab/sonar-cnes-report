//import fr.cnes.sonar.report.exporters.JsonExporter;
//import fr.cnes.sonar.report.exporters.XmlExporter;
//import fr.cnes.sonar.report.exporters.docx.DocXExporter;
//import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
//import fr.cnes.sonar.report.factory.ReportFactory;
//import fr.cnes.sonar.report.model.QualityProfile;
//import fr.cnes.sonar.report.model.Report;
//import org.junit.Test;
//
//import static fr.cnes.sonar.report.ReportCommandLine.formatFilename;
//
///**
// * Test the creation of a docx file from a template
// * @author garconb
// */
//public class FullWorkTest extends MasterTest {
//
//    @Test
//    public void completeProcessTest() throws Exception {
//        // Files exporters : export the data in the correct file type
//        DocXExporter docXExporter = new DocXExporter();
//        XmlExporter profileExporter = new XmlExporter();
//        JsonExporter gateExporter = new JsonExporter();
//        XlsXExporter issuesExporter = new XlsXExporter();
//
//        // Producing the report
//        Report superReport = new ReportFactory(params).create();
//
//        // Export all
//        // export each linked quality profile
//        for(QualityProfile qp : superReport.getQualityProfiles()) {
//            profileExporter.export(qp.getConf(), params, qp.getKey());
//        }
//        // export the quality gate
//        gateExporter.export(superReport.getQualityGate().getConf(),params,superReport.getQualityGate().getName());
//        String docXFilename = formatFilename("REPORT_FILENAME", superReport.getProjectName());
//
//        // export the full docx report
//        docXExporter.export(superReport, params, docXFilename);
//        // construct the docx file name by replacing date and name
//        String xlsXFilename = formatFilename("ISSUES_FILENAME", superReport.getProjectName());
//        // export the xlsx issues' list
//        issuesExporter.export(superReport, params, xlsXFilename);
//    }
//
//}
