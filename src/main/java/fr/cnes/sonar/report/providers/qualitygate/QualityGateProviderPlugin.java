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

package fr.cnes.sonar.report.providers.qualitygate;

import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.navigation.ComponentRequest;
import org.sonarqube.ws.client.qualitygates.ListRequest;
import org.sonarqube.ws.client.qualitygates.ProjectStatusRequest;
import org.sonarqube.ws.client.qualitygates.ShowRequest;
import org.sonarqube.ws.Measures.ComponentWsResponse;
import org.sonarqube.ws.Qualitygates.ListWsResponse;
import org.sonarqube.ws.Qualitygates.ProjectStatusResponse;
import org.sonarqube.ws.Qualitygates.ShowWsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Provides quality gates in plugin mode
 */
public class QualityGateProviderPlugin extends AbstractQualityGateProvider implements QualityGateProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public QualityGateProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public List<QualityGate> getQualityGates() throws BadSonarQubeRequestException, SonarQubeException {
        return getQualityGatesAbstract();
    }

    @Override
    public QualityGate getProjectQualityGate() throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {
        return getProjectQualityGateAbstract();
    }

    @Override
    public Map<String, String> getQualityGateStatus() throws BadSonarQubeRequestException, SonarQubeException {
        return getQualityGateStatusAbstract();
    }

    @Override
    protected JsonObject getQualityGatesAsJsonObject() {
        // Get all quality gates
        final ListRequest listRequest = new ListRequest();
        // perform the request to the server
        final ListWsResponse listWsResponse = getWsClient().qualitygates().list(listRequest);
        // transform response to JsonObject
        return responseToJsonObject(listWsResponse);
    }

    @Override
    protected JsonObject getQualityGatesDetailsAsJsonObject(final QualityGate qualityGate) {
        // request the criteria
        final ShowRequest showRequest = new ShowRequest().setName(qualityGate.getName());
        // perform previous request
        final ShowWsResponse showWsResponse = getWsClient().qualitygates().show(showRequest);
        // transform response to JsonObject
        return responseToJsonObject(showWsResponse);
    }

    @Override
    protected JsonObject getProjectAsJsonObject() {
        // get the project
        final ComponentRequest componentRequest = new ComponentRequest()
                                                        .setComponent(getProjectKey())
                                                        .setBranch(getBranch());
        // perform previous request
        final String componentResponse = getWsClient().navigation().component(componentRequest);
        // transform response to JsonObject
        return getGson().fromJson(componentResponse, JsonObject.class);
    }

    @Override
    protected JsonObject getQualityGateStatusAsJsonObject() {
        final ProjectStatusRequest projectStatusRequest = new ProjectStatusRequest()
                                                                    .setBranch(getBranch())
                                                                    .setProjectKey(getProjectKey());
        final ProjectStatusResponse projectStatusResponse = getWsClient().qualitygates().projectStatus(projectStatusRequest);
        return responseToJsonObject(projectStatusResponse);
    }

    @Override
    protected JsonObject getMetricAsJsonObject(final String metricKey) {
        final List<String> additionalFields = new ArrayList<>(Arrays.asList(METRICS));
        final List<String> metricKeys = new ArrayList<>(Arrays.asList(metricKey));
        final org.sonarqube.ws.client.measures.ComponentRequest componentRequest =
                new org.sonarqube.ws.client.measures.ComponentRequest()
                                                        .setAdditionalFields(additionalFields)
                                                        .setBranch(getBranch())
                                                        .setComponent(getProjectKey())
                                                        .setMetricKeys(metricKeys);
        final ComponentWsResponse componentWsResponse = getWsClient().measures().component(componentRequest);
        return responseToJsonObject(componentWsResponse);
    }
}