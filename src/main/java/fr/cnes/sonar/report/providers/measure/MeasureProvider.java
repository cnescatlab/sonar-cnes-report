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

/**
 * Generic interface for measure providers
 */
public interface MeasureProvider {
    /**
     * Get all the measures of a project
     * @return Array containing all the measures
     * @throws BadSonarQubeRequestException when the server does not understand the request
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    List<Measure> getMeasures() throws BadSonarQubeRequestException, SonarQubeException;
}