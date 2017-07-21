package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.input.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides issue items
 * @author begarco
 */
public class IssuesProvider extends AbstractDataProvider {

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public IssuesProvider(Params params, RequestManager singleton) throws UnknownParameterException {
        super(params, singleton);
    }

    /**
     * Get all the issues of a project
     * @return Array containing all the issues
     * @throws IOException when connecting the server
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<Issue> getIssues()
            throws IOException, BadSonarQubeRequestException {
        // results variable
        final List<Issue> res = new ArrayList<>();

        // stop condition
        boolean goOn = true;
        // current page
        int page = 1;

        // search all issues of the project
        while(goOn) {
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
            // prepare the url to get all the issues
            final String request = String.format(getRequest(GET_ISSUES_REQUEST),
                    getUrl(), getProjectKey(), maxPerPage, page);
            // perform the request to the server
            final JsonObject jo = request(request);
            // transform json to Issue objects
            final Issue [] tmp = (getGson().fromJson(jo.get(ISSUES), Issue[].class));
            // add them to the final result
            res.addAll(Arrays.asList(tmp));
            // check next results' pages
            final int number = (jo.get(TOTAL).getAsInt());
            goOn = page* maxPerPage < number;
            page++;
        }

        // return the issues
        return res;
    }

    /**
     * Get all the issues of a project in a raw format (map)
     * @return Array containing all the issues as maps
     * @throws IOException when connecting the server
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<Map> getRawIssues() throws IOException, BadSonarQubeRequestException {
        // results variable
        final List<Map> res = new ArrayList<>();

        // stop condition
        boolean goon = true;
        // current page
        int page = 1;

        // search all issues of the project
        while(goon) {
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
            // prepare the url to get all the issues
            String request = String.format(getRequest(GET_ISSUES_REQUEST),
                    getUrl(), getProjectKey(), maxPerPage, page);
            // perform the request to the server
            JsonObject jo = request(request);
            // transform json to Issue objects
            Map [] tmp = (getGson().fromJson(jo.get(ISSUES), Map[].class));
            // add them to the final result
            res.addAll(Arrays.asList(tmp));
            // check next results' pages
            int number = (jo.get(TOTAL).getAsInt());
            goon = page* maxPerPage < number;
            page++;
        }

        // return the issues
        return res;
    }

    /**
     * Get all the stats on a project
     * @return A list of facets
     * @throws IOException on resources processing error
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<Facet> getFacets() throws IOException, BadSonarQubeRequestException {
        // results variable
        final List<Facet> res = new ArrayList<>();

        // prepare the request
        final String request = String.format(getRequest(GET_FACETS_REQUEST), getUrl(), getProjectKey());
        // contact the server to request the resources as json
        final JsonObject jo = request(request);
        // put wanted resources in facets array and list
        final Facet [] tmp = (getGson().fromJson(jo.get(FACETS), Facet[].class));
        res.addAll(Arrays.asList(tmp));

        // return list of facets
        return res;
    }
}
