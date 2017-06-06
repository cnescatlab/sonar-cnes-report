package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Generic interface for data providers
 * @author begarco
 */
public abstract class AbstractDataProvider {

    /**
     * Logger for the class
     */
    static final Logger LOGGER = Logger.getLogger(AbstractDataProvider.class.getCanonicalName());

    /**
     * Name for properties' file about requests
     */
    private static final String REQUESTS_PROPERTIES = "requests.properties";

    /**
     *  Name of the request for getting quality profiles' linked projects
     */
    static final String GET_QUALITY_PROFILES_PROJECTS_REQUEST = "GET_QUALITY_PROFILES_PROJECTS_REQUEST";
    /**
     *  Name of the request for getting quality gates' details
     */
    static final String GET_QUALITY_GATES_DETAILS_REQUEST = "GET_QUALITY_GATES_DETAILS_REQUEST";
    /**
     *  Name of the request for getting quality profiles' linked rules
     */
    static final String GET_QUALITY_PROFILES_RULES_REQUEST = "GET_QUALITY_PROFILES_RULES_REQUEST";
    /**
     *  Name of the request for getting issues
     */
    static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";
    /**
     *  Name of the request for getting facets
     */
    static final String GET_FACETS_REQUEST = "GET_FACETS_REQUEST";
    /**
     *  Name of the property for the maximum number of results per page
     */
    static final String MAX_PER_PAGE_SONARQUBE = "MAX_PER_PAGE_SONARQUBE";
    /**
     *  Name of the request for getting quality gates
     */
    static final String GET_QUALITY_GATES_REQUEST = "GET_QUALITY_GATES_REQUEST";
    /**
     *  Name of the request for getting measures
     */
    static final String GET_MEASURES_REQUEST = "GET_MEASURES_REQUEST";
    /**
     *  Name of the request for getting quality profiles
     */
    static final String GET_QUALITY_PROFILES_REQUEST = "GET_QUALITY_PROFILES_REQUEST";
    /**
     *  Name of the request for getting quality profiles' configuration
     */
    static final String GET_QUALITY_PROFILES_CONFIGURATION_REQUEST = "GET_QUALITY_PROFILES_CONFIGURATION_REQUEST";
    /**
     * Field to search in json to get results' values
     */
    static final String RESULTS = "results";
    /**
     * Field to search in json to get profiles
     */
    static final String PROFILES = "profiles";
    /**
     * Field to search in json to get issues
     */
    static final String ISSUES = "issues";
    /**
     * Field to search in json to get the total page's number
     */
    static final String TOTAL = "total";
    /**
     * Field to search in json to get facets
     */
    static final String FACETS = "facets";
    /**
     * Field to search in json to get the component
     */
    static final String COMPONENT = "component";
    /**
     * Field to search in json to get measures
     */
    static final String MEASURES = "measures";
    /**
     * Field to search in json to get the boolean saying if a profile is the default one
     */
    static final String DEFAULT = "default";
    /**
     * Field to search in json to get quality gates
     */
    static final String QUALITYGATES = "qualitygates";
    /**
     * Field to search in json to get rules
     */
    static final String RULES = "rules";

    /**
     * Contain all the properties related to requests
     */
    private static Properties requests;

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
     * Static initialization block for reading .properties
     */
    static {
        // store properties
        requests = new Properties();
        // read the file
        InputStream input = null;

        try {
            // load properties file as a stream
            input = AbstractDataProvider.class.getClassLoader().getResourceAsStream(REQUESTS_PROPERTIES);
            if(input!=null) {
                // load properties from the stream in an adapted structure
                requests.load(input);
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            // it logs all the stack trace
            for (StackTraceElement ste: e.getStackTrace()) {
                LOGGER.severe(ste.toString());
            }
        } finally {
            if(input!=null) {
                try {
                    // close the stream if necessary (not null)
                    input.close();
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                    // it logs all the stack trace
                    for (StackTraceElement ste: e.getStackTrace()) {
                        LOGGER.severe(ste.toString());
                    }
                }
            }
        }
    }

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
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to requests.
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    static String getRequest(String property) {
        return requests.getProperty(property);
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
            // log exception's message
            LOGGER.severe(e.getMessage());
            // it logs all the stack trace
            for (StackTraceElement ste: e.getStackTrace()) {
                LOGGER.severe(ste.toString());
            }
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
