package fr.cnes.sonar.report;

import fr.cnes.sonar.report.exceptions.*;
import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.input.ParamsFactory;
import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Report;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
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
     * Placeholder for the date of reporting
     */
    private static final String DATE = "DATE";
    /**
     * Placeholder for the name of the project
     */
    private static final String NAME = "NAME";
    /**
     * Pattern to format the date
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * Help message to display when a user misused this program
     */
    public static final String HELP_MESSAGE = "Welcome to Sonar Report CNES\n" +
            "Here are the list of parameters you can use:\n" +
            "  > --sonar.url [mandatory]\n" +
            "  > --sonar.project.id [mandatory]\n" +
            "  > --report.author\n" +
            "  > --report.date\n" +
            "  > --report.path\n" +
            "  > --report.conf [yes|no]\n" +
            "  > --report.locale [fr_FR|en_US]\n" +
            "  > --report.template\n" +
            "  > --issues.template\n" +
            "Example :\n" +
            "java -jar sonar-report-cnes.jar --sonar.url http://sonarqube:9000 --sonar.project.id genius-sonar";
    /**
     * Property for the word report filename
     */
    public static final String REPORT_FILENAME = "REPORT_FILENAME";
    /**
     * Property for the excel report filename
     */
    public static final String ISSUES_FILENAME = "ISSUES_FILENAME";
    /**
     * Pattern for the name of the directory containing configuration files
     */
    public static final String CONF_FOLDER_PATTERN = "%s/conf";
    /**
     * Name of the property to find the base of report location
     */
    private static final String REPORT_PATH = "report.path";
    /**
     * Error message returned when the program cannot create a folder because it already exists
     */
    public static final String CNES_MKDIR_ERROR = "[WARN] Impossible to create the following directory: %s";

    /**
     * Main method
     * See HELP_MESSAGE for more information about using this program
     * Entry point of the program
     * @param args arguments that will be preprocessed
     */
    public static void main(String[] args)  {
        // main catches all exceptions
        try {
            // preparing args
            Params params = new ParamsFactory().create(args);

            // Files exporters : export the resources in the correct file type
            DocXExporter docXExporter = new DocXExporter();
            XmlExporter profileExporter = new XmlExporter();
            JsonExporter gateExporter = new JsonExporter();
            XlsXExporter issuesExporter = new XlsXExporter();

            // full path to the configuration folder
            String confDirectory = String.format(CONF_FOLDER_PATTERN,params.get(REPORT_PATH));

            // create the configuration folder
            boolean success = (new File(confDirectory)).mkdirs();
            if (!success) {
                // Directory creation failed
                LOGGER.severe(String.format(CNES_MKDIR_ERROR, confDirectory));
            }

            // Producing the report
            Report superReport = new ReportFactory(params).create();

            // Export all
            // export each linked quality profile
            for(ProfileMetaData metaData : superReport.getProject().getQualityProfiles()) {
                Iterator<QualityProfile> iterator = superReport.getQualityProfiles().iterator();
                boolean goOn = true;
                while(iterator.hasNext() && goOn) {
                    QualityProfile qp = iterator.next();
                    if(qp.getKey().equals(metaData.getKey())) {
                        profileExporter.export(qp.getConf(), params, confDirectory, qp.getKey());
                        goOn = false;
                    }
                }
            }

            // export the quality gate
            gateExporter.export(superReport.getQualityGate().getConf(),params,confDirectory,superReport.getQualityGate().getName());

            // prepare docx report's filename
            String docXFilename = formatFilename(REPORT_FILENAME, superReport.getProjectName());
            // export the full docx report
            docXExporter.export(superReport, params, params.get(REPORT_PATH), docXFilename);

            // construct the xlsx filename by replacing date and name
            String xlsXFilename = formatFilename(ISSUES_FILENAME, superReport.getProjectName());
            // export the xlsx issues' list
            issuesExporter.export(superReport, params, params.get(REPORT_PATH), xlsXFilename);
        } catch (BadExportationDataTypeException | MalformedParameterException |
                BadSonarQubeRequestException | IOException | UnknownParameterException |
                MissingParameterException | UnknownQualityGateException | OpenXML4JException |
                XmlException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE,e.getMessage(), e);
            // prints the help
            LOGGER.info(HELP_MESSAGE);
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
        return StringManager.getProperty(propertyName)
                .replaceAll(DATE, new SimpleDateFormat(DATE_PATTERN).format(new Date()))
                .replaceAll(NAME, projectName);
    }

}
