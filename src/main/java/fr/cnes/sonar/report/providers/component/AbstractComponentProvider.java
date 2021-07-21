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

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Component;
import fr.cnes.sonar.report.model.Components;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.measures.ComponentTreeRequest;
import org.sonarqube.ws.Measures.ComponentTreeWsResponse;

/**
 * Contains common code for component providers
 */
public abstract class AbstractComponentProvider extends AbstractDataProvider {
    
    /**
     *  Name of the request for getting componentsList
     */
    private static final String GET_COMPONENTS_REQUEST = "GET_COMPONENTS_REQUEST";
    /**
     * Field to search in json to get components
     */
    private static final String COMPONENTS = "components";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractComponentProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractComponentProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Generic getter for componentsList, retrieve all component from a project and their metrics
     * @param isCalledInStandalone True if the method is called in standalone mode
     * @return componentsList List of componentsList
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected Components getComponentsAbstract(final boolean isCalledInStandalone) throws BadSonarQubeRequestException, SonarQubeException {
        int page = 1;
        JsonObject jo;
        ArrayList<Map<String,String>> componentsList = new ArrayList<>();

        // For each page, we get the components
        boolean goOn = true;
        while(goOn){
            // Send request to server
            if (isCalledInStandalone) {
                jo = request(String.format(getRequest(GET_COMPONENTS_REQUEST),
                        getServer(), getProjectKey(), page, getRequest(MAX_PER_PAGE_SONARQUBE), getBranch()));
            } else {
                final List<String> metricKeys = new ArrayList<>(Arrays.asList(
                    "ncloc", "comment_lines_density", "coverage", "complexity", "cognitive_complexity", "duplicated_lines_density"));
                final String p = String.valueOf(page);
                final String ps = getRequest(MAX_PER_PAGE_SONARQUBE);
                final ComponentTreeRequest componentTreeRequest = new ComponentTreeRequest()
                                                                        .setComponent(getProjectKey())
                                                                        .setMetricKeys(metricKeys)
                                                                        .setP(p)
                                                                        .setPs(ps)
                                                                        .setBranch(getBranch());
                final ComponentTreeWsResponse componentTreeWsResponse = getWsClient().measures().componentTree(componentTreeRequest);
                jo = responseToJsonObject(componentTreeWsResponse);
            }

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
        return components;
    }
}