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
import fr.cnes.sonar.report.utils.UrlEncoder;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * Provides quality gates in standalone mode
 */
public class QualityProfileProviderStandalone extends AbstractQualityProfileProvider implements QualityProfileProvider {

        /**
         * Name of the request for getting quality profiles' linked projects
         */
        private static final String GET_QUALITY_PROFILES_PROJECTS_REQUEST = "GET_QUALITY_PROFILES_PROJECTS_REQUEST";
        /**
         * Name of the request for getting quality profiles' linked rules
         */
        private static final String GET_QUALITY_PROFILES_RULES_REQUEST = "GET_QUALITY_PROFILES_RULES_REQUEST";
        /**
         * Name of the request for getting quality profiles
         */
        private static final String GET_QUALITY_PROFILES_REQUEST = "GET_QUALITY_PROFILES_REQUEST";
        /**
         * Name of the request for getting quality profiles' configuration
         */
        private static final String GET_QUALITY_PROFILES_CONF_REQUEST = "GET_QUALITY_PROFILES_CONFIGURATION_REQUEST";

        /**
         * Complete constructor
         * 
         * @param pServer  SonarQube server..
         * @param pToken   String representing the user token.
         * @param pProject The id of the project to report.
         */
        public QualityProfileProviderStandalone(final String pServer, final String pToken, final String pProject) {
                super(pServer, pToken, pProject);
        }

        @Override
        public List<QualityProfile> getQualityProfiles()
                        throws BadSonarQubeRequestException, SonarQubeException {
                return getQualityProfilesAbstract();
        }

        @Override
        protected JsonObject getQualityProfilesAsJsonObject() throws BadSonarQubeRequestException, SonarQubeException {
                return request(String.format(getRequest(GET_QUALITY_PROFILES_REQUEST), getServer(), getProjectKey()));
        }

        @Override
        protected String getQualityProfilesConfAsXml(final ProfileMetaData profileMetaData)
                        throws BadSonarQubeRequestException, SonarQubeException {
                // URL Encoder is used to avoid issues with special characters
                return stringRequest(String.format(getRequest(GET_QUALITY_PROFILES_CONF_REQUEST), getServer(),
                                UrlEncoder.urlEncodeString(profileMetaData.getLanguage()),
                                UrlEncoder.urlEncodeString(profileMetaData.getName())));
        }

        @Override
        protected JsonObject getQualityProfilesRulesAsJsonObject(final int page, final String profileKey)
                        throws BadSonarQubeRequestException, SonarQubeException {
                return request(String.format(getRequest(GET_QUALITY_PROFILES_RULES_REQUEST), getServer(), profileKey,
                                getMetrics(QP_ISSUES_FIELDS), Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)), page));
        }

        @Override
        protected JsonObject getQualityProfilesProjectsAsJsonObject(final ProfileMetaData profileMetaData)
                        throws BadSonarQubeRequestException, SonarQubeException {
                return request(String.format(getRequest(GET_QUALITY_PROFILES_PROJECTS_REQUEST), getServer(),
                                profileMetaData.getKey()));
        }
}
