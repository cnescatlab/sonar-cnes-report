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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.qualitygates.ListRequest;
import org.sonarqube.ws.client.qualitygates.ShowRequest;
import org.sonarqube.ws.client.qualitygates.ProjectStatusRequest;
import org.sonarqube.ws.client.navigation.ComponentRequest;
import org.sonarqube.ws.Qualitygates.ListWsResponse;
import org.sonarqube.ws.Qualitygates.ShowWsResponse;
import org.sonarqube.ws.Qualitygates.ProjectStatusResponse;
import org.sonarqube.ws.Measures.ComponentWsResponse;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

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
    public List<QualityGate> getQualityGates() {
        // result list
        final List<QualityGate> res = new ArrayList<>();

        // Get all quality gates
        final ListRequest listRequest = new ListRequest();
        // perform the request to the server
        final ListWsResponse listWsResponse = getWsClient().qualitygates().list(listRequest);
        // transform response to JsonObject
        JsonObject jo = responseToJsonObject(listWsResponse);

        // Get quality gates criteria
        final String defaultQG = (getGson().fromJson(jo.get(DEFAULT), String.class));
        final QualityGate[] tmp = (getGson().fromJson(jo.get(QUALITYGATES), QualityGate[].class));
        // for each quality gate
        for (QualityGate i : tmp) {
            // request the criteria
            final ShowRequest showRequest = new ShowRequest().setName(i.getName());
            // perform previous request
            final ShowWsResponse showWsResponse = getWsClient().qualitygates().show(showRequest);
            // transform response to JsonObject
            jo = responseToJsonObject(showWsResponse);

            // put it in configuration field
            i.setConf(jo.toString());
            
            // check if it is the default quality gate
            i.setDefault(i.getId().equals(defaultQG));

            // add the quality gate to the result list
            res.add(i);
        }

        return res;
    }

    @Override
    public QualityGate getProjectQualityGate() throws UnknownQualityGateException {
        // get all the quality gates
        final List<QualityGate> qualityGates = getQualityGates();
        // get the project
        final ComponentRequest componentRequest = new ComponentRequest()
                                                        .setComponent(getProjectKey())
                                                        .setBranch(getBranch());
        QualityGate res = null;

        // perform previous request
        final String componentResponse = getWsClient().navigation().component(componentRequest);
        
        // transform response to JsonObject
        final JsonObject jsonObject = getGson().fromJson(componentResponse, JsonObject.class);

        // search for the good quality gate
        final Iterator<QualityGate> iterator = qualityGates.iterator();
        
        QualityGate tmp;
        boolean find = false;
        final String key = jsonObject.getAsJsonObject(QUALITY_GATE).get(KEY).getAsString();

        while (iterator.hasNext() && !find) {
            tmp = iterator.next();
            if (tmp.getId().equals(key)) {
                res = tmp;
                find = true;
            }
        }

        // check if it was found
        if (!find) {
            throw new UnknownQualityGateException(key);
        }       

        return res;
    }

    @Override
    public Map<String, String> getQualityGateStatus() {
        // request to get the quality gate status
        final ProjectStatusRequest projectStatusRequest = new ProjectStatusRequest()
                                                                .setBranch(getBranch())
                                                                .setProjectKey(getProjectKey());
        final ProjectStatusResponse projectStatusResponse = getWsClient().qualitygates().projectStatus(projectStatusRequest);
        final JsonObject projectStatusResult = responseToJsonObject(projectStatusResponse);
        // map containing the result
        Map<String, String> res = new LinkedHashMap<>();
        // retrieve the content of the object
        JsonObject projectStatusObject = projectStatusResult.get(PROJECT_STATUS).getAsJsonObject();
        // retrieve the array of conditions
        JsonArray conditions = projectStatusObject.get(CONDITIONS).getAsJsonArray();
        // add a couple metric name / status to the map for each condition
        for (JsonElement condition : conditions) {
            JsonObject conditionObject = condition.getAsJsonObject();
            String status = conditionObject.get(STATUS).getAsString();
            String metricKey = conditionObject.get(METRIC_KEY).getAsString();
            final List<String> additionalFields = new ArrayList<>(Arrays.asList("metrics"));
            final List<String> metricKeys = new ArrayList<>(Arrays.asList(metricKey));
            final org.sonarqube.ws.client.measures.ComponentRequest componentRequest =
                new org.sonarqube.ws.client.measures.ComponentRequest()
                                                        .setAdditionalFields(additionalFields)
                                                        .setBranch(getBranch())
                                                        .setComponent(getProjectKey())
                                                        .setMetricKeys(metricKeys);
            final ComponentWsResponse componentWsResponse = getWsClient().measures().component​(componentRequest);
            final JsonObject metricResult = responseToJsonObject(componentWsResponse);
            String name = metricResult.get(METRICS).getAsJsonArray().get(0).getAsJsonObject().get(NAME).getAsString();
            // add the detailed explanation on why the condition failed if it's the case
            if (status.equals(ERROR)) {
                String actualValue = conditionObject.get(ACTUAL_VALUE).getAsString();
                String errorThreshold = conditionObject.get(ERROR_THRESHOLD).getAsString();
                String comparator = conditionObject.get(COMPARATOR).getAsString();
                String type = metricResult.get(METRICS).getAsJsonArray().get(0).getAsJsonObject().get(TYPE).getAsString();
                status = status.concat(getErrorExplanation(actualValue, errorThreshold, comparator, type));
            }
            res.put(name, status);
        }
        return res;
    }
}