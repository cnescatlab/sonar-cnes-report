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

package fr.cnes.sonar.report.providers.issues;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Issue;

import java.util.List;
import java.util.Map;

/**
 * Generic interface for issues providers
 */
public interface IssuesProvider {
    /**
     * Get all the real issues of a project
     * @return Array containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    List<Issue> getIssues() throws BadSonarQubeRequestException, SonarQubeException;
    /**
     * Get all the unconfirmed issues of a project
     * @return Array containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    List<Issue> getUnconfirmedIssues() throws BadSonarQubeRequestException, SonarQubeException;
    /**
     * Get all the issues of a project in a raw format (map)
     * @return Array containing all the issues as maps
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    List<Map<String,String>> getRawIssues() throws BadSonarQubeRequestException, SonarQubeException;
}