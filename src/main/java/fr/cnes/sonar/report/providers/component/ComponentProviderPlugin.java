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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.measures.ComponentTreeRequest;
import org.sonarqube.ws.Measures.ComponentTreeWsResponse;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Components;

/**
 * Provides component items in plugin mode
 */
public class ComponentProviderPlugin extends AbstractComponentProvider implements ComponentProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public ComponentProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public Components getComponents() throws BadSonarQubeRequestException, SonarQubeException {
        return getComponentsAbstract();
    }

    @Override
    protected JsonObject getComponentsAsJsonObject(final int page) {
        final List<String> metricKeys = new ArrayList<>(Arrays.asList(getMetrics(SHEETS_METRICS).split(",")));
        final String pageIndex = String.valueOf(page);
        final String maxPerPage = getRequest(MAX_PER_PAGE_SONARQUBE);
        final ComponentTreeRequest componentTreeRequest = new ComponentTreeRequest()
                                                                .setComponent(getProjectKey())
                                                                .setMetricKeys(metricKeys)
                                                                .setP(pageIndex)
                                                                .setPs(maxPerPage)
                                                                .setBranch(getBranch());
        final ComponentTreeWsResponse componentTreeWsResponse = getWsClient().measures().componentTree(componentTreeRequest);
        return responseToJsonObject(componentTreeWsResponse);
    }
}