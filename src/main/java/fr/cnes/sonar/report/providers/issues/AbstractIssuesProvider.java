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

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Rule;
import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for issues providers
 */
public abstract class AbstractIssuesProvider extends AbstractDataProvider {

    /**
     * Correspond to the maximum number of issues that SonarQube allow
     * web api's users to collect.
     */
    protected static final int MAXIMUM_ISSUES_LIMIT = 10000;
    /**
     * Value of the field to get confirmed issues
     */
    protected static final String CONFIRMED = "false";
    /**
     * Value of the field to get unconfirmed issues
     */
    protected static final String UNCONFIRMED = "true";
    /**
     * Parameter "issues" of the JSON response
     */
    protected static final String ISSUES = "issues";
    /**
     * Parameter "facets" of the JSON response
     */
    protected static final String FACETS = "facets";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractIssuesProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractIssuesProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Find the display name of the programming language corresponding
     * to a rule with its key
     * @param ruleKey key of the rule to find
     * @param rules array of the rules to browse
     * @return a String containing the display name of the programming language
     */
    protected String findLanguageOf(String ruleKey, Rule[] rules) {
        // stop condition for the main loop
        boolean again = true;
        // increment for browsing the array
        int inc = 0;

        // result to return
        String language = "";

        // we iterate on the array until we find the good key
        while(again && inc < rules.length) {
            if(ruleKey.equals(rules[inc].getKey())) {
                again = false;
                language = rules[inc].getLangName();
            }
            inc++;
        }

        return language;
    }

    /**
     * Set the language of each issues
     * @param issues an array of issues to set
     * @param rules an array of rules containing language information
     */
    protected void setIssuesLanguage(Issue[] issues, Rule[] rules) {
        // rule's key of an issue
        String rulesKey;
        // language of the previous rule's key
        String rulesLanguage;

        // for each issue we associate the corresponding programming language
        // by browsing the rules array
        for (Issue issue : issues) {
            rulesKey = issue.getRule();
            rulesLanguage = findLanguageOf(rulesKey, rules);
            issue.setLanguage(rulesLanguage);
        }
    }

}