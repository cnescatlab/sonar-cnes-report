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
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.Arrays;
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
    /** Field to find in json response. */
    private static final String RESULTS = "results";

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
                    getServer().getUrl(), i.getName().replaceAll(" ", "%20"));
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
        // request the criteria
        String request = String.format(getRequest(GET_QUALITY_GATE_REQUEST),
                getServer().getUrl(), getProjectKey(), getBranch());
        // Final quality gate result.
        QualityGate res;

        // perform previous request
        final JsonObject jsonObject = request(request);

        // search for the good quality gate
        final Iterator<QualityGate> iterator = qualityGates.iterator();

        if(server.getNormalizedVersion().matches("5.*|6.[012].*")) { // Special code for SonarQube v5.*
            res = getProjectQualityGateOld(jsonObject, iterator);
        } else { // code for new SonarQube versions
            res = getProjectQualityGateNew(jsonObject, iterator);
        }

        return res;
    }

    /**
     * Get project quality gate for new SonarQube versions.
     * @param jsonObject Json Object containing quality gates information.
     * @param iterator Iterator on quality gates.
     * @return The project quality gate.
     * @throws UnknownQualityGateException Quality gate does not exist.
     */
    private QualityGate getProjectQualityGateNew(final JsonObject jsonObject, final Iterator<QualityGate> iterator)
            throws UnknownQualityGateException {

        QualityGate tmp;
        QualityGate res = null;
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

    /**
     * Get project quality gate for old SonarQube versions.
     * @param jsonObject Json Object containing quality gates information.
     * @param iterator Iterator on quality gates.
     * @return The project quality gate.
     * @throws BadSonarQubeRequestException Malformed request.
     * @throws UnknownQualityGateException Quality gate does not exist.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    private QualityGate getProjectQualityGateOld(final JsonObject jsonObject, final Iterator<QualityGate> iterator)
            throws BadSonarQubeRequestException, UnknownQualityGateException, SonarQubeException {

        QualityGate tmp;
        QualityGate res = null;
        boolean find = false;
        final Project project = getGson().fromJson(jsonObject, Project.class);
        Iterator<Project> iteratorOnProjects;
        JsonObject response;
        Project[] projects;
        Project tmpProject;

        while (iterator.hasNext() && !find) {
            tmp = iterator.next();
            // In version 5.X if the quality gate is the default one:
            // quality gate and project are not linked to each other
            // so we set default qg if we found no corresponding gate
            if (tmp.isDefault()) {
                res = tmp;
            }
            String request = String.format(getRequest(QUALITY_GATE_PROJECTS_REQUEST),
                    getServer().getUrl(), tmp.getId(), project.getName());
            response = request(request);
            projects = (getGson().fromJson(response.getAsJsonArray(RESULTS), Project[].class));
            iteratorOnProjects = Arrays.asList(projects).iterator();
            while (iteratorOnProjects.hasNext() && !find) {
                tmpProject = iteratorOnProjects.next();
                if (tmpProject.getName().equals(project.getName())) {
                    res = tmp;
                    find = true;
                }
            }
        }
        // check if it was found
        if(!find && res != null) {
            throw new UnknownQualityGateException(StringManager.EMPTY);
        }
        return res;
    }
}
