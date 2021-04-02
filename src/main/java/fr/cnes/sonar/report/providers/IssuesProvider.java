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

package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides issue items
 */
public class IssuesProvider extends AbstractDataProvider {

    /**
     * Correspond to the maximum number of issues that SonarQube allow
     * web api's users to collect.
     */
    private static final int MAXIMUM_ISSUES_LIMIT = 10000;
    /**
     * Value of the field to get confirmed issues
     */
    private static final String CONFIRMED = "false";
    /**
     * Value of the field to get unconfirmed issues
     */
    private static final String UNCONFIRMED = "true";
    /**
     * Parameter "issues" of the JSON response
     */
    private static final String ISSUES = "issues";
    /**
     * Parameter "facets" of the JSON response
     */
    private static final String FACETS = "facets";
    /**
     * Parameter "severity" of the JSON response in the issues
     */
    private static final String SEVERITY = "severity";
    /**
     * Parameter "severities" of the JSON response in the facets
     */
    private static final String SEVERITIES = "severities";
    /**
     * Parameter "types" of the JSON response in the facets
     */
    private static final String TYPES = "types";
    /**
     * Parameter "values" of the JSON response in the facets
     */
    private static final String VALUES = "values";
    /**
     * Parameter "count" of the JSON response in the facets
     */
    private static final String COUNT = "count";
    /**
     * Parameter "property" of the JSON response in the facets
     */
    private static final String PROPERTY = "property";
    /**
     * Parameter "val" of the JSON response in the facets
     */
    private static final String VAL = "val";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    public IssuesProvider(final SonarQubeServer pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Get all the real issues of a project
     * @return Array containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<Issue> getIssues()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(CONFIRMED);
    }

    /**
     * Get all the unconfirmed issues of a project
     * @return Array containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<Issue> getUnconfirmedIssues()
            throws BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(UNCONFIRMED);
    }

    /**
     * Get issues depending on their resolved status
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    private List<Issue> getIssuesByStatus(String confirmed)
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
            // prepare the server to get all the issues
            final String request = String.format(getRequest(GET_ISSUES_REQUEST),
                    getServer().getUrl(), getProjectKey(), maxPerPage, page, confirmed, getBranch());
            // perform the request to the server
            final JsonObject jo = request(request);

            // Add the severity "CRITICAL" for the security hotspots
            // Because the issue type "hotspot security" do not have any severity on the json file
            JsonArray ja = jo.getAsJsonArray(ISSUES);
            for(int i = 0; i < ja.size(); i++){
                JsonElement je = ja.get(i);
                JsonObject jobj = je.getAsJsonObject();
                // If there is no severity, it's a security hotspot issue
                if(!jobj.has(SEVERITY)){
                    // Add the proper property into the JSON file
                    jobj.addProperty(SEVERITY, StringManager.HOTSPOT_SEVERITY);
                }
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

    /**
     * Get all the issues of a project in a raw format (map)
     * @return Array containing all the issues as maps
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<Map<String,String>> getRawIssues() throws BadSonarQubeRequestException, SonarQubeException {
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
            final String request = String.format(getRequest(GET_ISSUES_REQUEST),
                    getServer().getUrl(), getProjectKey(), maxPerPage, page, CONFIRMED, getBranch());
            // perform the request to the server
            final JsonObject jo = request(request);

            // Add the severity "CRITICAL" for the security hotspots
            // Because the issue type "hotspot security" do not have any severity on the json file
            JsonArray ja = jo.getAsJsonArray(ISSUES);
            for(int i = 0; i < ja.size(); i++){
                JsonElement je = ja.get(i);
                JsonObject jobj = je.getAsJsonObject();
                // If there is no severity, it's a security hotspot issue
                if(!jobj.has(SEVERITY)){
                    // Add the proper property into the JSON file
                    jobj.addProperty(SEVERITY, StringManager.HOTSPOT_SEVERITY);
                }
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
     * Get all the stats on a project
     * @return A list of facets
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<Facet> getFacets() throws BadSonarQubeRequestException, SonarQubeException {

        // number of security hotspot and temporary number of critical issues
        int nbHotspots;
        
        // prepare the request
        final String request = String.format(getRequest(GET_FACETS_REQUEST),
                getServer().getUrl(), getProjectKey(), getBranch());
        // contact the server to request the resources as json
        final JsonObject jo = request(request);
        // set this JsonObject into a JsonArray for the facets
        JsonArray ja = jo.getAsJsonArray(FACETS);
        // get the number of security hotspots
        nbHotspots = getNbHotspots(ja);
        // set the real number of critical issues taking into account security hotspots
        setNbCriticalIssues(ja, nbHotspots);

        // put wanted resources in facets array and list
        final Facet [] tmp = (getGson().fromJson(jo.get(FACETS), Facet[].class));

        // return list of facets
        return new ArrayList<>(Arrays.asList(tmp));
    }

    /**
     * Get the number of security hotspot type
     * @param ja JsonArray of a given request
     * @return return the number of security hotspots
     */
    private int getNbHotspots(JsonArray ja){
        // return 0 if there is no security hotspot
        int nbHotspots = 0;
        // search for the facet with the property named "types"
        for(int i = 0; i < ja.size(); i++){
            JsonElement je = ja.get(i);
            JsonObject jo = je.getAsJsonObject();
            String property = jo.get(PROPERTY).getAsString();
            if(property.equals(TYPES)){
                // get the array of the values of this property and search for the val "SECURITY_HOTSPOT"
                JsonArray jaValues = jo.getAsJsonArray(VALUES);
                for(int j = 0; j < jaValues.size(); j++){
                    JsonElement je2 = jaValues.get(j);
                    String type = je2.getAsJsonObject().get(VAL).getAsString();
                    if(type.equals(StringManager.HOTSPOT_TYPE)){
                        // add the number of security hotspots to return this value
                        nbHotspots += je2.getAsJsonObject().get(COUNT).getAsInt();
                    }
                }
            }
        }
        return nbHotspots;
    }

    /**
     * Set the real number of critical issue taking into account the security hotspots
     * @param ja JsonArray of a given request
     * @param nbHotspots the number of security hotspots
     */
    private void setNbCriticalIssues(JsonArray ja, int nbHotspots){
        // search for the facet with the property named "severities"
        for(int i = 0; i < ja.size(); i++){
            JsonElement je = ja.get(i);
            JsonObject jb = je.getAsJsonObject();
            String property = jb.get(PROPERTY).getAsString();
            if (property.equals(SEVERITIES)){
                // get the array of the values of this property and search for the val "CRITICAL"
                JsonArray jaValues = jb.getAsJsonArray(VALUES);
                for(int k = 0; k < jaValues.size(); k++){
                    JsonElement je3 = jaValues.get(k);
                    String severity = je3.getAsJsonObject().get(VAL).getAsString();
                    if(severity.equals(StringManager.HOTSPOT_SEVERITY)){
                        // get the actual number of critical issues
                        int nbTmp = je3.getAsJsonObject().get(COUNT).getAsInt();
                        int nbCriticalIssues = nbTmp + nbHotspots;
                        // add the number of security hotspots to the number of critical issues to have the real number of critical issues
                        je3.getAsJsonObject().addProperty(COUNT, nbCriticalIssues);
                    }
                }
            }

        }
    }
}
