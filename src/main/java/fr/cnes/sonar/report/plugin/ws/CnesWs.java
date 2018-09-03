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
package fr.cnes.sonar.report.plugin.ws;

import fr.cnes.sonar.report.plugin.tasks.ReportTask;
import org.sonar.api.server.ws.WebService;

/**
 * Expose CNES plugin api
 * @author lequal
 */
public class CnesWs implements WebService {

    /**
     * Define the new web service
     * Define each controller and action
     * @param context Context of the WebService
     */
    @Override
    public void define(final Context context) {
        // create the new controller for the cnes web service
        final NewController controller = context.createController("api/cnesreport");
        // set minimal sonarqube version required
        controller.setSince("6.3.1");
        // set description of the controller
        controller.setDescription("Export information about an analysis like issues, measures and quality gate.");

        // create the action for URL /api/cnes/report
        reportAction(controller);

        // important to apply changes
        controller.done();
    }

    /**
     * Add the action corresponding to the report generation
     * @param controller controller to which add the action
     */
    private void reportAction(final NewController controller) {
        final NewAction report = controller.createAction("report");
        report.setDescription("Generate the report of an analysis.");
        report.setSince("6.3.1");
        report.setResponseExample(getClass().getResource("/report-example.json"));
        report.setHandler(new ReportTask());
        // add the parameters of the controller
        // key parameter
        createParam(report, "key", "The key of the project to report.", true);
        // author's name parameter
        createParam(report, "author", "The name of this report's author.", true);
    }

    /**
     * Create a new parameter on a given action.
     * @param action Action to enhance.
     * @param key Key of the parameter.
     * @param desc Description of the parameter.
     * @param required True if the parameter is required.
     */
    private void createParam(NewAction action, final String key, final String desc, final boolean required) {
        NewParam newParam = action.createParam(key);
        newParam.setDescription(desc);
        newParam.setRequired(required);
    }

}