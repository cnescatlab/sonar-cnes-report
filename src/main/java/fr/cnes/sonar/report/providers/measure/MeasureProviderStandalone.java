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

import java.util.List;

import com.google.gson.JsonObject;

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
        return getMeasuresAbstract();
    }

    @Override
    protected JsonObject getMeasuresAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
        return request(String.format(getRequest(GET_MEASURES_REQUEST), getServer(), getProjectKey(), getMetrics(REPORTS_METRICS), getBranch()));
    }
}
