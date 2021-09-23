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
 * Generic interface for quality gate providers
 */
public interface QualityGateProvider {
    /**
     * Get all the quality gates.
     * @return Array containing all the issues.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    List<QualityGate> getQualityGates() throws BadSonarQubeRequestException, SonarQubeException;
    /**
     * Return the quality gate corresponding to the project.
     * @return The Quality Gate.
     * @throws UnknownQualityGateException When there is an error on a quality gate.
     * @throws BadSonarQubeRequestException When the request is incorrect.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    QualityGate getProjectQualityGate() throws UnknownQualityGateException, BadSonarQubeRequestException, SonarQubeException;
    /**
     * Get the quality gate status of a project
     * @return Map containing each condition of the quality gate and its status
     * @throws BadSonarQubeRequestException when the server does not understand the request
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    Map<String, String> getQualityGateStatus() throws BadSonarQubeRequestException, SonarQubeException;
}