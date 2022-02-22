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

package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.exporters.data.DataAdapter;
import fr.cnes.sonar.report.exporters.data.IssuesAdapter;
import fr.cnes.sonar.report.exporters.data.PlaceHolders;
import fr.cnes.sonar.report.exporters.data.SecurityHotspotsAdapter;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exports the report in .docx format
 */
public class DocXExporter implements IExporter {

        /** Logger for DocXExporter. */
        private static final Logger LOGGER = Logger.getLogger(StringManager.class.getCanonicalName());
        /**
         * Placeholder for the table containing detailed issues
         */
        private static final String DETAILS_TABLE_PLACEHOLDER = "$ISSUES_DETAILS";
        /**
         * Placeholder for the table containing counts of issues by type and severity
         */
        private static final String COUNT_TABLE_PLACEHOLDER = "$ISSUES_COUNT";
        /**
         * Placeholder for the table containing detailed security hotspots
         */
        private static final String SECURITY_HOTSPOTS_DETAILS_PLACEHOLDER = "$SECURITY_HOTSPOTS_DETAILS";
        /**
         * Placeholder for the table containing counts of security hotspots by review
         * priority and security category
         */
        private static final String SECURITY_HOTSPOTS_COUNT_TABLE_PLACEHOLDER = "$SECURITY_HOTSPOTS_COUNT";
        /**
         * Placeholder for the table containing counts of issues by type and severity
         */
        private static final String VOLUME_TABLE_PLACEHOLDER = "$VOLUME";
        /**
         * Placeholder for the table containing values of each metric of the quality
         * gate
         */
        private static final String QUALITY_GATE_STATUS_TABLE_PLACEHOLDER = "$QUALITY_GATE_STATUS";
        /**
         * Placeholder for the table containing the detailed technical debt
         */
        private static final String DETAILED_TECHNICAL_DEBT_TABLE_PLACEHOLDER = "$DETAILED_TECHNICAL_DEBT";
        /**
         * Name of the property giving the path header's number
         */
        private static final String HEADER_NUMBER = "header.number";
        /**
         * Name of the property giving the default docx template
         */
        private static final String DEFAULT_TEMPLATE = "docx.template";
        /**
         * Property to get the list of issues types
         */
        private static final String SECURITY_HOTSPOTS_PRIORITIES = "securityhotspots.priorities";

        /**
         * Overridden export for docX
         * 
         * @param data     Data to export as Report
         * @param path     Path where to export the file
         * @param filename Name of the template file
         * @return Exported file.
         * @throws BadExportationDataTypeException Data has not the good type
         * @throws OpenXML4JException              ...
         * @throws IOException                     ...
         * @throws XmlException                    ...
         */
        @Override
        public File export(final Object data, final String path, final String filename)
                        throws BadExportationDataTypeException, OpenXML4JException, IOException, XmlException {
                // check resources type
                if (!(data instanceof Report)) {
                        throw new BadExportationDataTypeException();
                }
                // resources casting
                final Report report = (Report) data;

                // open excel file from the path given in the parameters
                final File file = new File(filename);

                // Check if template file exists
                if (!file.exists() && !filename.isEmpty()) {
                        LOGGER.log(Level.WARNING,
                                        () -> "Unable to find provided DOCX template file (using default one instead) : "
                                                        + file.getAbsolutePath());
                }

                try (
                        InputStream fileInputStream = file.exists() ? new FileInputStream(file)
                                        : getClass().getResourceAsStream(
                                                        StringManager.getProperty(DEFAULT_TEMPLATE));
                        OPCPackage opcPackage = OPCPackage.open(fileInputStream);
                        XWPFDocument document = new XWPFDocument(opcPackage)
                ) {

                        // Fill charts
                        DocXTools.fillCharts(document, report);

                        // Add issues
                        replaceIssuesPlaceholders(document, report);

                        // Add issues count by type and severity
                        replaceIssuesCountPlaceholders(document, report);

                        // Add security hotspots
                        replaceSecurityHotspotsPlaceholders(document, report);

                        // Add security hotspots by security category and review priority.
                        replaceSecurityHotspotsCountPlaceholders(document, report);

                        // Add volumes by language
                        replaceVolumesPlaceholders(document, report);

                        // Add quality gate status
                        replaceQualityGateStatusPlaceholders(document, report);

                        // Add detailed technical debt
                        replaceDetailedTechnicalDebtPlaceholders(document, report);

                        // replace simple placeholders (not tables/charts)
                        replaceSimplePlaceholders(document, report);

                        // Save the result by creating a new file in the directory given by report.path
                        // property
                        final FileOutputStream out = new FileOutputStream(path);
                        // close open resources
                        document.write(out);
                        out.close();
                }

                return new File(path);
        }

        /**
         * Replace simple placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceSimplePlaceholders(XWPFDocument document, Report report)
                        throws IOException, OpenXML4JException {
                // Map which contains all values to replace
                // the key is the placeholder and the value is the value to write over
                final Map<String, String> replacementValues = PlaceHolders.loadPlaceholdersMap(report);

                // replace all placeholder in the document (head, body, foot) with the map
                DocXTools.replacePlaceholder(document, replacementValues);
        }

        /**
         * Replace issues placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceIssuesPlaceholders(XWPFDocument document, Report report) {
                final List<List<String>> issues = IssuesAdapter.getIssues(report);
                final String[] issuesArrayFr = { StringManager.string("header.name"),
                                StringManager.string("header.description"),
                                StringManager.string("header.type"),
                                StringManager.string("header.severity"),
                                StringManager.string(HEADER_NUMBER) };
                final List<String> headerIssues = new ArrayList<>(Arrays.asList(issuesArrayFr));
                DocXTools.fillTable(document, headerIssues, issues, DETAILS_TABLE_PLACEHOLDER);
        }

        /**
         * Replace issues count placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceIssuesCountPlaceholders(XWPFDocument document, Report report) {
                final List<List<String>> types = IssuesAdapter.getTypes(report);
                final List<String> headerIssuesCount = IssuesAdapter.getReversedIssuesSeverities();
                headerIssuesCount.add(0, StringManager.string("header.typeSlashSeverity"));
                DocXTools.fillTable(document, headerIssuesCount, types, COUNT_TABLE_PLACEHOLDER);
        }

        /**
         * Replace security hotspots count placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceSecurityHotspotsPlaceholders(XWPFDocument document, Report report) {
                final List<List<String>> securityHotspots = SecurityHotspotsAdapter.getSecurityHotspots(report);
                final String[] securityHotspotsHeader = { StringManager.string("header.category"),
                                StringManager.string("header.name"),
                                StringManager.string("header.priority"),
                                StringManager.string("header.severity"),
                                StringManager.string("header.count") };
                final List<String> headerSecurityHotspots = new ArrayList<>(Arrays.asList(securityHotspotsHeader));
                DocXTools.fillTable(document, headerSecurityHotspots, securityHotspots,
                                SECURITY_HOTSPOTS_DETAILS_PLACEHOLDER);
        }

        /**
         * Replace security hotspots count placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceSecurityHotspotsCountPlaceholders(XWPFDocument document, Report report) {
                final List<List<String>> securityHotspotsByCategoryAndPriority = SecurityHotspotsAdapter
                                .getSecurityHotspotsByCategoryAndPriority(report);
                final List<String> headerSecurityHotspotsCount = new ArrayList<>(
                                Arrays.asList(StringManager.getProperty(SECURITY_HOTSPOTS_PRIORITIES).split(",")));
                headerSecurityHotspotsCount.add(0, StringManager.string("header.categorySlashPriority"));
                DocXTools.fillTable(document, headerSecurityHotspotsCount, securityHotspotsByCategoryAndPriority,
                                SECURITY_HOTSPOTS_COUNT_TABLE_PLACEHOLDER);
        }

        /**
         * Replace volumes placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceVolumesPlaceholders(XWPFDocument document, Report report) {
                final String[] volumesHeader = { StringManager.string("header.language"),
                                StringManager.string(HEADER_NUMBER) };
                final List<String> headerVolumes = new ArrayList<>(Arrays.asList(volumesHeader));
                final List<List<String>> volumes = DataAdapter.getVolumes(report);
                DocXTools.fillTable(document, headerVolumes, volumes, VOLUME_TABLE_PLACEHOLDER);
        }

        /**
         * Replace quality gate status placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceQualityGateStatusPlaceholders(XWPFDocument document, Report report) {
                final String[] qualityGateStatusHeader = { StringManager.string("header.metric"),
                                StringManager.string("header.value") };
                final List<String> headerQualityGateStatus = new ArrayList<>(Arrays.asList(qualityGateStatusHeader));
                final List<List<String>> qualityGateStatus = DataAdapter.getQualityGateStatus(report);
                DocXTools.fillTable(document, headerQualityGateStatus, qualityGateStatus,
                                QUALITY_GATE_STATUS_TABLE_PLACEHOLDER);
        }

        /**
         * Replace detailed technical debt placeholders with data
         * 
         * @param doc    The document containing placeholders
         * @param report The report containing data
         */
        private void replaceDetailedTechnicalDebtPlaceholders(XWPFDocument document, Report report) {
                final String[] detailedTechnicalDebtHeader = { StringManager.string("header.reliability"),
                                StringManager.string("header.security"), StringManager.string("header.maintainability"),
                                StringManager.string("header.total") };
                final List<String> headerDetailedTechnicalDebt = new ArrayList<>(
                                Arrays.asList(detailedTechnicalDebtHeader));
                final List<List<String>> detailedTechnicalDebt = DataAdapter.getDetailedTechnicalDebt(report);
                DocXTools.fillTable(document, headerDetailedTechnicalDebt, detailedTechnicalDebt,
                                DETAILED_TECHNICAL_DEBT_TABLE_PLACEHOLDER);
        }

}
