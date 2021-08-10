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

package fr.cnes.sonar.report.providers.sonarqubeinfo;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

/**
 * Generic interface for SonarQube info providers
 */
public interface SonarQubeInfoProvider {
    /**
     * Get the SonarQube version.
     * @return String containing the SonarQube version.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    String getSonarQubeVersion() throws BadSonarQubeRequestException, SonarQubeException;
    /**
     * Get the SonarQube server status.
     * @return String containing the SonarQube status.
     */
    String getSonarQubeStatus();
}