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

package fr.cnes.sonar.report.providers.measure;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for measure providers
 */
public abstract class AbstractMeasureProvider extends AbstractDataProvider {

    /**
     * Field to search in json to get the component
     */
    private static final String COMPONENT = "component";
    /**
     * Field to search in json to get measures
     */
    private static final String MEASURES = "measures";
    /**
     *  Name of the SonarQube metrics to retrieve 
     */
    protected static final String REPORTS_METRICS = "REPORTS_METRICS";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractMeasureProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractMeasureProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Generic getter for all the measures of a project
     * @return Array containing all the measures
     * @throws BadSonarQubeRequestException when the server does not understand the request
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<Measure> getMeasuresAbstract() throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return the response as a json object
        final JsonObject jo = getMeasuresAsJsonObject();

        // json element containing measure information
        final JsonElement measuresJE = jo.get(COMPONENT).getAsJsonObject().get(MEASURES);
        // put json in a list of measures
        final Measure[] tmp = (getGson().fromJson(measuresJE, Measure[].class));

        // then add all measure to the results list
        // return the list
        return new ArrayList<>(Arrays.asList(tmp));
    }

    /**
     * Get a JsonObject from the response of a get component request.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getMeasuresAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException;
}