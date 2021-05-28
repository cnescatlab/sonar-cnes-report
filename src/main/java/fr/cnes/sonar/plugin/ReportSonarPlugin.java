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

package fr.cnes.sonar.plugin;

import fr.cnes.sonar.plugin.web.ReportPluginPageDefinition;
import fr.cnes.sonar.plugin.ws.ReportWs;
import fr.cnes.sonar.plugin.settings.ReportSonarPluginProperties;
import org.sonar.api.Plugin;

public class ReportSonarPlugin implements Plugin {
    @Override
    public void define(Context context) {
        context.addExtensions(ReportSonarPluginProperties.getProperties());
        context.addExtension(ReportWs.class);
        context.addExtension(ReportPluginPageDefinition.class);
    }
}
