package fr.cnes.sonar.report;

import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XlsXExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.params.ParamsFactory;

import java.util.logging.Logger;

/**
 * Main entry point
 * @author begarco
 */
public class ReportCommandLine {

    private static final Logger LOGGER = Logger.getLogger(ReportCommandLine.class.getName());

    public static void main(String[] args) {
        try {
            Params params = new ParamsFactory().create(args);

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
        } catch (Exception e) {
            e.printStackTrace();
            help();
        }
    }

    private static void help() {
        LOGGER.info("Bienvenue dans Sonar Report CNES\n" +
                "Voici l'aide pour exécuter correctement cette commande :\n" +
                "  > --sonar.url [mandatory]\n" +
                "  > --sonar.project.id [mandatory]\n" +
                "  > --sonar.project.quality.profile [mandatory]\n" +
                "  > --sonar.project.quality.gate [mandatory]\n" +
                "  > --project.name\n" +
                "  > --report.author\n" +
                "  > --report.date\n" +
                "  > --report.path\n" +
                "  > --report.template\n" +
                "Exemple :\n" +
                "java -jar sonar-report-cnes.jar --sonar.url http://sonarqube:9000 --sonar.project.id genius-sonar");
    }

}
