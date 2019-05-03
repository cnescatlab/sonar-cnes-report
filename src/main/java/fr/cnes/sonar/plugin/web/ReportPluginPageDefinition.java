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

package fr.cnes.sonar.plugin.web;

import fr.cnes.sonar.plugin.tools.PluginStringManager;
import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.PageDefinition;

public class ReportPluginPageDefinition implements PageDefinition {

    /**
     * Define the web view for the plugin. Called by Sonarqube.
     * @param context
     */
    @Override
    public void define(Context context) {
        Page.Builder page = Page.builder(PluginStringManager.getProperty("homepage.url"));
        page.setName(PluginStringManager.getProperty("homepage.name"));
        page.setScope(Page.Scope.GLOBAL);
        context.addPage(page.build());
    }
}
