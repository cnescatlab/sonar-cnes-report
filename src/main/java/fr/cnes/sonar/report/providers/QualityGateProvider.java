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

package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.input.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides quality gates
 * @author lequal
 */
public class QualityGateProvider extends AbstractDataProvider {

    /**
     * Field to find in json
     */
    private static final String QUALITY_GATE = "qualityGate";
    /**
     * Field to find in json corresponding to the quality gate's id
     */
    private static final String KEY = "key";

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public QualityGateProvider(Params params, RequestManager singleton)
            throws UnknownParameterException {
        super(params, singleton);
    }

    /**
     * Get all the quality gates
     * @return Array containing all the issues
     * @throws IOException when connecting the server
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<QualityGate> getQualityGates()
            throws IOException, BadSonarQubeRequestException {
        // result list
        final List<QualityGate> res = new ArrayList<>();

        // Get all quality gates
        String request = String.format(getRequest(GET_QUALITY_GATES_REQUEST), getUrl());
        // perform the request to the server
        JsonObject jo = request(request);

        // Get quality gates criteria
        final String defaultQG = (getGson().fromJson(jo.get(DEFAULT), String.class));
        final QualityGate[] tmp = (getGson().fromJson(jo.get(QUALITYGATES), QualityGate[].class));
        // for each quality gate
        for (QualityGate i : tmp) {
            // request the criteria
            request = String.format(getRequest(GET_QUALITY_GATES_DETAILS_REQUEST),
                    getUrl(), i.getName().replaceAll(" ", "%20"));
            // perform previous request
            jo = request(request);

            // put it in configuration field
            i.setConf(jo.toString());
            // check if it is the default quality gate
            if (i.getId().equals(defaultQG)) {
                i.setDefault(true);
            } else {
                i.setDefault(false);
            }
            // add the quality gate to the result list
            res.add(i);
        }

        return res;
    }

    /**
     * Return the quality gate corresponding to the project
     * @return The Quality Gate
     * @throws IOException when there are problem reading json
     * @throws UnknownQualityGateException when there is an error on a quality gate
     * @throws BadSonarQubeRequestException when the request is incorrect
     */
    public QualityGate getProjectQualityGate()
            throws IOException, UnknownQualityGateException, BadSonarQubeRequestException {
        QualityGate res = null;
        QualityGate tmp;
        boolean find = false;
        // get all the quality gates
        final List<QualityGate> qualityGates = getQualityGates();
        // request the criteria
        final String request = String.format(getRequest(GET_QUALITY_GATE_REQUEST),
                getUrl(), getProjectKey());

        // perform previous request
        final JsonObject jo = request(request);
        final String key = jo.getAsJsonObject(QUALITY_GATE)
                .get(KEY).getAsString();

        // search for the good quality gate
        final Iterator<QualityGate> iterator = qualityGates.iterator();
        while (iterator.hasNext() && !find) {
            tmp = iterator.next();
            if(tmp.getId().equals(key)) {
                res = tmp;
                find = true;
            }
        }

        // check if it was found
        if(!find) {
            throw new UnknownQualityGateException(key);
        }

        return res;
    }
}
