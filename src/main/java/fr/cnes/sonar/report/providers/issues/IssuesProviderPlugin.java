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

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.utils.StringManager;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.issues.SearchRequest;
import org.sonarqube.ws.Issues.SearchWsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides issue items in plugin mode
 */
public class IssuesProviderPlugin extends AbstractIssuesProvider implements IssuesProvider {

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
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public IssuesProviderPlugin(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    @Override
    public List<Issue> getIssues() {
        return getIssuesByStatus(CONFIRMED);
    }

    @Override
    public List<Issue> getUnconfirmedIssues() {
        return getIssuesByStatus(UNCONFIRMED);
    }

    /**
     * Get issues depending on their resolved status
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    private List<Issue> getIssuesByStatus(String confirmed) {
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
            final JsonObject jo = responseToJsonObject(searchWsResponse);
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

    @Override
    public List<Map<String,String>> getRawIssues() {
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
            final JsonObject jo = responseToJsonObject(searchWsResponse);
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

    @Override
    public List<Facet> getFacets() {
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
        final JsonObject jo = responseToJsonObject(searchWsResponse);
        // put wanted resources in facets array and list
        final Facet [] tmp = (getGson().fromJson(jo.get(FACETS), Facet[].class));
        // return list of facets
        return new ArrayList<>(Arrays.asList(tmp));
    }
}