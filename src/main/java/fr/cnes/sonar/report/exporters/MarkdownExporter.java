package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.docx.DataAdapter;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MarkdownExporter implements IExporter {

    /**
     * Name of the columns in issues table
     */
    private static final String[] HEADER_FIELDS = {StringManager.string("header.name"),
            StringManager.string("header.description"),
            StringManager.string("header.type"),
            StringManager.string("header.severity"),
            StringManager.string("header.number")};
    /**
     * Name of the columns in volumes table
     */
    private static final String[] VOLUMES_HEADER = {StringManager.string("header.language"),
            StringManager.string("header.number")};

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
    private static String ISSUES_DETAILS_PLACEHOLDER = "$ISSUES_DETAILS";
    /**
     * Placeholder for volume table
     */
    private static final String VOLUMES_TABLE_PLACEHOLDER = "$VOLUME";
    /**
     * Markdown special chars
     */
    private static String CELL_SEPARATOR = "|";
    private static String ESCAPED_CELL_SEPARATOR = "&#124";
    private static String HEADER_SEPARATOR = "---";
    private static String LINE_BREAK = "\n";
    private static String MD_LINE_BREAK = " <br /> ";

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
            IOUtils.copy(fileInputStream, writer);

            // Getting replacement values, reusing placeholders from docx exporter
            Map<String, String> placeholdersMap = DataAdapter.loadPlaceholdersMap(report);

            // Replace placeholders by values
            String output = writer.toString();
            for(Map.Entry<String, String> entry: placeholdersMap.entrySet()){
                output = output.replace(entry.getKey(), entry.getValue());
            }

            // Generate issue table
            final List<List<String>> issues = DataAdapter.getIssues(report);
            final List<String> headerIssues = new ArrayList<>(Arrays.asList(HEADER_FIELDS));
            final String tableIssues = generateMDTable(headerIssues, issues);
            output = output.replace(ISSUES_DETAILS_PLACEHOLDER, tableIssues);

            // Generate issue count table
            final List<List<String>> types = DataAdapter.getTypes(report);
            final String tableTypes = generateMDTable(
                    headerIssues.subList(HEADER_START_INDEX, HEADER_END_INDEX),
                    types);
            output = output.replace(ISSUES_COUNT_PLACEHOLDER, tableTypes);


            // Generate volume table
            final List<String> volumesHeader = new ArrayList<>(Arrays.asList(VOLUMES_HEADER));
            final List<List<String>> volumes = DataAdapter.getVolumes(report);
            final String volumeTable = generateMDTable(volumesHeader, volumes);
            output = output.replace(VOLUMES_TABLE_PLACEHOLDER, volumeTable);


            // Saving output
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(output);
            fileWriter.close();
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