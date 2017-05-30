package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.params.ParamsFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides issue items
 * @author begarco
 */
public class IssuesProvider implements IDataProvider {

    private Params params;
    private static final Logger LOGGER = Logger.getLogger(IssuesProvider.class.getCanonicalName());

    public IssuesProvider(Params params) {
        this.setParams(params);
    }

    /**
     * Get all the issues of a project
     * @return Array containing all the issues
     */
    public List<Issue> getIssues() throws IOException, UnknownParameterException {
        ArrayList<Issue> res = new ArrayList<>();


        boolean goon = true;
        int page = 1;

        Gson gson = new Gson();
        String url = getParams().get("sonar.url");
        String projectKey = getParams().get("sonar.project.id");

        while(goon) {
            String request = String.format("%s/api/issues/search?projectKeys=%s&resolved=false&facets=types,rules,severities,directories,fileUuids,tags&ps=%d&p=%d&additionalFields=rules",
                    url, projectKey, IDataProvider.MAX_PER_PAGE_SONARQUBE, page);
            String raw = RequestManager.getInstance().get(request);
            JsonElement json = gson.fromJson(raw, JsonElement.class);
            JsonObject jo = json.getAsJsonObject();
            Issue [] tmp = (gson.fromJson(jo.get("issues"), Issue[].class));
            res.addAll(Arrays.asList(tmp));
            int number = (json.getAsJsonObject().get("total").getAsInt());
            goon = page*IDataProvider.MAX_PER_PAGE_SONARQUBE < number;
            page++;
        }

        return res;
    }

    /**
     * Get all the stats on a project
     * @return A list of facets
     * @throws IOException on data processing error
     * @throws UnknownParameterException on bad parameter
     */
    public List<Facet> getFacets() throws IOException, UnknownParameterException {
        ArrayList<Facet> res = new ArrayList<>();

        Gson gson = new Gson();
        String url = getParams().get("sonar.url");
        String projectKey = getParams().get("sonar.project.id");

        String request = String.format("%s/api/issues/search?projectKeys=%s&resolved=false&facets=rules,severities,types&ps=1&p=1",
                url, projectKey);
        String raw = RequestManager.getInstance().get(request);
        JsonElement json = gson.fromJson(raw, JsonElement.class);
        JsonObject jo = json.getAsJsonObject();
        Facet [] tmp = (gson.fromJson(jo.get("facets"), Facet[].class));
        res.addAll(Arrays.asList(tmp));

        return res;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }
}
