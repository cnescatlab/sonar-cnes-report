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
package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.docx.DataAdapter;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MarkdownExporter implements IExporter {

    /**
     * Start index of the sub array in the headers array for the the second table
     */
    private static final int HEADER_START_INDEX = 2;
    /**
     * End index of the sub array in the headers array for the the second table
     */
    private static final int HEADER_END_INDEX = 5;
    /**
     * Placeholder for issues count table
     */
    private static final String ISSUES_COUNT_PLACEHOLDER = "$ISSUES_COUNT";
    /**
     * Placeholder for issues table
     */
    private static final String ISSUES_DETAILS_PLACEHOLDER = "$ISSUES_DETAILS";
    /**
     * Placeholder for the table containing counts of security hotspots by review priority and security category
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
     * Placeholder for the table containing values of each metric of the quality gate
     */
    private static final String QUALITY_GATE_STATUS_TABLE_PLACEHOLDER = "$QUALITY_GATE_STATUS";
    /**
     * Placeholder for the table containing the detailed technical debt
     */
    private static final String DETAILED_TECHNICAL_DEBT_TABLE_PLACEHOLDER = "$DETAILED_TECHNICAL_DEBT";
    /**
     * Markdown special chars
     */
    private static final String CELL_SEPARATOR = "|";
    private static final String ESCAPED_CELL_SEPARATOR = "&#124";
    private static final String HEADER_SEPARATOR = "---";
    private static final String LINE_BREAK = "\n";
    private static final String MD_LINE_BREAK = " <br /> ";
    /**
     * PNG extension
     */
    private static final String PNG_EXTENSION = ".png";

    @Override
    public File export(Object data, String path, String filename) throws IOException, BadExportationDataTypeException {
        if (!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        final File file = new File(filename);
        final Report report = (Report) data;

        try (
                InputStream fileInputStream = file.exists() ?
                        new FileInputStream(file) : getClass().getResourceAsStream("/template/code-analysis-template.md");
        ) {
            // Getting MD template
            StringWriter writer = new StringWriter();
            IOUtils.copy(fileInputStream, writer, StandardCharsets.UTF_8);

            // Getting replacement values, reusing placeholders from docx exporter
            Map<String, String> placeholdersMap = DataAdapter.loadPlaceholdersMap(report);

            // Replace placeholders by values
            String output = writer.toString();
            for(Map.Entry<String, String> entry: placeholdersMap.entrySet()){
                String value = entry.getValue();
                if (value.endsWith(PNG_EXTENSION)) {
                    value = FilenameUtils.removeExtension(value);
                }
                output = output.replace(entry.getKey(), value);
            }

            // Generate issue table
            final List<List<String>> issues = DataAdapter.getIssues(report);
            final String[] headerFields = {StringManager.string("header.name"),
                StringManager.string("header.description"),
                StringManager.string("header.type"),
                StringManager.string("header.severity"),
                StringManager.string("header.number")};
            final List<String> headerIssues = new ArrayList<>(Arrays.asList(headerFields));
            final String tableIssues = generateMDTable(headerIssues, issues);
            output = output.replace(ISSUES_DETAILS_PLACEHOLDER, tableIssues);

            // Generate issue count table
            final List<List<String>> types = DataAdapter.getTypes(report);
            final String tableTypes = generateMDTable(
                    headerIssues.subList(HEADER_START_INDEX, HEADER_END_INDEX),
                    types);
            output = output.replace(ISSUES_COUNT_PLACEHOLDER, tableTypes);

            // Generate security hotspots table
            final List<List<String>> securityHotspots = DataAdapter.getSecurityHotspots(report);
            final String[] securityHotspotsHeader = {StringManager.string("header.category"),
                StringManager.string("header.name"),
                StringManager.string("header.priority"),
                StringManager.string("header.severity"),
                StringManager.string("header.count")};
            final List<String> headerSecurityHotspots = new ArrayList<>(Arrays.asList(securityHotspotsHeader));
            final String tableSecurityHotspots = generateMDTable(headerSecurityHotspots, securityHotspots);
            output = output.replace(SECURITY_HOTSPOTS_DETAILS_PLACEHOLDER, tableSecurityHotspots);

            // Generate security hotspots count table
            final List<List<String>> securityHotspotsByCategoryAndPriority = DataAdapter.getSecurityHotspotsByCategoryAndPriority(report);
            final List<String> headerSecurityHotspotsCount = new ArrayList<>(Arrays.asList(DataAdapter.getSecurityHotspotPriorities()));
            headerSecurityHotspotsCount.add(0, StringManager.string("header.categorySlashPriority"));
            final String tableSecurityHotspotsCount = generateMDTable(headerSecurityHotspotsCount, securityHotspotsByCategoryAndPriority);
            output = output.replace(SECURITY_HOTSPOTS_COUNT_TABLE_PLACEHOLDER, tableSecurityHotspotsCount);

            // Generate volume table
            final String[] volumesHeader = {StringManager.string("header.language"),
                StringManager.string("header.number")};
            final List<String> headerVolumes = new ArrayList<>(Arrays.asList(volumesHeader));
            final List<List<String>> volumes = DataAdapter.getVolumes(report);
            final String volumeTable = generateMDTable(headerVolumes, volumes);
            output = output.replace(VOLUMES_TABLE_PLACEHOLDER, volumeTable);
            
            // Generate quality gate status table
            final String[] qualityGateStatusHeader = {StringManager.string("header.metric"),
                StringManager.string("header.value")};
            final List<String> headerQualityGateStatus = new ArrayList<>(Arrays.asList(qualityGateStatusHeader));
            final List<List<String>> qualityGateStatus = DataAdapter.getQualityGateStatus(report);
            final String qualityGateStatusTable = generateMDTable(headerQualityGateStatus, qualityGateStatus);
            output = output.replace(QUALITY_GATE_STATUS_TABLE_PLACEHOLDER, qualityGateStatusTable);

            // Generate detailed technical debt table
            final String[] detailedTechnicalDebtHeader = {StringManager.string("header.reliability"),
                StringManager.string("header.security"), StringManager.string("header.maintainability"),
                StringManager.string("header.total")};
            final List<String> headerDetailedTechnicalDebt = new ArrayList<>(Arrays.asList(detailedTechnicalDebtHeader));
            final List<List<String>> detailedTechnicalDebt = DataAdapter.getDetailedTechnicalDebt(report);
            final String detailedTechnicalDebtTable = generateMDTable(headerDetailedTechnicalDebt, detailedTechnicalDebt);
            output = output.replace(DETAILED_TECHNICAL_DEBT_TABLE_PLACEHOLDER, detailedTechnicalDebtTable);

            // Saving output
            try(FileWriter fileWriter = new FileWriter(path)){
              fileWriter.write(output);
            }
            return file;
        }


    }

    /**
     * This method will convert datas in Markdown table
     * @param headers list of headers
     * @param datas list of list of datas
     * @return table converted in markdown
     */
    public static String generateMDTable(List<String> headers, List<List<String>> datas){

        StringBuilder table = new StringBuilder();

        if (datas != null && !datas.isEmpty()) {
            List<String> headerSeparator = new ArrayList<>();

            // Create header line
            table.append(generateMDTableLine(headers));

            // In markdown we need to add line to separate header and values
            for(int i=0;i<headers.size();++i){
                headerSeparator.add(HEADER_SEPARATOR);
            }
            table.append(generateMDTableLine(headerSeparator));

            // Adding values
            for (List<String> row: datas) {
                table.append(generateMDTableLine(row));
            }
        }        

        return table.toString();

    }

    /**
     * Generate a table line in markdown
     * @param datas
     * @return line at format `data1|data2|data3`
     */
    private static String generateMDTableLine(List<String> datas){
        StringBuilder row = new StringBuilder();

        // Create line
        for(int i=0;i<datas.size();++i){
            row.append(datas
                    // Get a data
                    .get(i)
                    // Escape pipes
                    .replace(CELL_SEPARATOR, ESCAPED_CELL_SEPARATOR)
                    // Escape line breaks to avoid confusion between end of row and line break inside a row
                    .replace(LINE_BREAK, MD_LINE_BREAK));
            if(i<datas.size()-1) {
                row.append(CELL_SEPARATOR);
            } else {
                row.append(LINE_BREAK);
            }
        }

        return row.toString();
    }
}
