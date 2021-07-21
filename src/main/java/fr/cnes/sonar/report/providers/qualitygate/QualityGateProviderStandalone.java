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

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;

import java.util.List;
import java.util.Map;

/**
 * Provides quality gates in standalone mode
 */
public class QualityGateProviderStandalone extends AbstractQualityGateProvider implements QualityGateProvider {    

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
        return getQualityGatesAbstract(true);
    }

    @Override
    public QualityGate getProjectQualityGate()
            throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException {
        return getProjectQualityGateAbstract(true);
    }

    @Override
    public Map<String, String> getQualityGateStatus() throws BadSonarQubeRequestException, SonarQubeException {
        return getQualityGateStatusAbstract(true);
    }
}
