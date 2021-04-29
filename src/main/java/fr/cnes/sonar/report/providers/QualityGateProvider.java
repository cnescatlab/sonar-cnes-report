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
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.model.SonarQubeServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides quality gates
 */
public class QualityGateProvider extends AbstractDataProvider {

    /** Field to find in json. */
    private static final String QUALITY_GATE = "qualityGate";
    /** Field to find in json corresponding to the quality gate's id. */
    private static final String KEY = "key";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    public QualityGateProvider(final SonarQubeServer pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Get all the quality gates.
     * @return Array containing all the issues.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<QualityGate> getQualityGates()
            throws BadSonarQubeRequestException, SonarQubeException {
        // result list
        final List<QualityGate> res = new ArrayList<>();

        // Get all quality gates
        String request = String.format(getRequest(GET_QUALITY_GATES_REQUEST), getServer().getUrl());
        // perform the request to the server
        JsonObject jo = request(request);

        // Get quality gates criteria
        final String defaultQG = (getGson().fromJson(jo.get(DEFAULT), String.class));
        final QualityGate[] tmp = (getGson().fromJson(jo.get(QUALITYGATES), QualityGate[].class));
        // for each quality gate
        for (QualityGate i : tmp) {
            // request the criteria
            request = String.format(getRequest(GET_QUALITY_GATES_DETAILS_REQUEST),
                    getServer().getUrl(), i.getName().replace(" ", "%20"));
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

    /**
     * Return the quality gate corresponding to the project.
     * @return The Quality Gate.
     * @throws UnknownQualityGateException When there is an error on a quality gate.
     * @throws BadSonarQubeRequestException When the request is incorrect.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public QualityGate getProjectQualityGate()
            throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {

        // get all the quality gates
        final List<QualityGate> qualityGates = getQualityGates();
        // get the project
        String request = String.format(getRequest(GET_PROJECT_REQUEST),
                getServer().getUrl(), getProjectKey(), getBranch());
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
}
