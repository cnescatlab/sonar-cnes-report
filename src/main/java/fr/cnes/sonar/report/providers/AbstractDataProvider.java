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

import com.google.protobuf.Message;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.utils.StringManager;
import org.sonarqube.ws.client.WsClient;
import org.sonar.core.util.ProtobufJsonFormat;

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
     * Name for properties' file about metrics to retrieve
     */
    protected static final String METRICS_PROPERTIES = "metrics.properties";
    /**
     *  Name of the property for the maximum number of results per page
     */
    protected static final String MAX_PER_PAGE_SONARQUBE = "MAX_PER_PAGE_SONARQUBE";
    /**
     *  Name of the request for getting a specific project
     */
    protected static final String GET_PROJECT_REQUEST = "GET_PROJECT_REQUEST";
    /**
     * Field to search in json to get the paging section
     */
    protected static final String PAGING = "paging";
    /**
     * Field to search in json to get the total page's number
     */
    protected static final String TOTAL = "total";
    /**
     * Field to search in json to get rules
     */
    protected static final String RULES = "rules";
    /**
     * Field to search in json to get a key
     */
    protected static final String KEY = "key";

    /**
     * Logger for the class
     */
    protected static final Logger LOGGER = Logger.getLogger(AbstractDataProvider.class.getCanonicalName());

    /**
     * Contain all the properties related to requests
     */
    protected static Properties requestsProperties = new Properties();

    /**
     * Contain all the properties related to metrics to retrieve
     */
    protected static Properties metricsProperties = new Properties();

    /**
     * Tool for parsing json
     */
    protected Gson gson;

    /**
     * SonarQube server
     */
    protected String server;

    /**
     * Token to authenticate the user on the sonarqube server
     */
    protected String token;

    /**
     * Key of the project to report
     */
    protected String projectKey;

    /**
     * The branch of the project
     */
    protected String branch;

    /**
     * Client to talk with sonarqube's services
     */
	protected WsClient wsClient;

    /**
     * Name of the used quality gate
     */
    protected String qualityGateName;

    // Static initialization block for reading .properties
    static {
        // Need of the local classloader to read inner properties file.
        final ClassLoader classLoader = AbstractDataProvider.class.getClassLoader();

        // load requests properties file as a stream
        try (InputStream input = classLoader.getResourceAsStream(REQUESTS_PROPERTIES)){
            if(input!=null) {
                // load properties from the stream in an adapted structure
                requestsProperties.load(input);
            }
        } catch (IOException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        // load metrics properties file as a stream
        try (InputStream input = classLoader.getResourceAsStream(METRICS_PROPERTIES)){
            if(input!=null) {
                // load properties from the stream in an adapted structure
                metricsProperties.load(input);
            }
        } catch (IOException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Constructor.
     * @param server SonarQube server.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractDataProvider(final String server, final String token, final String project, final String branch) {
        // json tool
        this.gson = new Gson();
        // get sonar server
        this.server = server;
        // get user token
        this.token = token;
        // get project key
        this.projectKey = project;
        // get branch
        this.branch = branch;
    }

    /**
     * Constructor.
     * @param server SonarQube server.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     */
    protected AbstractDataProvider(final String server, final String token, final String project) {
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
     * Constructor.
     * @param wsClient The web client
     */
    protected AbstractDataProvider(final WsClient wsClient) {
        // json tool
        this.gson = new Gson();
        // get web client
        this.wsClient = wsClient;
    }

    /**
     * Constructor.
     * @param wsClient The web client
     * @param project The id of the project to report.
     */
    protected AbstractDataProvider(final WsClient wsClient, final String project) {
        // json tool
        this.gson = new Gson();
        // get web client
        this.wsClient = wsClient;
        // get project key
        this.projectKey = project;
    }

    /**
     * Constructor.
     * @param wsClient The web client
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractDataProvider(final WsClient wsClient, final String project, final String branch) {
        // json tool
        this.gson = new Gson();
        // get web client
        this.wsClient = wsClient;
        // get project key
        this.projectKey = project;
        // get branch
        this.branch = branch;
    }

    /**
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to requests.
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    protected String getRequest(final String property) {
        return requestsProperties.getProperty(property);
    }

    /**
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to metrics.
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    protected String getMetrics(final String property) {
        return metricsProperties.getProperty(property);
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
        String preparedRequest = request.replace(" ", "%20");
        // replace + characters
        preparedRequest = preparedRequest.replace("+", "%2B");

        // launch the request on SonarQube server and retrieve resources into a string
        return RequestManager.getInstance().get(preparedRequest, this.token);
    }

    /**
     * Convert a SonarQube API response into a JsonObject
     * @param response the SonarQube API response
     * @return the response as a JsonObject
     */
    protected JsonObject responseToJsonObject(Message response) {
        final String jsonString = ProtobufJsonFormat.toJson(response);
        final JsonElement jsonElement = getGson().fromJson(jsonString, JsonElement.class);
        return jsonElement.getAsJsonObject();
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
    public String getServer() {
        return server;
    }

    /**
     * Setter of server
     * @param pServer value
     */
    public void setServer(final String pServer) {
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
     * Branch of the project to report or "%" if branch was not set (default)
     * @return the project branch as a String
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Setter of branch
     * @param branch value to give
     */
    public void setBranch(final String branch) {
        this.branch = branch;
    }

    /**
     * Client to talk with sonarqube's services
     * @return the web client
     */
    public WsClient getWsClient() {
        return wsClient;
    }

    /**
     * Setter of wsClient
     * @param pWsClient value to give
     */
    public void setWsClient(final WsClient pWsClient) {
        this.wsClient = pWsClient;
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
