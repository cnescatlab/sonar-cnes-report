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

package fr.cnes.sonar.report.providers.project;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Project;

/**
 * Generic interface for project providers
 */
public interface ProjectProvider {
    /**
     * Get the project corresponding to the given key.
     * @param projectKey the key of the project.
     * @param branch the branch of the project.
     * @return A simple project.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    Project getProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException;
    /**
     * Check if a project exists on a SonarQube instance.
     * @param projectKey the key of the project.
     * @param branch the branch of the project.
     * @return True if the project exists.
     * @throws BadSonarQubeRequestException when the server does not understand the request.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    boolean hasProject(final String projectKey, final String branch) throws BadSonarQubeRequestException, SonarQubeException;
}