package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic interface for resources providers
 * @author begarco
 */
public abstract class AbstractDataProvider {

    /**
     * Logger for the class
     */
    protected static final Logger LOGGER = Logger.getLogger(AbstractDataProvider.class.getCanonicalName());

    /**
     * Name for properties' file about requests
     */
    public static final String REQUESTS_PROPERTIES = "requests.properties";

    /**
     *  Field to retrieve languages list.
     */
    public static final String GET_LANGUAGES = "GET_LANGUAGES";

    /**
     *  Name of the request for getting quality profiles' linked projects
     */
    public static final String GET_QUALITY_PROFILES_PROJECTS_REQUEST = "GET_QUALITY_PROFILES_PROJECTS_REQUEST";
    /**
     *  Name of the request allowing to retrieve the quality gate
     */
    public static final String GET_QUALITY_GATE_REQUEST = "GET_QUALITY_GATE_REQUEST";
    /**
     *  Name of the request for getting quality gates' details
     */
    public static final String GET_QUALITY_GATES_DETAILS_REQUEST = "GET_QUALITY_GATES_DETAILS_REQUEST";
    /**
     *  Name of the request for getting quality profiles' linked rules
     */
    public static final String GET_QUALITY_PROFILES_RULES_REQUEST = "GET_QUALITY_PROFILES_RULES_REQUEST";
    /**
     *  Name of the request for getting issues
     */
    public static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";
    /**
     *  Name of the request for getting facets
     */
    public static final String GET_FACETS_REQUEST = "GET_FACETS_REQUEST";
    /**
     *  Name of the property for the maximum number of results per page
     */
    public static final String MAX_PER_PAGE_SONARQUBE = "MAX_PER_PAGE_SONARQUBE";
    /**
     *  Name of the request for getting quality gates
     */
    public static final String GET_QUALITY_GATES_REQUEST = "GET_QUALITY_GATES_REQUEST";
    /**
     *  Name of the request for getting measures
     */
    public static final String GET_MEASURES_REQUEST = "GET_MEASURES_REQUEST";
    /**
     *  Name of the request for getting a specific project
     */
    public static final String GET_PROJECT_REQUEST = "GET_PROJECT_REQUEST";
    /**
     *  Name of the request for getting quality profiles
     */
    public static final String GET_QUALITY_PROFILES_REQUEST = "GET_QUALITY_PROFILES_REQUEST";
    /**
     *  Name of the request for getting quality profiles' configuration
     */
    public static final String GET_QUALITY_PROFILES_CONFIGURATION_REQUEST = "GET_QUALITY_PROFILES_CONFIGURATION_REQUEST";
    /**
     * Field to search in json to get results' values
     */
    public static final String RESULTS = "results";
    /**
     * Field to search in json to get profiles
     */
    public static final String PROFILES = "profiles";
    /**
     * Field to search in json to get issues
     */
    public static final String ISSUES = "issues";
    /**
     * Field to search in json to get the total page's number
     */
    public static final String TOTAL = "total";
    /**
     * Field to search in json to get facets
     */
    public static final String FACETS = "facets";
    /**
     * Field to search in json to get the component
     */
    public static final String COMPONENT = "component";
    /**
     * Field to search in json to get measures
     */
    public static final String MEASURES = "measures";
    /**
     * Field to search in json to get the boolean saying if a profile is the default one
     */
    public static final String DEFAULT = "default";
    /**
     * Field to search in json to get quality gates
     */
    public static final String QUALITYGATES = "qualitygates";
    /**
     * Field to search in json to get rules
     */
    public static final String RULES = "rules";



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

    // Static initialization block for reading .properties
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
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if(input!=null) {
                try {
                    // close the stream if necessary (not null)
                    input.close();
                } catch (IOException e) {
                    // it logs all the stack trace
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Singleton which execute concrete http requests
     */
    private RequestManager requestManager;

    /**
     * Constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException when a parameter is not known in the program
     */
    public AbstractDataProvider(Params params, RequestManager singleton) throws UnknownParameterException {
        this.params = params;
        // json tool
        this.gson = new Gson();
        // get sonar url
        this.url = getParams().get("sonar.url");
        // get project key
        this.projectKey = getParams().get("sonar.project.id");
        // set network tool to execute request
        this.requestManager = singleton;
    }

    /**
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to requests.
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    public static String getRequest(String property) {
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
    public JsonObject request(String request) throws IOException, BadSonarQubeRequestException {
        // do the request to the server and return a string answer
        String raw = stringRequest(request);

        // prepare json
        JsonElement json;

        // verify that the server response was correct
        try {
            json = getGson().fromJson(raw, JsonElement.class);
        } catch (Exception e) {
            // log exception's message
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

        // launch the request on sonarqube server and retrieve resources into a string
        return RequestManager.getInstance().get(preparedRequest);
    }

    /**
     * Getter for input
     * @return a Params object
     */
    private Params getParams() {
        return params;
    }

    /**
     * Setter for input
     * @param params the value to give
     */
    private void setParams(Params params) {
        this.params = params;
    }

    /**
     * Json parsing tool
     * @return the gson tool
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Setter of gson
     * @param gson value
     */
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    /**
     * Name of the project to report
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter of url
     * @param url value
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Key of the project to report
     * @return the project key as a String
     */
    public String getProjectKey() {
        return projectKey;
    }

    /**
     * Setter of projectKey
     * @param projectKey value to give
     */
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    /**
     * Quality gate's name (used by the project)
     * @return the name of the quality gate as a string
     */
    public String getQualityGateName() {
        return qualityGateName;
    }

    /**
     * Setter of qualityGateName
     * @param qualityGateName value
     */
    public void setQualityGateName(String qualityGateName) {
        this.qualityGateName = qualityGateName;
    }
}
