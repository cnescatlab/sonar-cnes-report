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
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Report;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * Exports the report in the console
 */
public class ConsoleExporter implements IExporter {

    /** Default logger. */
    private static final Logger LOGGER = Logger.getLogger(ConsoleExporter.class.getName());

    /**
     * Overridden export for console
     * @param data Data to export as report
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @return null: no file is generated
     * @throws BadExportationDataTypeException resources is not a Report
     */
    @Override
    public File export(Object data, String path, String filename)
            throws BadExportationDataTypeException {
        // check resources type
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // resources casting
        final Report report = (Report) data;

        // get issues
        final List<Issue> issues = report.getIssues().getIssuesList();
        String message = "key\tproject\tcomponent\ttype\tseverity\tmessage\tline\tstatus\t";
        LOGGER.info(message);

        // log all issues
        for(Issue issue : issues) {
            message = issue.toString();
            LOGGER.info(message);
        }

        // log number of issues
        message = String.format("Total number of violations : %s", issues.size());
        LOGGER.info(message);

        return null;
    }
}
