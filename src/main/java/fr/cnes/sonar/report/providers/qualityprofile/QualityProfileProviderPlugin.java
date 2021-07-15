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

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.utils.StringManager;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.qualityprofiles.SearchRequest;
import org.sonarqube.ws.client.qualityprofiles.ExportRequest;
import org.sonarqube.ws.client.qualityprofiles.ProjectsRequest;
import org.sonarqube.ws.Qualityprofiles.SearchWsResponse;
import org.sonarqube.ws.Rules.SearchResponse;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides quality gates in plugin mode
 */
public class QualityProfileProviderPlugin extends AbstractQualityProfileProvider implements QualityProfileProvider {

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    public QualityProfileProviderPlugin(final WsClient wsClient, final String project) {
        super(wsClient, project);
    }

    @Override
    public List<QualityProfile> getQualityProfiles() {
        // initializing returned list
        final List<QualityProfile> res = new ArrayList<>();

        // Get all quality profiles (metadata)
        final SearchRequest searchQualityProfilesRequest = new SearchRequest().setProject(getProjectKey());
        // perform the previous request
        final SearchWsResponse searchWsResponse = getWsClient().qualityprofiles().search(searchQualityProfilesRequest);
        JsonObject jo = responseToJsonObject(searchWsResponse);

        // Get quality profiles resources
        final ProfileMetaData[] metaData = (getGson().fromJson(
                jo.get(PROFILES), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaData) {
            final ProfileData profileData = new ProfileData();
            
            // get configuration
            ExportRequest exportRequest = new ExportRequest()
                                                .setLanguage(profileMetaData.getLanguage())
                                                .setQualityProfile(profileMetaData.getName());
            // perform request to sonarqube server
            final String xml = getWsClient().qualityprofiles().export(exportRequest);
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
                // prepare the request
                final List<String> f = new ArrayList<>(Arrays.asList("htmlDesc", "name", "repo", "severity", "defaultRemFn", "actives"));
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
                // convert json to Rule objects
                final Rule [] tmp = (getGson().fromJson(jo.get(RULES), Rule[].class));

                // Redefine the rule's severity, based on the active Quality Profile (not only the default one)
                for (Rule r: tmp) {
                    // If the rule is active in the Quality Profile
                    if(jo.get("actives").getAsJsonObject().has(r.getKey())) {
                        // Retrieve the severity set in the Quality Profile, and override the rule's default severity
                        String severity = jo.get("actives").getAsJsonObject().get(r.getKey()).getAsJsonArray().get(0).getAsJsonObject().get("severity").getAsString();
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

            // get projects linked to the profile
            final ProjectsRequest projectsRequest = new ProjectsRequest().setKey(profileMetaData.getKey());
            // perform a request
            final String projectsResponse = getWsClient().qualityprofiles().projects(projectsRequest);
            // transform response to JsonObject
            jo = getGson().fromJson(projectsResponse, JsonObject.class);
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