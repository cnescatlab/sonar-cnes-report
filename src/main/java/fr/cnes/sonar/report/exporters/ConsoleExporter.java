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
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.input.ParamsFactory;

import java.util.List;
import java.util.logging.Logger;

/**
 * Exports the report in the console
 * @author lequal
 */
public class ConsoleExporter implements IExporter {

    /**
     * logger
     */
    private static final Logger LOGGER = Logger.getLogger(ParamsFactory.class.getCanonicalName());

    /**
     * Overridden export for console
     * @param data Data to export as report
     * @param params Program's parameters
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException resources is not a Report
     */
    @Override
    public void export(Object data, Params params, String path, String filename)
            throws BadExportationDataTypeException {
        // check resources type
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // resources casting
        final Report report = (Report) data;

        // get issues
        final List<Issue> issues = report.getIssues();
        LOGGER.info("key\t" + "project\t" + "component\t" + "type\t" + "severity\t" +
                "message\t" + "line\t" + "status\t" + "\t");
        // log all issues
        for(Issue issue : issues) {
            LOGGER.info(issue.toString());
        }
        // log number of issues
        LOGGER.info("Nombre total de violations : " + issues.size());
    }
}
