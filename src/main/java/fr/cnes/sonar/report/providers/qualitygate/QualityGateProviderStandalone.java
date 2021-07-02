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
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Provides quality gates in standalone mode
 */
public class QualityGateProviderStandalone extends AbstractQualityGateProvider implements QualityGateProvider {

    /**
     *  Name of the request for getting quality gates' details
     */
    private static final String GET_QUALITY_GATES_DETAILS_REQUEST =
            "GET_QUALITY_GATES_DETAILS_REQUEST";
    /**
     *  Name of the request for getting quality gates
     */
    private static final String GET_QUALITY_GATES_REQUEST = "GET_QUALITY_GATES_REQUEST";
    /**
     * Name of the request for getting the quality gate status of a project
     */
    private static final String GET_QUALITY_GATE_STATUS_REQUEST = 
            "GET_QUALITY_GATE_STATUS_REQUEST";
    /**
     * Name of the request for getting a specific metric
     */
    private static final String GET_METRIC_REQUEST = "GET_METRIC_REQUEST";
    

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    public QualityGateProviderStandalone(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    @Override
    public List<QualityGate> getQualityGates()
            throws BadSonarQubeRequestException, SonarQubeException {
        // result list
        final List<QualityGate> res = new ArrayList<>();

        // Get all quality gates
        String request = String.format(getRequest(GET_QUALITY_GATES_REQUEST), getServer());
        // perform the request to the server
        JsonObject jo = request(request);

        // Get quality gates criteria
        final String defaultQG = (getGson().fromJson(jo.get(DEFAULT), String.class));
        final QualityGate[] tmp = (getGson().fromJson(jo.get(QUALITYGATES), QualityGate[].class));
        // for each quality gate
        for (QualityGate i : tmp) {
            // request the criteria
            request = String.format(getRequest(GET_QUALITY_GATES_DETAILS_REQUEST),
                    getServer(), i.getName().replace(" ", "%20"));
            // perform previous request
            jo = request(request);

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
    public QualityGate getProjectQualityGate()
            throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {

        // get all the quality gates
        final List<QualityGate> qualityGates = getQualityGates();
        // get the project
        String request = String.format(getRequest(GET_PROJECT_REQUEST),
                getServer(), getProjectKey(), getBranch());
        // Final quality gate result.
        QualityGate res = null;

        // perform previous request
        final JsonObject jsonObject = request(request);

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
    public Map<String, String> getQualityGateStatus() throws BadSonarQubeRequestException, SonarQubeException {
        // request to get the quality gate status
        final JsonObject projectStatusResult = request(String.format(getRequest(GET_QUALITY_GATE_STATUS_REQUEST),
                getServer(), getBranch(), getProjectKey()));
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
            final JsonObject metricResult = request(String.format(getRequest(GET_METRIC_REQUEST),
                getServer(), getBranch(), getProjectKey(), metricKey));
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
