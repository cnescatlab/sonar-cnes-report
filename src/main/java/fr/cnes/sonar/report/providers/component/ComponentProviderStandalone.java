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

package fr.cnes.sonar.report.providers.component;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Component;
import fr.cnes.sonar.report.model.Components;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides component items in standalone mode
 */
public class ComponentProviderStandalone extends AbstractComponentProvider implements ComponentProvider {

    /**
     *  Name of the request for getting componentsList
     */
    private static final String GET_COMPONENTS_REQUEST = "GET_COMPONENTS_REQUEST";
    
    /**
     * Constructor.
     *
     * @param server  SonarQube server.
     * @param token   String representing the user token.
     * @param project The id of the component to report.
     * @param project The branch of the component to report.
     */
    public ComponentProviderStandalone(final String server, final String token, final String project,
            final String branch) {
        super(server, token, project, branch);
    }

    @Override
    public Components getComponents() throws BadSonarQubeRequestException, SonarQubeException {
        int page = 1;
        JsonObject jo;
        ArrayList<Map<String,String>> componentsList = new ArrayList<>();
        Set<String> excludeMetricSet = new HashSet<>(Arrays.asList(StringManager.getProperty(EXCLUDED_COMPONENTS).split(",")));

        // For each page, we get the components
        boolean goOn = true;
        while(goOn){
            // Send request to server
            jo = request(String.format(getRequest(GET_COMPONENTS_REQUEST),
                    getServer(), getProjectKey(), page, getRequest(MAX_PER_PAGE_SONARQUBE), getBranch()));

            // Get components from response
            final Component[] tmp = getGson().fromJson(jo.get(COMPONENTS), Component[].class);
            for (Component c:tmp) {
                Map<String,String> map = c.toMap();
                componentsList.add(map);
            }

            // Check if we reach the end
            final int number = jo.getAsJsonObject(PAGING).get(TOTAL).getAsInt();
            goOn =  page * Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)) < number;
            page++;
        }

        Components components = new Components();
        components.setComponentsList(componentsList);
        components.setExcludeMetricSet(excludeMetricSet);
        return components;
    }
}
