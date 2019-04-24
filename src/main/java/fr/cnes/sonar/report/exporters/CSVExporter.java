package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exporters.xlsx.XlsXTools;
import fr.cnes.sonar.report.model.Report;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVExporter implements IExporter {
    @Override
    public File export(Object data, String path, String filename) throws IOException{
        final String filePath = path;
        final Report report = (Report) data;

        // Opening file
        File file = new File(filePath);
        FileWriter outputfile = new FileWriter(file);
        CSVPrinter csvPrinter = new CSVPrinter(outputfile, CSVFormat.EXCEL.withDelimiter('\t'));

        List<Map> allIssues = report.getRawIssues();

        // Extracting headers
        List<String> headers = XlsXTools.extractHeader(allIssues);

        // Writing headers
        csvPrinter.printRecord(headers);

        // Writing lines
        List<String> line;
        for(Map<String, String> issue:allIssues){
            line = new ArrayList<>();
            for(String col: headers){
                line.add(issue.get(col));
            }
            csvPrinter.printRecord(line);
        }

        // closing file
        csvPrinter.close();
        return file;
    }
}
