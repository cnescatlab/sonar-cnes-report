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

package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.*;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.utils.ReportConfiguration;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public class ReportFactory {

    /** Property for the word report filename. */
    private static final String REPORT_FILENAME = "report.output";
    /** Property for the CSV report filename. */
    private static final String CSV_FILENAME = "csv.output";
    /** Property for the CSV report filename. */
    private static final String MD_FILENAME = "markdown.output";
    /** Property for the excel report filename. */
    private static final String ISSUES_FILENAME = "issues.output";
    /** Pattern for the name of the directory containing configuration files. */
    private static final String CONF_FOLDER_PATTERN = "%s/conf";
    /** Error message returned when the program cannot create a folder because it already exists. */
    private static final String CNES_MKDIR_ERROR = "Impossible to create the following directory: %s";
    /** Placeholder for the base directory of reporting. */
    private static final String BASEDIR = "BASEDIR";
    /** Placeholder for the date of reporting. */
    private static final String DATE = "DATE";
    /** Placeholder for the name of the project. */
    private static final String NAME = "NAME";
    /** Logger of this class. */
    private static final Logger LOGGER = Logger.getLogger(ReportFactory.class.getName());

    /**
     * Private constructor.
     */
    private ReportFactory() {}

    /**
     * Generate report from simple parameters.
     * @param configuration Contains all configuration details.
     * @param model Contains the report as a Java object model.
     * @throws IOException Caused by I/O.
     * @throws XmlException Caused by XML error.
     * @throws BadExportationDataTypeException Caused by export.
     * @throws OpenXML4JException Caused by Apache library.
     */
    public static void report(final ReportConfiguration configuration, final Report model)
            throws IOException, XmlException, BadExportationDataTypeException, OpenXML4JException {

        // Files exporters : export the resources in the correct file type
        final DocXExporter docXExporter = new DocXExporter();
        final XmlExporter profileExporter = new XmlExporter();
        final JsonExporter gateExporter = new JsonExporter();
        final XlsXExporter issuesExporter = new XlsXExporter();
        final CSVExporter csvExporter = new CSVExporter();
        final MarkdownExporter markdownExporter =  new MarkdownExporter();


        // Export analysis configuration if requested.
        if(configuration.isEnableConf()) {
            createConfigurationFiles(configuration, model, profileExporter, gateExporter);
        }

        // Export issues and metrics in report if requested.
        if(configuration.isEnableReport()) {
            // prepare docx report's filename
            final String docXFilename = formatFilename(REPORT_FILENAME, configuration.getOutput(), model.getProjectName());
            // export the full docx report
            docXExporter.export(model, docXFilename, configuration.getTemplateReport());
        }

        // Export issues in spreadsheet if requested.
        if(configuration.isEnableSpreadsheet()) {
            // construct the xlsx filename by replacing date and name
            final String xlsXFilename = formatFilename(ISSUES_FILENAME, configuration.getOutput(), model.getProjectName());
            // export the xlsx issues' list
            issuesExporter.export(model, xlsXFilename, configuration.getTemplateSpreadsheet());
        }

        // Export in markdown if requested
        if (configuration.isEnableMarkdown()) {
            final String MDFilename = formatFilename(MD_FILENAME, configuration.getOutput(), model.getProjectName());
            markdownExporter.export(model, MDFilename, configuration.getTemplateMarkdown());
        }

        // Export issues in report if requested
        if(configuration.isEnableCSV()) {
            final String CSVFilename = formatFilename(CSV_FILENAME, configuration.getOutput(), model.getProjectName());
            csvExporter.export(model, CSVFilename, model.getProjectName());
        }
    }

    /**
     * Generate files containing analysis configuration.
     * @param configuration Contains all configuration details.
     * @param model Contains the report as a Java object model.
     * @param profileExporter Exporter for quality profiles.
     * @param gateExporter Exporter for quality gates.
     * @throws IOException Caused by I/O.
     * @throws XmlException Caused by XML error.
     * @throws BadExportationDataTypeException Caused by export.
     * @throws OpenXML4JException Caused by Apache library.
     */
    private static void createConfigurationFiles(final ReportConfiguration configuration, final Report model,
                                                 final XmlExporter profileExporter, final JsonExporter gateExporter)
            throws XmlException, BadExportationDataTypeException, OpenXML4JException, IOException {

        // full path to the configuration folder
        final String confDirectory = String.format(CONF_FOLDER_PATTERN, configuration.getOutput());

        // create the configuration folder
        final File configFolder = new File(confDirectory);
        final boolean success = configFolder.mkdirs();
        if (!success && !configFolder.exists()) {
            // Directory creation failed
            final String message = String.format(CNES_MKDIR_ERROR, confDirectory);
            LOGGER.warning(message);
        }

        // Export all
        // export each linked quality profile
        exportAllQualityProfiles(model, profileExporter, confDirectory);

        // quality gate information
        final String qualityGateName = model.getQualityGate().getName();
        final String qualityGateConf = model.getQualityGate().getConf();
        // export the quality gate
        gateExporter.export(qualityGateConf, confDirectory, qualityGateName);
    }

    /**
     * Export all quality profiles related to a given report as xml file depending on exporter.
     * @param report Modeling data containing data to export.
     * @param exporter Class given the way to export previous data.
     * @param dir Directory for output.
     * @throws XmlException Thrown on xml error.
     * @throws BadExportationDataTypeException Thrown if the data does not correspond to exporter.
     * @throws OpenXML4JException Thrown on OpenXML error.
     * @throws IOException Thrown on files error.
     */
    private static void exportAllQualityProfiles(final Report report, final IExporter exporter, final String dir)
            throws XmlException, BadExportationDataTypeException, OpenXML4JException, IOException {
        for(ProfileMetaData metaData : report.getProject().getQualityProfiles()) {
            final Iterator<QualityProfile> iterator =
                    report.getQualityProfiles().iterator();
            boolean goOn = true;
            while(iterator.hasNext() && goOn) {
                final QualityProfile qp = iterator.next();
                if(qp.getKey().equals(metaData.getKey())) {
                    exporter.export(qp.getConf(), dir, qp.getKey());
                    goOn = false;
                }
            }
        }
    }

    /**
     * Format a given filename pattern.
     * Add the date and the project's name
     * @param propertyName Name of pattern's property
     * @param projectName Name of the current project
     * @return a formatted filename
     */
    public static String formatFilename(final String propertyName, final String baseDir, final String projectName) {
        // construct the filename by replacing date and name
        return StringManager.getProperty(propertyName)
                .replaceFirst(BASEDIR, Matcher.quoteReplacement(baseDir))
                .replaceAll(DATE, new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()))
                .replaceAll(NAME, projectName);
    }

}
