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
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.measures.ComponentRequest;
import org.sonarqube.ws.Measures.ComponentWsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * Provides issue items in plugin mode
 */
public class MeasureProviderPlugin extends AbstractMeasureProvider implements MeasureProvider {

    /**
     * Complete constructor.
     * 
     * @param wsClient The web client.
     * @param project  The id of the project to report.
     * @param branch   The branch of the project to report.
     */
    public MeasureProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public List<Measure> getMeasures() throws BadSonarQubeRequestException, SonarQubeException {
        return getMeasuresAbstract();
    }

    @Override
    protected JsonObject getMeasuresAsJsonObject() {
        List<String> metricKeys = new ArrayList<>(Arrays.asList(getMetrics(REPORTS_METRICS).split(",")));

        final ComponentRequest componentRequest = new ComponentRequest()
                .setComponent(getProjectKey())
                .setMetricKeys(metricKeys)
                .setBranch(getBranch());
        final ComponentWsResponse componentWsResponse = getWsClient().measures().component(componentRequest);
        return responseToJsonObject(componentWsResponse);
    }
}