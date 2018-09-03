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
package fr.cnes.sonar.report.plugin.web;

import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.Page.Scope;
import org.sonar.api.web.page.PageDefinition;

/**
 * Define web pages of the plugin
 * @author lequal
 */
public class CnesPluginPageDefinition implements PageDefinition {

    /**
     * Define concretely the pages to add through the plugin
     * @param context Execution context of the plugin
     */
    @Override
    public void define(final Context context) {

        // create a page
        Page.Builder page = Page.builder("cnesreport/report");
        // set its name
        page.setName("CNES Report");
        // set its scope (where it is displayed)
        page.setScope(Scope.GLOBAL);
        // add a new page for reporting
        context.addPage(page.build());

    }
}
