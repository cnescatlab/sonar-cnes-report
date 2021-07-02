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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Measure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides issue items in standalone mode
 */
public class MeasureProviderStandalone extends AbstractMeasureProvider implements MeasureProvider {

    /**
     *  Name of the request for getting measures
     */
    private static final String GET_MEASURES_REQUEST = "GET_MEASURES_REQUEST";

    /**
     * Complete constructor
     * @param pServer SonarQube server..
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    public MeasureProviderStandalone(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    @Override
    public List<Measure> getMeasures() throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return the response as a json object
        // if there is an error on server side this method throws an exception
        final JsonObject jo = request(String.format(getRequest(GET_MEASURES_REQUEST),
                getServer(), getProjectKey(), getBranch()));

        // json element containing measure information
        final JsonElement measuresJE = jo.get(COMPONENT).getAsJsonObject().get(MEASURES);
        // put json in a list of measures
        final Measure[] tmp = (getGson().fromJson(measuresJE, Measure[].class));

        // then add all measure to the results list
        // return the list
        return new ArrayList<>(Arrays.asList(tmp));
    }
}
