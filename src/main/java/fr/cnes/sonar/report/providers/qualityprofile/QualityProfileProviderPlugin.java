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

package fr.cnes.sonar.report.providers.qualityprofile;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.*;
import org.sonarqube.ws.client.WsClient;

import java.util.List;

/**
 * Provides quality gates in plugin mode
 */
public class QualityProfileProviderPlugin extends AbstractQualityProfileProvider implements QualityProfileProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public QualityProfileProviderPlugin(final WsClient wsClient, final String project) {
        super(wsClient, project);
    }

    @Override
    public List<QualityProfile> getQualityProfiles() throws BadSonarQubeRequestException, SonarQubeException {
        return getQualityProfilesAbstract(false);
    }
}