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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for quality profile providers
 */
public abstract class AbstractQualityProfileProvider extends AbstractDataProvider {

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
     *  Name of the SonarQube issues fields to retrieve 
     */
    protected static final String QP_ISSUES_FIELDS = "QP_ISSUES_FIELDS";

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
     * @return Array containing all the quality profiles of a project.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected List<QualityProfile> getQualityProfilesAbstract() throws BadSonarQubeRequestException, SonarQubeException {
        // initializing returned list
        final List<QualityProfile> res = new ArrayList<>();

        JsonObject jo = getQualityProfilesAsJsonObject();

        // Get quality profiles resources
        final ProfileMetaData[] metaData = (getGson().fromJson(
                jo.get(PROFILES), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaData) {
            final ProfileData profileData = new ProfileData();
            
            final String xml = getQualityProfilesConfAsXml(profileMetaData);
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
                jo = getQualityProfilesRulesAsJsonObject(page, profileKey);
                // convert json to Rule objects
                final Rule [] tmp = (getGson().fromJson(jo.get(RULES), Rule[].class));

                // Redefine the rule's severity, based on the active Quality Profile (not only the default one)
                for (Rule r: tmp) {
                    // If the rule is active in the Quality Profile
                    if(jo.get(ACTIVES).getAsJsonObject().has(r.getKey())) {
                        // Retrieve the severity set in the Quality Profile, and override the rule's default severity
                        String severity = jo.get(ACTIVES).getAsJsonObject().get(r.getKey()).getAsJsonArray().get(0)
                                .getAsJsonObject().get("severity").getAsString();
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

            jo = getQualityProfilesProjectsAsJsonObject(profileMetaData);
            // convert json to Project objects
            final Project[] projects = (getGson().fromJson(jo.get(RESULTS), Project[].class));

            // create and add the new quality profile
            final QualityProfile qualityProfile = new QualityProfile(profileData, profileMetaData);
            qualityProfile.setProjects(projects);
            res.add(qualityProfile);
        }

        return res;
    }

    /**
     * Get a JsonObject from the response of a search quality profiles request.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getQualityProfilesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException;

    /**
     * Get a XML String from the response of an export quality profiles request.
     * @param profileMetaData The quality profile metadata.
     * @return The response as a String.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract String getQualityProfilesConfAsXml(final ProfileMetaData profileMetaData)
            throws BadSonarQubeRequestException, SonarQubeException;

    /**
     * Get a JsonObject from the response of a search rules request.
     * @param page The current page.
     * @param profileKey The key of the quality profile.
     * @return The response as a JsonObject.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getQualityProfilesRulesAsJsonObject(final int page, final String profileKey)
            throws BadSonarQubeRequestException, SonarQubeException;

    /**
     * Get a JsonObject from the response of a get quality profiles projects request.
     * @param profileMetaData The quality profile metadata.
     * @return The response as a String.
     * @throws BadSonarQubeRequestException A request is not recognized by the server.
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    protected abstract JsonObject getQualityProfilesProjectsAsJsonObject(final ProfileMetaData profileMetaData)
            throws BadSonarQubeRequestException, SonarQubeException;
}