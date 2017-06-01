package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Generic interface for data providers
 * @author begarco
 */
public abstract class AbstractDataProvider {
    /**
     * Number max of results per page
     */
    final static int MAX_PER_PAGE_SONARQUBE = 500;

    /**
     * Logger for the class
     */
    static final Logger LOGGER = Logger.getLogger(AbstractDataProvider.class.getCanonicalName());

    /**
     * Request to get the list of metrics
     */
    static final String GET_MEASURES_REQUEST = "%s/api/measures/component?componentKey=%s&metricKeys=ncloc,duplicated_lines_density,coverage,sqale_rating,reliability_rating,security_rating,alert_status,complexity,function_complexity,file_complexity,class_complexity,blocker_violations,critical_violations,major_violations,minor_violations,info_violations,new_violations,bugs,vulnerabilities,code_smells";
    /**
     * Request to get the list of quality gates
     */
    static final String GET_QUALITY_GATES_REQUEST = "%s/api/qualitygates/list";
    /**
     * Request to get the details of a quality gate
     */
    static final String GET_QUALITY_GATES_DETAILS_REQUEST = "%s/api/qualitygates/show?name=%s";
    /**
     * Request to get the list of quality profiles
     */
    static final String GET_QUALITY_PROFILES_REQUEST = "%s/api/qualityprofiles/search?projectKey=%s";
    /**
     * Request to get the configuration file of a quality profile
     */
    static final String GET_QUALITY_PROFILES_CONFIGURATION_REQUEST = "%s/api/qualityprofiles/export?language=%s&name=%s";
    /**
     * Request to get the list of rules of a profile
     */
    static final String GET_QUALITY_PROFILES_RULES_REQUEST = "%s/api/rules/search?qprofile=%s&f=htmlDesc,name,repo,severity&ps=%d&p=%d";
    /**
     * Request to get the list of projects linked to a profile
     */
    static final String GET_QUALITY_PROFILES_PROJECTS_REQUEST = "%s/api/qualityprofiles/projects?key=%s";
    /**
     * Request to get the list of issues linked to a project
     */
    static final String GET_ISSUES_REQUEST = "%s/api/issues/search?projectKeys=%s&resolved=false&facets=types,rules,severities,directories,fileUuids,tags&ps=%d&p=%d&additionalFields=rules";
    /**
     * Request to get the list of a project's facets
     */
    static final String GET_FACETS_REQUEST = "%s/api/issues/search?projectKeys=%s&resolved=false&facets=rules,severities,types&ps=1&p=1";

    /**
     * Params of the program itself
     */
    private Params params;

    /**
     * Tool for parsing json
     */
    private Gson gson;

    /**
     * Url of the sonarqube server
     */
    private String url;

    /**
     * Key of the project to report
     */
    private String projectKey;

    /**
     * Name of the used quality gate
     */
    private String qualityGateName;

    /**
     * Constructor
     * @param params Program's parameters
     * @throws UnknownParameterException when a parameter is not known in the program
     */
    AbstractDataProvider(Params params) throws UnknownParameterException {
        this.params = params;
        // json tool
        this.gson = new Gson();
        // get sonar url
        this.url = getParams().get("sonar.url");
        // get project key
        this.projectKey = getParams().get("sonar.project.id");
        // get quality gate's name
        this.qualityGateName = getParams().get("sonar.project.quality.gate");
    }

    /**
     * Check if the server has sent an error
     * @param jsonObject The response from the server
     * @throws BadSonarQubeRequestException thrown if the server do not understand our request
     */
    private void isErrorFree(JsonObject jsonObject) throws BadSonarQubeRequestException {
        // we retrieve the exception
        JsonElement error = jsonObject.get("errors");
        // if there is an error we search the message and throw an exception
        if (error != null) {
            // get the error message
            JsonElement errorElement = error.getAsJsonArray().get(0).getAsJsonObject().get("msg");
            String errorMessage = (getGson().fromJson(errorElement, String.class));
            // throw exception if there was a problem when dealing with the server
            throw new BadSonarQubeRequestException(errorMessage);
        }
    }

    /**
     * Execute a given request
     * @param request Url for the request, for example http://sonarqube:1234/api/toto/list
     * @return Server's response as a JsonObject
     * @throws IOException if there were an error contacting the server
     * @throws BadSonarQubeRequestException if SonarQube Server sent an error
     */
    JsonObject request(String request) throws IOException, BadSonarQubeRequestException {
        // do the request to the server and return a string answer
        String raw = stringRequest(request);

        // prepare json
        JsonElement json;

        // verify that the server response was correct
        try {
            json = getGson().fromJson(raw, JsonElement.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadSonarQubeRequestException("Server answered: " + raw);
        }

        // get the json object version
        JsonObject jo = json.getAsJsonObject();

        // verify if an error occurred
        isErrorFree(jo);

        return jo;
    }

    /**
     * Get the raw string response
     * @param request the raw url of the request
     * @return the server's response as a string
     * @throws IOException when not able to contact the server
     */
    protected String stringRequest(String request) throws IOException {
        // prepare the request by replacing some relevant special characters
        // replace spaces
        String preparedRequest = request.replaceAll(" ", "%20");
        // replace + characters
        preparedRequest = preparedRequest.replaceAll("\\+", "%2B");

        // launch the request on sonarqube server and retrieve data into a string
        return RequestManager.getInstance().get(preparedRequest);
    }

    /**
     * Getter for params
     * @return a Params object
     */
    private Params getParams() {
        return params;
    }

    /**
     * Setter for params
     * @param params the value to give
     */
    private void setParams(Params params) {
        this.params = params;
    }

    /**
     * Json parsing tool
      */
    Gson getGson() {
        return gson;
    }

    /**
     * Setter of gson
     * @param gson value
     */
    void setGson(Gson gson) {
        this.gson = gson;
    }

    /**
     * Name of the project to report
     */
    String getUrl() {
        return url;
    }

    /**
     * Setter of url
     * @param url value
     */
    void setUrl(String url) {
        this.url = url;
    }

    /**
     * Key of the project to report
     */
    String getProjectKey() {
        return projectKey;
    }

    /**
     * Setter of projectKey
     * @param projectKey value to give
     */
    void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    /**
     * Quality gate's name (used by the project)
     */
    String getQualityGateName() {
        return qualityGateName;
    }

    /**
     * Setter of qualityGateName
     * @param qualityGateName value
     */
    void setQualityGateName(String qualityGateName) {
        this.qualityGateName = qualityGateName;
    }
}
