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
    public File export(Object data, String path, String filename) throws IOException, BadExportationDataTypeException {

        if (!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }

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
