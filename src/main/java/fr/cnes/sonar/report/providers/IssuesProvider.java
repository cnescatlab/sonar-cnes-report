package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides issue items
 * @author begarco
 */
public class IssuesProvider extends AbstractDataProvider {

    /**
     * Complete constructor
     * @param params Program's parameters
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public IssuesProvider(Params params) throws UnknownParameterException {
        super(params);
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
        ArrayList<Issue> res = new ArrayList<>();

        // stop condition
        boolean goon = true;
        // current page
        int page = 1;

        // search all issues of the project
        while(goon) {
            // prepare the url to get all the issues
            String request = String.format(getRequest("GET_ISSUES_REQUEST"),
                    getUrl(), getProjectKey(), Integer.valueOf(getRequest("MAX_PER_PAGE_SONARQUBE")), page);
            // perform the request to the server
            JsonObject jo = request(request);
            // transform json to Issue objects
            Issue [] tmp = (getGson().fromJson(jo.get("issues"), Issue[].class));
            // add them to the final result
            res.addAll(Arrays.asList(tmp));
            // check next results' pages
            int number = (jo.get("total").getAsInt());
            goon = page* Integer.valueOf(getRequest("MAX_PER_PAGE_SONARQUBE")) < number;
            page++;
        }

        // return the issues
        return res;
    }

    /**
     * Get all the stats on a project
     * @return A list of facets
     * @throws IOException on data processing error
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<Facet> getFacets() throws IOException, BadSonarQubeRequestException {
        // results variable
        ArrayList<Facet> res = new ArrayList<>();

        // prepare the request
        String request = String.format(getRequest("GET_FACETS_REQUEST"), getUrl(), getProjectKey());
        // contact the server to request the data as json
        JsonObject jo = request(request);
        // put wanted data in facets array and list
        Facet [] tmp = (getGson().fromJson(jo.get("facets"), Facet[].class));
        res.addAll(Arrays.asList(tmp));

        // return list of facets
        return res;
    }
}
