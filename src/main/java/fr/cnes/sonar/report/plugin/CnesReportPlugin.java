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
package fr.cnes.sonar.report.plugin;

import fr.cnes.sonar.report.plugin.web.CnesPluginPageDefinition;
import fr.cnes.sonar.report.plugin.ws.CnesWs;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 * @author lequal
 */
public class CnesReportPlugin implements Plugin {

    /**
     * Definition of the plugin:
     * add pages, web services, rules, sensor, etc.
     *
     * @param context Execution context of the plugin
     */
    @Override
    public void define(final Context context) {
        // report web service extension
        context.addExtension(CnesWs.class);

        // report web extensions
        context.addExtension(CnesPluginPageDefinition.class);

        // Add two properties to specify a path to custom templates
        createProperty(context, "sonar.cnes.reportTemplate", "Report's template", "Path to the template of the report.", "");
        createProperty(context, "sonar.cnes.issuesTemplate", "Issues' template", "Path to the template of the issues report.", "");
    }

    /**
     * Create a new property and add it to SonarQube context.
     * @param context Context in which to append the property.
     * @param key Unique id for the property.
     * @param name Name to display.
     * @param desc Description of the property.
     * @param value Default value for the property.
     */
    private void createProperty(final Context context, final String key,
                               final String name, final String desc, final String value) {
        // define the builder
        final PropertyDefinition.Builder builder = PropertyDefinition.builder(key);
        // set attributes
        builder.name(name);
        builder.description(desc);
        builder.defaultValue(value);
        // build the property
        final PropertyDefinition propertyDefinition = builder.build();
        // add it to SonarQube context
        context.addExtension(propertyDefinition);
    }
}
