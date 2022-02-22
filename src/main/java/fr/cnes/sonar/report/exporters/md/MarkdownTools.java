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

import java.util.ArrayList;
import java.util.List;

public class MarkdownTools {

    /**
     * Markdown special chars
     */
    private static final String CELL_SEPARATOR = "|";
    private static final String ESCAPED_CELL_SEPARATOR = "&#124";
    private static final String HEADER_SEPARATOR = "---";
    private static final String LINE_BREAK = "\n";
    private static final String MD_LINE_BREAK = " <br /> ";

    private MarkdownTools() {}

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
