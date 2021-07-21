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

package fr.cnes.sonar.report.providers.qualityprofile;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.ProfileData;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.providers.AbstractDataProvider;
import fr.cnes.sonar.report.utils.StringManager;
import fr.cnes.sonar.report.utils.UrlEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.qualityprofiles.ExportRequest;
import org.sonarqube.ws.client.qualityprofiles.ProjectsRequest;
import org.sonarqube.ws.client.qualityprofiles.SearchRequest;
import org.sonarqube.ws.Qualityprofiles.SearchWsResponse;
import org.sonarqube.ws.Rules.SearchResponse;

/**
 * Contains common code for quality profile providers
 */
public abstract class AbstractQualityProfileProvider extends AbstractDataProvider {

    /**
     *  Name of the request for getting quality profiles' linked projects
     */
    private static final String GET_QUALITY_PROFILES_PROJECTS_REQUEST =
            "GET_QUALITY_PROFILES_PROJECTS_REQUEST";
    /**
     *  Name of the request for getting quality profiles' linked rules
     */
    private static final String GET_QUALITY_PROFILES_RULES_REQUEST =
            "GET_QUALITY_PROFILES_RULES_REQUEST";
    /**
     *  Name of the request for getting quality profiles
     */
    private static final String GET_QUALITY_PROFILES_REQUEST = "GET_QUALITY_PROFILES_REQUEST";
    /**
     *  Name of the request for getting quality profiles' configuration
     */
    private static final String GET_QUALITY_PROFILES_CONF_REQUEST =
            "GET_QUALITY_PROFILES_CONFIGURATION_REQUEST";
    /**
     * Field to search in json to get results' values
     */
    private static final String RESULTS = "results";
    /**
     * Field to search in json to get profiles
     */
    private static final String PROFILES = "profiles";
    /**
     * Field to search in json to get profiles
     */
    private static final String ACTIVES = "actives";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractQualityProfileProvider(final String pServer, final String pToken, final String pProject) {
        super(pServer, pToken, pProject);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractQualityProfileProvider(final WsClient wsClient, final String project) {
        super(wsClient, project);
    }

    /**
     * Get all the quality profiles.
     * @param isCalledInStandalone True if the method is called in standalone mode.
     * @return Array containing all the quality profiles of a project.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<QualityProfile> getQualityProfilesAbstract(final boolean isCalledInStandalone) throws BadSonarQubeRequestException, SonarQubeException {
        // initializing returned list
        final List<QualityProfile> res = new ArrayList<>();

        String request;
        JsonObject jo;
        if (isCalledInStandalone) {
            // Get all quality profiles (metadata)
            request = String.format(getRequest(GET_QUALITY_PROFILES_REQUEST),
                    getServer(), getProjectKey());
            // perform the previous request
            jo = request(request);
        } else {
            // Get all quality profiles (metadata)
            final SearchRequest searchQualityProfilesRequest = new SearchRequest().setProject(getProjectKey());
            // perform the previous request
            final SearchWsResponse searchWsResponse = getWsClient().qualityprofiles().search(searchQualityProfilesRequest);
            jo = responseToJsonObject(searchWsResponse);
        }

        // Get quality profiles resources
        final ProfileMetaData[] metaData = (getGson().fromJson(
                jo.get(PROFILES), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaData) {
            final ProfileData profileData = new ProfileData();
            
            String xml;
            if (isCalledInStandalone) {
                // get configuration
                // URL Encode Quality Profile & Language name to avoid issue with special characters
                request = String.format(getRequest(GET_QUALITY_PROFILES_CONF_REQUEST),
                        getServer(),
                        UrlEncoder.urlEncodeString(profileMetaData.getLanguage()),
                        UrlEncoder.urlEncodeString(profileMetaData.getName()));
                // perform request to sonarqube server
                xml = stringRequest(request);
            } else {
                // get configuration
                ExportRequest exportRequest = new ExportRequest()
                                                    .setLanguage(profileMetaData.getLanguage())
                                                    .setQualityProfile(profileMetaData.getName());
                // perform request to sonarqube server
                xml = getWsClient().qualityprofiles().export(exportRequest);
            }
            // add configuration as string to the profile
            profileData.setConf(xml);

            // get the rules of the profile
            // stop condition
            boolean goon = true;
            // page result index
            int page = 1;
            // contain the resulted rules
            final List<Rule> rules = new ArrayList<>();
            // profile's key formatted for server (%20 instead of ' ')
            final String profileKey = profileMetaData.getKey().replaceAll(
                    String.valueOf(StringManager.SPACE),
                    StringManager.URI_SPACE);
            // continue until there are no more results
            while(goon) {
                if (isCalledInStandalone) {
                    // prepare the request
                    request = String.format(getRequest(GET_QUALITY_PROFILES_RULES_REQUEST),
                            getServer(), profileKey, Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)), page);
                    // perform the previous request to sonarqube server
                    jo = request(request);
                } else {
                    // prepare the request
                    final List<String> f = new ArrayList<>(Arrays.asList("htmlDesc", "name", "repo", "severity", "defaultRemFn", ACTIVES));
                    final String ps = String.valueOf(Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)));
                    final String p = String.valueOf(page);
                    final org.sonarqube.ws.client.rules.SearchRequest searchRulesRequest =
                        new org.sonarqube.ws.client.rules.SearchRequest()
                                                            .setQprofile(profileKey)
                                                            .setF(f)
                                                            .setPs(ps)
                                                            .setP(p)
                                                            .setActivation("true");
                    // perform the previous request to sonarqube server
                    final SearchResponse searchRulesResponse = getWsClient().rules().search(searchRulesRequest);
                    // transform response to JsonObject
                    jo = responseToJsonObject(searchRulesResponse);
                }
                // convert json to Rule objects
                final Rule [] tmp = (getGson().fromJson(jo.get(RULES), Rule[].class));

                // Redefine the rule's severity, based on the active Quality Profile (not only the default one)
                for (Rule r: tmp) {
                    // If the rule is active in the Quality Profile
                    if(jo.get(ACTIVES).getAsJsonObject().has(r.getKey())) {
                        // Retrieve the severity set in the Quality Profile, and override the rule's default severity
                        String severity = jo.get(ACTIVES).getAsJsonObject().get(r.getKey()).getAsJsonArray().get(0).getAsJsonObject().get("severity").getAsString();
                        r.setSeverity(severity);
                    }
                }

                // add rules to the result list
                rules.addAll(Arrays.asList(tmp));

                // check if there are other pages
                final int number = (jo.get(TOTAL).getAsInt());
                goon = page* Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)) < number;
                page++;
            }
            profileData.setRules(rules);

            if (isCalledInStandalone) {
                // get projects linked to the profile
                request = String.format(getRequest(GET_QUALITY_PROFILES_PROJECTS_REQUEST),
                        getServer(), profileMetaData.getKey());
                // perform a request
                jo = request(request);
            } else {
                // get projects linked to the profile
                final ProjectsRequest projectsRequest = new ProjectsRequest().setKey(profileMetaData.getKey());
                // perform a request
                final String projectsResponse = getWsClient().qualityprofiles().projects(projectsRequest);
                // transform response to JsonObject
                jo = getGson().fromJson(projectsResponse, JsonObject.class);
            }
            // convert json to Project objects
            final Project[] projects = (getGson().fromJson(jo.get(RESULTS), Project[].class));

            // create and add the new quality profile
            final QualityProfile qualityProfile = new QualityProfile(profileData, profileMetaData);
            qualityProfile.setProjects(projects);
            res.add(qualityProfile);
        }

        return res;
    }
}