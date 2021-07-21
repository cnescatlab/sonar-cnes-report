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
import fr.cnes.sonar.report.utils.StringManager;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.issues.SearchRequest;
import org.sonarqube.ws.Issues.SearchWsResponse;

/**
 * Contains common code for issues providers
 */
public abstract class AbstractIssuesProvider extends AbstractDataProvider {

    /**
     *  Name of the request for getting issues
     */
    private static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";
    /**
     *  Name of the request for getting facets
     */
    private static final String GET_FACETS_REQUEST = "GET_FACETS_REQUEST";
    /**
     * Correspond to the maximum number of issues that SonarQube allow
     * web api's users to collect.
     */
    private static final int MAXIMUM_ISSUES_LIMIT = 10000;
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
    private static final String ISSUES = "issues";
    /**
     * Parameter "facets" of the JSON response
     */
    private static final String FACETS = "facets";
    /**
     * Field to get the types facet in a response
     */
    private static final String TYPES = "types";
    /**
     * Field to get the severities facet in a response
     */
    private static final String SEVERITIES = "severities";

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
     * Generic getter for issues depending on their resolved status
     * @param isCalledInStandalone True if the method is called in standalone mode
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<Issue> getIssuesByStatusAbstract(final boolean isCalledInStandalone, final String confirmed)
            throws BadSonarQubeRequestException, SonarQubeException {
        // results variable
        final List<Issue> res = new ArrayList<>();

        // stop condition
        boolean goOn = true;
        // flag when there are too many violation (> MAXIMUM_ISSUES_LIMIT)
        boolean overflow = false;
        // current page
        int page = 1;

        // temporary declared variable to contain data from ws
        Issue [] issuesTemp;
        Rule[] rulesTemp;

        // search all issues of the project
        while(goOn) {
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
            JsonObject jo;
            if (isCalledInStandalone) {
                // prepare the server to get all the issues
                final String request = String.format(getRequest(GET_ISSUES_REQUEST),
                        getServer(), getProjectKey(), maxPerPage, page, confirmed, getBranch());
                // perform the request to the server
                jo = request(request);
            } else {
                // prepare the server to get all the issues
                final List<String> projects = new ArrayList<>(Arrays.asList(getProjectKey()));
                final List<String> facets = new ArrayList<>(Arrays.asList(TYPES, RULES, SEVERITIES, "directories", "files", "tags"));
                final String ps = String.valueOf(maxPerPage);
                final String p = String.valueOf(page);
                final List<String> additionalFields = new ArrayList<>(Arrays.asList(RULES, "comments"));
                final SearchRequest searchRequest = new SearchRequest()
                                                        .setProjects(projects)
                                                        .setFacets(facets)
                                                        .setPs(ps)
                                                        .setP(p)
                                                        .setAdditionalFields(additionalFields)
                                                        .setResolved(confirmed)
                                                        .setBranch(getBranch());
                // perform the request to the server
                final SearchWsResponse searchWsResponse = getWsClient().issues().search(searchRequest);
                // transform response to JsonObject
                jo = responseToJsonObject(searchWsResponse);
            }
            // transform json to Issue and Rule objects
            issuesTemp = (getGson().fromJson(jo.get(ISSUES), Issue[].class));
            rulesTemp = (getGson().fromJson(jo.get(RULES), Rule[].class));
            // association of issues and languages
            setIssuesLanguage(issuesTemp, rulesTemp);
            // add them to the final result
            res.addAll(Arrays.asList(issuesTemp));
            // check next results' pages
            int number = (jo.get(TOTAL).getAsInt());

            // check overflow
            if(number > MAXIMUM_ISSUES_LIMIT) {
                number = MAXIMUM_ISSUES_LIMIT;
                overflow = true;
            }
            goOn = page* maxPerPage < number;
            page++;
        }

        // in case of overflow we log the problem
        if(overflow) {
            String message = StringManager.string(StringManager.ISSUES_OVERFLOW_MSG);
            LOGGER.warning(message);
        }

        // return the issues
        return res;
    }

    /**
     * Generic getter for all the issues of a project in a raw format (map)
     * @param isCalledInStandalone True if the method is called in standalone mode
     * @return Array containing all the issues as maps
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<Map<String,String>> getRawIssuesAbstract(final boolean isCalledInStandalone) throws BadSonarQubeRequestException, SonarQubeException {
        // results variable
        final List<Map<String,String>> res = new ArrayList<>();

        // stop condition
        boolean goon = true;
        // flag when there are too many violation (> MAXIMUM_ISSUES_LIMIT)
        boolean overflow = false;
        // current page
        int page = 1;

        // search all issues of the project
        while(goon) {
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
            JsonObject jo;
            if (isCalledInStandalone) {
                // prepare the server to get all the issues
                final String request = String.format(getRequest(GET_ISSUES_REQUEST),
                        getServer(), getProjectKey(), maxPerPage, page, CONFIRMED, getBranch());
                // perform the request to the server
                jo = request(request);
            } else {
                // prepare the server to get all the issues
                final List<String> projects = new ArrayList<>(Arrays.asList(getProjectKey()));
                final List<String> facets = new ArrayList<>(Arrays.asList(TYPES, RULES, SEVERITIES, "directories", "files", "tags"));
                final String ps = String.valueOf(maxPerPage);
                final String p = String.valueOf(page);
                final List<String> additionalFields = new ArrayList<>(Arrays.asList(RULES, "comments"));
                final SearchRequest searchRequest = new SearchRequest()
                                                        .setProjects(projects)
                                                        .setFacets(facets)
                                                        .setPs(ps)
                                                        .setP(p)
                                                        .setAdditionalFields(additionalFields)
                                                        .setResolved(CONFIRMED)
                                                        .setBranch(getBranch());
                // perform the request to the server
                final SearchWsResponse searchWsResponse = getWsClient().issues().search(searchRequest);
                // transform response to JsonObject
                jo = responseToJsonObject(searchWsResponse);
            }
            // transform json to Issue objects
            final Map<String,String> [] tmp = (getGson().fromJson(jo.get(ISSUES), Map[].class));
            // add them to the final result
            res.addAll(Arrays.asList(tmp));
            // check next results' pages
            int number = (jo.get(TOTAL).getAsInt());

            // check overflow
            if(number > MAXIMUM_ISSUES_LIMIT) {
                number = MAXIMUM_ISSUES_LIMIT;
                overflow = true;
            }

            goon = page* maxPerPage < number;
            page++;
        }

        // in case of overflow we log the problem
        if(overflow) {
            String message = StringManager.string(StringManager.ISSUES_OVERFLOW_MSG);
            LOGGER.warning(message);
        }

        // return the issues
        return res;
    }

    /**
     * Generic getter ofr all the stats on a project
     * @param isCalledInStandalone True if the method is called in standalone mode
     * @return A list of facets
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<Facet> getFacetsAbstract(final boolean isCalledInStandalone) throws BadSonarQubeRequestException, SonarQubeException {
        JsonObject jo;
        if (isCalledInStandalone) {
            // prepare the request
            final String request = String.format(getRequest(GET_FACETS_REQUEST),
                    getServer(), getProjectKey(), getBranch());
            // contact the server to request the resources as json
            jo = request(request);
        } else {
            // prepare the request
            final List<String> projects = new ArrayList<>(Arrays.asList(getProjectKey()));
            final List<String> facets = new ArrayList<>(Arrays.asList(TYPES, RULES, SEVERITIES));
            final String ps = String.valueOf(1);
            final String p = String.valueOf(1);
            final SearchRequest searchRequest = new SearchRequest()
                                                    .setProjects(projects)
                                                    .setResolved(CONFIRMED)
                                                    .setFacets(facets)
                                                    .setPs(ps)
                                                    .setP(p)
                                                    .setBranch(getBranch());
            // perform the request to the server
            final SearchWsResponse searchWsResponse = getWsClient().issues().search(searchRequest);
            // transform response to JsonObject
            jo = responseToJsonObject(searchWsResponse);
        }
        // put wanted resources in facets array and list
        final Facet [] tmp = (getGson().fromJson(jo.get(FACETS), Facet[].class));
        // return list of facets
        return new ArrayList<>(Arrays.asList(tmp));
    }

    /**
     * Find the display name of the programming language corresponding
     * to a rule with its key
     * @param ruleKey key of the rule to find
     * @param rules array of the rules to browse
     * @return a String containing the display name of the programming language
     */
    private String findLanguageOf(String ruleKey, Rule[] rules) {
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
    private void setIssuesLanguage(Issue[] issues, Rule[] rules) {
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