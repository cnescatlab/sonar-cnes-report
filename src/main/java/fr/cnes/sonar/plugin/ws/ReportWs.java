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

package fr.cnes.sonar.plugin.ws;

import fr.cnes.sonar.plugin.tools.PluginStringManager;
import org.sonar.api.config.Configuration;
import org.sonar.api.server.ws.WebService;

public class ReportWs implements WebService {

    // Sonarqube configuration
    private final Configuration config;

    /**
     * public constructor, called by sonarqube
     * @param config
     */
    public ReportWs(Configuration config){
        this.config = config;
    }

    /**
     * Define plugin, called at sonarqube startup
     * @param context
     */
    @Override
    public void define(Context context) {
        final NewController controller = context.createController(PluginStringManager.getProperty("api.url"));
        controller.setSince(PluginStringManager.getProperty("plugin.since"));
        controller.setDescription(PluginStringManager.getProperty("api.description"));
        reportAction(controller);
        controller.done();
    }

    /**
     * Define action executed when we called the webservice
     * @param controller
     */
    private void reportAction(final WebService.NewController controller){
        // Create API entry point
        final WebService.NewAction report = controller.createAction(PluginStringManager.getProperty("api.report.actionKey"));
        report.setDescription(PluginStringManager.getProperty("api.description"));
        report.setSince(PluginStringManager.getProperty("plugin.since"));

        // Bind webservice to export task
        report.setHandler(new ExportTask(config));

        // Adding key argument
        WebService.NewParam keyParam = report.createParam(PluginStringManager.getProperty("api.report.args.key"));
        keyParam.setDescription(PluginStringManager.getProperty("api.report.args.description.key"));
        keyParam.setRequired(true);

        // Adding author argument
        WebService.NewParam authorParam = report.createParam(PluginStringManager.getProperty("api.report.args.author"));
        authorParam.setDescription(PluginStringManager.getProperty("api.report.args.description.author"));
        authorParam.setRequired(true);

        // Adding token argument
        WebService.NewParam tokenParam = report.createParam(PluginStringManager.getProperty("api.report.args.token"));
        tokenParam.setDescription(PluginStringManager.getProperty("api.report.args.description.token"));
        tokenParam.setRequired(true);

        // Adding branch argument
        WebService.NewParam branchParam = report.createParam(PluginStringManager.getProperty("api.report.args.branch"));
        branchParam.setDescription(PluginStringManager.getProperty("api.report.args.description.branch"));
        branchParam.setRequired(false);
    }
}

