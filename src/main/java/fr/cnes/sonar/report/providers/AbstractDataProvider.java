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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.StringManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic interface for resources providers
 */
public abstract class AbstractDataProvider {

    /**
     * Name for properties' file about requests
     */
    protected static final String REQUESTS_PROPERTIES = "requests.properties";
    /**
     *  Field to retrieve languages list.
     */
    protected static final String GET_LANGUAGES = "GET_LANGUAGES";
    /**
     *  Name of the request for getting quality profiles' linked projects
     */
    protected static final String GET_QUALITY_PROFILES_PROJECTS_REQUEST =
            "GET_QUALITY_PROFILES_PROJECTS_REQUEST";
    /**
     *  Name of the request for getting project's quality profiles
     */
    public static final String GET_PROJECT_QUALITY_PROFILES_REQUEST =
            "GET_PROJECT_QUALITY_PROFILES_REQUEST";
    /**
     *  Name of the request allowing to retrieve the quality gate
     */
    protected static final String GET_QUALITY_GATE_REQUEST = "GET_QUALITY_GATE_REQUEST";
    /**
     *  Name of the request allowing to retrieve the projects linked to quality gate
     */
    public static final String QUALITY_GATE_PROJECTS_REQUEST = "QUALITY_GATE_PROJECTS_REQUEST";
    /**
     *  Name of the request for getting quality gates' details
     */
    protected static final String GET_QUALITY_GATES_DETAILS_REQUEST =
            "GET_QUALITY_GATES_DETAILS_REQUEST";
    /**
     *  Name of the request for getting quality profiles' linked rules
     */
    protected static final String GET_QUALITY_PROFILES_RULES_REQUEST =
            "GET_QUALITY_PROFILES_RULES_REQUEST";
    /**
     *  Name of the request for getting issues
     */
    protected static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";
    /**
     *  Name of the request for getting facets
     */
    protected static final String GET_FACETS_REQUEST = "GET_FACETS_REQUEST";
    /**
     *  Name of the property for the maximum number of results per page
     */
    protected static final String MAX_PER_PAGE_SONARQUBE = "MAX_PER_PAGE_SONARQUBE";
    /**
     *  Name of the request for getting quality gates
     */
    protected static final String GET_QUALITY_GATES_REQUEST = "GET_QUALITY_GATES_REQUEST";
    /**
     *  Name of the request for getting measures
     */
    protected static final String GET_MEASURES_REQUEST = "GET_MEASURES_REQUEST";
    /**
     *  Name of the request for getting components
     */
    protected static final String GET_COMPONENTS_REQUEST = "GET_COMPONENTS_REQUEST";
    /**
     *  Name of the request for getting a specific project
     */
    protected static final String GET_PROJECT_REQUEST = "GET_PROJECT_REQUEST";
    /**
     *  Name of the request for getting quality profiles
     */
    protected static final String GET_QUALITY_PROFILES_REQUEST = "GET_QUALITY_PROFILES_REQUEST";
    /**
     *  Name of the request for getting quality profiles' configuration
     */
    protected static final String GET_QUALITY_PROFILES_CONF_REQUEST =
            "GET_QUALITY_PROFILES_CONFIGURATION_REQUEST";
    /**
     *  Name of the request for getting SonarQube server information
     */
    protected static final String GET_SONARQUBE_INFO_REQUEST =
            "GET_SONARQUBE_INFO_REQUEST";
    /**
     * Field to search in json to get results' values
     */
    protected static final String RESULTS = "results";
    /**
     * Field to search in json to get profiles
     */
    protected static final String PROFILES = "profiles";
    /**
     * Field to search in json to get issues
     */
    protected static final String ISSUES = "issues";
    /**
     * Field to search in json to get the paging section
     */
    protected static final String PAGING = "paging";
    /**
     * Field to search in json to get the total page's number
     */
    protected static final String TOTAL = "total";
    /**
     * Field to search in json to get facets
     */
    protected static final String FACETS = "facets";
    /**
     * Field to search in json to get the component
     */
    protected static final String COMPONENT = "component";
    /**
     * Field to search in json to get measures
     */
    protected static final String MEASURES = "measures";
    /**
     * Field to search in json to get the boolean saying if a profile is the default one
     */
    protected static final String DEFAULT = "default";
    /**
     * Field to search in json to get quality gates
     */
    protected static final String QUALITYGATES = "qualitygates";
    /**
     * Field to search in json to get rules
     */
    protected static final String RULES = "rules";
    /**
     * Field to search in json to get components
     */
    protected static final String COMPONENTS = "components";

    /**
     * Logger for the class
     */
    protected static final Logger LOGGER = Logger.getLogger(AbstractDataProvider.class.getCanonicalName());

    /**
     * Contain all the properties related to requests
     */
    protected static Properties requests = new Properties();

    /**
     * Tool for parsing json
     */
    protected Gson gson;

    /**
     * SonarQube server
     */
    protected SonarQubeServer server;

    /**
     * Token to authenticate the user on the sonarqube server
     */
    protected String token;

    /**
     * Key of the project to report
     */
    protected String projectKey;

    /**
     * Name of the used quality gate
     */
    protected String qualityGateName;

    // Static initialization block for reading .properties
    static {
        // Need of the local classloader to read inner properties file.
        final ClassLoader classLoader = AbstractDataProvider.class.getClassLoader();

        // load properties file as a stream
        try (InputStream input = classLoader.getResourceAsStream(REQUESTS_PROPERTIES)){
            if(input!=null) {
                // load properties from the stream in an adapted structure
                requests.load(input);
            }
        } catch (IOException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Constructor.
     *  @param server SonarQube server.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     */
    AbstractDataProvider(final SonarQubeServer server, final String token, final String project) {
        // json tool
        this.gson = new Gson();
        // get sonar server
        this.server = server;
        // get user token
        this.token = token;
        // get project key
        this.projectKey = project;
    }

    /**
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to requests.
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    static String getRequest(final String property) {
        return requests.getProperty(property);
    }

    /**
     * Check if the server has sent an error
     * @param jsonObject The response from the server
     * @throws BadSonarQubeRequestException thrown if the server do not understand our request
     */
    private void isErrorFree(final JsonObject jsonObject) throws BadSonarQubeRequestException {
        // we retrieve the exception
        final JsonElement error = jsonObject.get("errors");
        // if there is an error we search the message and throw an exception
        if (error != null) {
            // Json object of the error
            final JsonObject errorJO = error.getAsJsonArray().get(0).getAsJsonObject();
            // get the error message
            final JsonElement errorElement = errorJO.get("msg");
            final String errorMessage = (getGson().fromJson(errorElement, String.class));
            // throw exception if there was a problem when dealing with the server
            throw new BadSonarQubeRequestException(errorMessage);
        }
    }

    /**
     * Execute a given request
     * @param request Url for the request, for example http://sonarqube:1234/api/toto/list
     * @return Server's response as a JsonObject
     * @throws BadSonarQubeRequestException if SonarQube Server sent an error
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public JsonObject request(final String request)
            throws BadSonarQubeRequestException, SonarQubeException {
        // do the request to the server and return a string answer
        final String raw = stringRequest(request);

        // prepare json
        final JsonElement json;

        // verify that the server response was correct
        try {
            json = getGson().fromJson(raw, JsonElement.class);
        } catch (Exception e) {
            // log exception's message
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new BadSonarQubeRequestException("Server answered: " + raw +
                    StringManager.SPACE + e.getMessage());
        }

        // get the json object version
        final JsonObject jsonObject;

        try {
            jsonObject = json.getAsJsonObject();
        } catch (NullPointerException e) {
            throw new BadSonarQubeRequestException("Empty server response, reason might be : " +
                    "server certificate not in JRE/JDK truststore, ...");
        }

        // verify if an error occurred
        isErrorFree(jsonObject);

        return jsonObject;
    }

    /**
     * Get the raw string response
     * @param request the raw server of the request
     * @return the server's response as a string
     * @throws SonarQubeException When SonarQube server is not callable.
     * @throws BadSonarQubeRequestException if SonarQube Server sent an error
     */
    protected String stringRequest(final String request) throws SonarQubeException, BadSonarQubeRequestException {
        // prepare the request by replacing some relevant special characters
        // replace spaces
        String preparedRequest = request.replaceAll(" ", "%20");
        // replace + characters
        preparedRequest = preparedRequest.replaceAll("\\+", "%2B");

        // launch the request on SonarQube server and retrieve resources into a string
        return RequestManager.getInstance().get(preparedRequest, this.token);
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
     * @param pGson value
     */
    public void setGson(final Gson pGson) {
        this.gson = pGson;
    }

    /**
     * SonarQube instance
     * @return the server
     */
    public SonarQubeServer getServer() {
        return server;
    }

    /**
     * Setter of server
     * @param pServer value
     */
    public void setServer(final SonarQubeServer pServer) {
        this.server = pServer;
    }

    /**
     * Token to authenticate the user
     * @return the user token
     */
    public String getToken() {
        return token;
    }

    /**
     * Setter of token
     * @param pToken value
     */
    public void setToken(final String pToken) {
        this.token = pToken;
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
     * @param pProjectKey value to give
     */
    public void setProjectKey(final String pProjectKey) {
        this.projectKey = pProjectKey;
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
     * @param pQualityGateName value
     */
    public void setQualityGateName(final String pQualityGateName) {
        this.qualityGateName = pQualityGateName;
    }
}
