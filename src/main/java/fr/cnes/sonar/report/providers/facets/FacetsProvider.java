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

package fr.cnes.sonar.report.providers.facets;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facets;
import fr.cnes.sonar.report.model.TimeFacets;

/**
 * Generic interface for facets providers
 */
public interface FacetsProvider {
    /**
     * Get all the stats on a project
     * @return A list of facets
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    Facets getFacets() throws BadSonarQubeRequestException, SonarQubeException;
    /**
     * Get all time values on a project
     * @return A list of facets
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    TimeFacets getTimeFacets() throws BadSonarQubeRequestException, SonarQubeException;
}