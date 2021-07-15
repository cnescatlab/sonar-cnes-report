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
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.cnes.sonar.report.utils.UrlEncoder;

/**
 * Provides quality gates in standalone mode
 */
public class QualityProfileProviderStandalone extends AbstractQualityProfileProvider implements QualityProfileProvider {

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
     * Complete constructor
     * @param pServer SonarQube server..
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     */
    public QualityProfileProviderStandalone(final String pServer, final String pToken, final String pProject) {
        super(pServer, pToken, pProject);
    }

    @Override
    public List<QualityProfile> getQualityProfiles()
            throws BadSonarQubeRequestException, SonarQubeException {
        // initializing returned list
        final List<QualityProfile> res = new ArrayList<>();

        // Get all quality profiles (metadata)
        String request = String.format(getRequest(GET_QUALITY_PROFILES_REQUEST),
                getServer(), getProjectKey());
        // perform the previous request
        JsonObject jo = request(request);

        // Get quality profiles resources
        final ProfileMetaData[] metaData = (getGson().fromJson(
                jo.get(PROFILES), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaData) {
            final ProfileData profileData = new ProfileData();
            
            // get configuration
            // URL Encode Quality Profile & Language name to avoid issue with special characters
            request = String.format(getRequest(GET_QUALITY_PROFILES_CONF_REQUEST),
                    getServer(),
                    UrlEncoder.urlEncodeString(profileMetaData.getLanguage()),
                    UrlEncoder.urlEncodeString(profileMetaData.getName()));
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
            // profile's key formatted for server (%20 instead of ' ')
            final String profileKey = profileMetaData.getKey().replaceAll(
                    String.valueOf(StringManager.SPACE),
                    StringManager.URI_SPACE);
            // continue until there are no more results
            while(goon) {
                // prepare the request
                request = String.format(getRequest(GET_QUALITY_PROFILES_RULES_REQUEST),
                        getServer(), profileKey,
                        Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)), page);
                // perform the previous request to sonarqube server
                jo = request(request);
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
            request = String.format(getRequest(GET_QUALITY_PROFILES_PROJECTS_REQUEST),
                    getServer(), profileMetaData.getKey());
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
