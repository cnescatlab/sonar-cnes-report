package fr.cnes.sonar.report;

import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XlsXExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.params.ParamsFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Main entry point
 * @author begarco
 */
public class ReportCommandLine {

    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(ReportCommandLine.class.getName());

    /**
     * Main method
     * Entry point of the program
     * @param args arguments that will be preprocessed
     */
    public static void main(String[] args) {
        // main catch all exceptions
        try {
            // preparing args
            Params params = new ParamsFactory().create(args);

            // Files exporters : export the data in the correct file type
            DocXExporter docXExporter = new DocXExporter();
            XmlExporter profileExporter = new XmlExporter();
            JsonExporter gateExporter = new JsonExporter();
            XlsXExporter issuesExporter = new XlsXExporter();

            // Producing the report
            Report superReport = new ReportFactory(params).create();

            // Export all
            // export each linked quality profile
            for(QualityProfile qp : superReport.getQualityProfiles()) {
                profileExporter.export(qp.getConf(), params, qp.getKey());
            }
            // export the quality gate
            gateExporter.export(superReport.getQualityGate().getConf(),params,superReport.getQualityGate().getName());
            String docXFilename = formatFilename("REPORT_FILENAME", superReport.getProjectName());

            // export the full docx report
            docXExporter.export(superReport, params, docXFilename);
            // construct the docx file name by replacing date and name
            String xlsXFilename = formatFilename("ISSUES_FILENAME", superReport.getProjectName());
            // export the xlsx issues' list
            issuesExporter.export(superReport, params, xlsXFilename);
        } catch (Exception e) { // on each exception
            // it logs all the stack trace
            for (StackTraceElement ste: e.getStackTrace()) {
                LOGGER.severe(ste.toString());
            }
            // prints the help
            help();
        }
    }

    /**
     * Format a given filename pattern
     * Add the date and the project's name
     * @param propertyName Name of pattern's property
     * @param projectName Name of the current project
     * @return a formatted filename
     */
    public static String formatFilename(String propertyName, String projectName) {
        // construct the filename by replacing date and name
        return ParamsFactory.getProperty(propertyName)
                .replaceAll("DATE", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .replaceAll("NAME", projectName);
    }

    /**
     * Provide help on bad command line
     */
    private static void help() {
        // only log the help
        LOGGER.info("Bienvenue dans Sonar Report CNES\n" +
                "Voici l'aide pour exécuter correctement cette commande :\n" +
                "  > --sonar.url [mandatory]\n" +
                "  > --sonar.project.id [mandatory]\n" +
                "  > --sonar.project.quality.profile\n" +
                "  > --sonar.project.quality.gate\n" +
                "  > --project.name\n" +
                "  > --report.author\n" +
                "  > --report.date\n" +
                "  > --report.path\n" +
                "  > --report.template\n" +
                "Exemple :\n" +
                "java -jar sonar-report-cnes.jar --sonar.url http://sonarqube:9000 --sonar.project.id genius-sonar");
    }

}
