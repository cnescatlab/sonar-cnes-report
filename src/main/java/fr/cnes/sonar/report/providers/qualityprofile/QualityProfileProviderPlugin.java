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
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.QualityProfile;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.qualityprofiles.ExportRequest;
import org.sonarqube.ws.client.qualityprofiles.ProjectsRequest;
import org.sonarqube.ws.client.qualityprofiles.SearchRequest;
import org.sonarqube.ws.Qualityprofiles.SearchWsResponse;
import org.sonarqube.ws.Rules.SearchResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

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
    public List<QualityProfile> getQualityProfiles() throws BadSonarQubeRequestException, SonarQubeException {
        return getQualityProfilesAbstract();
    }

    @Override
    protected JsonObject getQualityProfilesAsJsonObject() {
        // Get all quality profiles (metadata)
        final SearchRequest searchQualityProfilesRequest = new SearchRequest().setProject(getProjectKey());
        // perform the previous request
        final SearchWsResponse searchWsResponse = getWsClient().qualityprofiles().search(searchQualityProfilesRequest);
        return responseToJsonObject(searchWsResponse);
    }

    @Override
    protected String getQualityProfilesConfAsXml(final ProfileMetaData profileMetaData) {
        // get configuration
        ExportRequest exportRequest = new ExportRequest()
                                            .setLanguage(profileMetaData.getLanguage())
                                            .setQualityProfile(profileMetaData.getName());
        // perform request to sonarqube server
        return getWsClient().qualityprofiles().export(exportRequest);
    }

    @Override
    protected JsonObject getQualityProfilesRulesAsJsonObject(final int page, final String profileKey) {
        // prepare the request
        final List<String> issueFieldList = new ArrayList<>(Arrays.asList(getMetrics(QP_ISSUES_FIELDS).split(",")));
        final String maxPerPage = String.valueOf(Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)));
        final String pageIndex = String.valueOf(page);
        final org.sonarqube.ws.client.rules.SearchRequest searchRulesRequest =
                new org.sonarqube.ws.client.rules.SearchRequest()
                                                    .setQprofile(profileKey)
                                                    .setF(issueFieldList)
                                                    .setPs(maxPerPage)
                                                    .setP(pageIndex)
                                                    .setActivation("true");
        // perform the previous request to sonarqube server
        final SearchResponse searchRulesResponse = getWsClient().rules().search(searchRulesRequest);
        // transform response to JsonObject
        return responseToJsonObject(searchRulesResponse);
    }

    @Override
    protected JsonObject getQualityProfilesProjectsAsJsonObject(final ProfileMetaData profileMetaData) {
        // get projects linked to the profile
        final ProjectsRequest projectsRequest = new ProjectsRequest().setKey(profileMetaData.getKey());
        // perform a request
        final String projectsResponse = getWsClient().qualityprofiles().projects(projectsRequest);
        // transform response to JsonObject
        return getGson().fromJson(projectsResponse, JsonObject.class);
    }
}