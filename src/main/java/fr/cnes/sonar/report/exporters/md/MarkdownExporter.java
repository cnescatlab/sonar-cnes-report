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
package fr.cnes.sonar.report.exporters.md;

import static fr.cnes.sonar.report.exporters.md.MarkdownTools.generateMDTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.exporters.data.DataAdapter;
import fr.cnes.sonar.report.exporters.data.IssuesAdapter;
import fr.cnes.sonar.report.exporters.data.PlaceHolders;
import fr.cnes.sonar.report.exporters.data.SecurityHotspotsAdapter;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.utils.StringManager;

public class MarkdownExporter implements IExporter {

    /**
     * Placeholder for issues count table
     */
    private static final String ISSUES_COUNT_PLACEHOLDER = "$ISSUES_COUNT";
    /**
     * Placeholder for issues table
     */
    private static final String ISSUES_DETAILS_PLACEHOLDER = "$ISSUES_DETAILS";
    /**
     * Placeholder for the table containing counts of security hotspots by review
     * priority and security category
     */
    private static final String SECURITY_HOTSPOTS_COUNT_TABLE_PLACEHOLDER = "$SECURITY_HOTSPOTS_COUNT";
    /**
     * Placeholder for the table containing detailed security hotspots
     */
    private static final String SECURITY_HOTSPOTS_DETAILS_PLACEHOLDER = "$SECURITY_HOTSPOTS_DETAILS";
    /**
     * Placeholder for volume table
     */
    private static final String VOLUMES_TABLE_PLACEHOLDER = "$VOLUME";
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
     * PNG extension
     */
    private static final String PNG_EXTENSION = ".png";
    /**
     * Name of the property giving the default markdown template
     */
    private static final String DEFAULT_TEMPLATE = "md.template";
    /**
     * Property to get the list of issues types
     */
    private static final String SECURITY_HOTSPOTS_PRIORITIES = "securityhotspots.priorities";

    @Override
    public File export(Object data, String path, String filename) throws IOException, BadExportationDataTypeException {
        if (!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        final File file = new File(filename);
        final Report report = (Report) data;

        try (
                InputStream fileInputStream = file.exists() ? new FileInputStream(file)
                        : getClass().getResourceAsStream(StringManager.getProperty(DEFAULT_TEMPLATE));) {
            // Getting MD template
            StringWriter writer = new StringWriter();
            IOUtils.copy(fileInputStream, writer, StandardCharsets.UTF_8);
            String output = writer.toString();

            // Replace simple placeholders by values
            output = replaceSimplePlaceHolders(output, report);

            // Generate issue table
            output = replaceIssueTable(output, report);

            // Generate issue count table
            output = replaceIssuesCountTable(output, report);

            // Generate security hotspots table
            output = replaceSecurityHotspotsTable(output, report);

            // Generate security hotspots count table
            output = replaceSecurityHotspotsCountTable(output, report);

            // Generate volume table
            output = replaceVolumeTable(output, report);

            // Generate quality gate status table
            output = replaceQualityGateStatusTable(output, report);

            // Generate detailed technical debt table
            output = replaceDetailedTechnicalDebtTable(output, report);

            // Saving output
            try (FileWriter fileWriter = new FileWriter(path)) {
                fileWriter.write(output);
            }
            return file;
        }
    }

    /**
     * Replace simple placeholders (not in a table/chart)
     * 
     * @param content The raw markdown content with placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of placeholders
     */
    private String replaceSimplePlaceHolders(String content, Report report) {
        // Getting replacement values, reusing placeholders from docx exporter
        Map<String, String> placeholdersMap = PlaceHolders.loadPlaceholdersMap(report);

        // Replace placeholders by values
        for (Map.Entry<String, String> entry : placeholdersMap.entrySet()) {
            String value = entry.getValue();
            if (value.endsWith(PNG_EXTENSION)) {
                value = FilenameUtils.removeExtension(value);
            }
            content = content.replace(entry.getKey(), value);
        }

        return content;
    }

    /**
     * Replace the issue table placeholders
     * 
     * @param content The raw markdown content with the issue table placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of the issue table
     *         placeholders
     */
    private String replaceIssueTable(String content, Report report) {
        final List<List<String>> issues = IssuesAdapter.getIssues(report);
        final String[] headerFields = { StringManager.string("header.name"),
                StringManager.string("header.description"),
                StringManager.string("header.type"),
                StringManager.string("header.severity"),
                StringManager.string("header.number") };
        final List<String> headerIssues = new ArrayList<>(Arrays.asList(headerFields));
        final String tableIssues = generateMDTable(headerIssues, issues);
        return content.replace(ISSUES_DETAILS_PLACEHOLDER, tableIssues);
    }

    /**
     * Replace the issues count table placeholders
     * 
     * @param report The report containing data
     * @return The raw markdown content with data instead of the issues count table
     *         placeholders
     */
    private String replaceIssuesCountTable(String content, Report report) {
        final List<List<String>> types = IssuesAdapter.getTypes(report);
        final List<String> headerIssuesCount = IssuesAdapter.getReversedIssuesSeverities();
        headerIssuesCount.add(0, StringManager.string("header.typeSlashSeverity"));
        final String tableTypes = generateMDTable(headerIssuesCount, types);
        return content.replace(ISSUES_COUNT_PLACEHOLDER, tableTypes);
    }

    /**
     * Replace the security hotspots table placeholders
     * 
     * @param content The raw markdown content with the security hotspots table
     *                placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of the security hotspots
     *         table
     *         placeholders
     */
    private String replaceSecurityHotspotsTable(String content, Report report) {
        final List<List<String>> securityHotspots = SecurityHotspotsAdapter.getSecurityHotspots(report);
        final String[] securityHotspotsHeader = { StringManager.string("header.category"),
                StringManager.string("header.name"),
                StringManager.string("header.priority"),
                StringManager.string("header.severity"),
                StringManager.string("header.count") };
        final List<String> headerSecurityHotspots = new ArrayList<>(Arrays.asList(securityHotspotsHeader));
        final String tableSecurityHotspots = generateMDTable(headerSecurityHotspots, securityHotspots);
        return content.replace(SECURITY_HOTSPOTS_DETAILS_PLACEHOLDER, tableSecurityHotspots);
    }

    /**
     * Replace the security hotspots count table placeholders
     * 
     * @param content The raw markdown content with the security hotspots count
     *                table
     *                placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of the security hotspots
     *         count table
     *         placeholders
     */
    private String replaceSecurityHotspotsCountTable(String content, Report report) {
        final List<List<String>> securityHotspotsByCategoryAndPriority = SecurityHotspotsAdapter
                .getSecurityHotspotsByCategoryAndPriority(report);
        final List<String> headerSecurityHotspotsCount = new ArrayList<>(
                Arrays.asList(StringManager.getProperty(SECURITY_HOTSPOTS_PRIORITIES).split(",")));
        headerSecurityHotspotsCount.add(0, StringManager.string("header.categorySlashPriority"));
        final String tableSecurityHotspotsCount = generateMDTable(headerSecurityHotspotsCount,
                securityHotspotsByCategoryAndPriority);
        return content.replace(SECURITY_HOTSPOTS_COUNT_TABLE_PLACEHOLDER, tableSecurityHotspotsCount);
    }

    /**
     * Replace the volume table placeholders
     * 
     * @param content The raw markdown content with the volume table
     *                placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of the volume table
     *         placeholders
     */
    private String replaceVolumeTable(String content, Report report) {
        final String[] volumesHeader = { StringManager.string("header.language"),
                StringManager.string("header.number") };
        final List<String> headerVolumes = new ArrayList<>(Arrays.asList(volumesHeader));
        final List<List<String>> volumes = DataAdapter.getVolumes(report);
        final String volumeTable = generateMDTable(headerVolumes, volumes);
        return content.replace(VOLUMES_TABLE_PLACEHOLDER, volumeTable);
    }

    /**
     * Replace the quality gate status table placeholders
     * 
     * @param content The raw markdown content with the quality gate status table
     *                placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of the quality gate status
     *         table
     *         placeholders
     */
    private String replaceQualityGateStatusTable(String content, Report report) {
        final String[] qualityGateStatusHeader = { StringManager.string("header.metric"),
                StringManager.string("header.value") };
        final List<String> headerQualityGateStatus = new ArrayList<>(Arrays.asList(qualityGateStatusHeader));
        final List<List<String>> qualityGateStatus = DataAdapter.getQualityGateStatus(report);
        final String qualityGateStatusTable = generateMDTable(headerQualityGateStatus, qualityGateStatus);
        return content.replace(QUALITY_GATE_STATUS_TABLE_PLACEHOLDER, qualityGateStatusTable);
    }

    /**
     * Replace the detailed technical debt table placeholders
     * 
     * @param content The raw markdown content with the detailed technical debt
     *                table
     *                placeholders
     * @param report  The report containing data
     * @return The raw markdown content with data instead of the detailed technical
     *         debt table
     *         placeholders
     */
    private String replaceDetailedTechnicalDebtTable(String content, Report report) {
        final String[] detailedTechnicalDebtHeader = { StringManager.string("header.reliability"),
                StringManager.string("header.security"), StringManager.string("header.maintainability"),
                StringManager.string("header.total") };
        final List<String> headerDetailedTechnicalDebt = new ArrayList<>(
                Arrays.asList(detailedTechnicalDebtHeader));
        final List<List<String>> detailedTechnicalDebt = DataAdapter.getDetailedTechnicalDebt(report);
        final String detailedTechnicalDebtTable = generateMDTable(headerDetailedTechnicalDebt,
                detailedTechnicalDebt);
        return content.replace(DETAILED_TECHNICAL_DEBT_TABLE_PLACEHOLDER, detailedTechnicalDebtTable);
    }
}
