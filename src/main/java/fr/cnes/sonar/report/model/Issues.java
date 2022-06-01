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

package fr.cnes.sonar.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of Issue objects
 */
public class Issues {

    /**
     * List of issues
     */
    private List<Issue> issuesList;

    /**
     * Default constructor
     */
    public Issues() {
        this.issuesList = new ArrayList<>();
    }

    /**
     * Setter for issues
     * @param pIssues List of issues
     */
    public void setIssuesList(List<Issue> pIssues) {
        this.issuesList = new ArrayList<>(pIssues);
    }

    /**
     * Get issues List
     * @return List of issues
     */
    public List<Issue> getIssuesList() {
        return new ArrayList<>(this.issuesList);
    }

    /**
     * Get the first Issue matching a given rule
     * @param pRuleKey
     * @return The first Issue related to the given rule key
     */
    public Issue getFirstIssueMatchingRule(String pRuleKey) {
        Issue match = null;

        for(Issue issue : this.issuesList) {
            if (issue.getRule() == pRuleKey) {
                match = issue;
                break;
            }
        }

        return match;
    }
}
