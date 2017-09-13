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

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.input.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides quality gates
 * @author lequal
 */
public class QualityProfileProvider extends AbstractDataProvider {

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public QualityProfileProvider(Params params, RequestManager singleton)
            throws UnknownParameterException {
        super(params, singleton);
    }

    /**
     * Get all the quality profiles
     * @return Array containing all the quality profiles of a project
     * @throws IOException when connecting the server
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<QualityProfile> getQualityProfiles()
            throws IOException, BadSonarQubeRequestException {
        // initializing returned list
        final List<QualityProfile> res = new ArrayList<>();

        // Get all quality profiles (metadata)
        String request = String.format(getRequest(GET_QUALITY_PROFILES_REQUEST),
                getUrl(), getProjectKey());
        // perform the previous request
        JsonObject jo = request(request);

        // Get quality profiles resources
        final ProfileMetaData[] metaData = (getGson().fromJson(
                jo.get(PROFILES), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaData) {
            final ProfileData profileData = new ProfileData();
            // get configuration
            request = String.format(getRequest(GET_QUALITY_PROFILES_CONF_REQUEST),
                    getUrl(),
                    profileMetaData.getLanguage().replaceAll(String.valueOf(StringManager.SPACE),
                    StringManager.URI_SPACE),
                    profileMetaData.getName().replaceAll(String.valueOf(StringManager.SPACE),
                    StringManager.URI_SPACE));
            // perform request to sonarqube server
            final String xml = stringRequest(request);
            // add configuration as string to the profile
            profileData.setConf(xml);

            // get the rules of the profile
            // stop condition
            boolean goon = true;
            // page result index
            int page = 1;
            // contain the resulted rules
            final List<Rule> rules = new ArrayList<>();
            // profile's key formatted for url (%20 instead of ' ')
            final String profileKey = profileMetaData.getKey().replaceAll(
                    String.valueOf(StringManager.SPACE),
                    StringManager.URI_SPACE);
            // continue until there are no more results
            while(goon) {
                // prepare the request
                request = String.format(getRequest(GET_QUALITY_PROFILES_RULES_REQUEST),
                        getUrl(), profileKey,
                        Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)), page);
                // perform the previous request to sonarqube server
                jo = request(request);
                // convert json to Rule objects
                final Rule [] tmp = (getGson().fromJson(jo.get(RULES), Rule[].class));
                // add rules to the result list
                rules.addAll(Arrays.asList(tmp));

                // check if there are other pages
                final int number = (jo.get(TOTAL).getAsInt());
                goon = page* Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)) < number;
                page++;
            }
            profileData.setRules(rules);

            // get projects linked to the profile
            request = String.format(getRequest(GET_QUALITY_PROFILES_PROJECTS_REQUEST),
                    getUrl(), profileMetaData.getKey());
            // perform a request
            jo = request(request);
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
