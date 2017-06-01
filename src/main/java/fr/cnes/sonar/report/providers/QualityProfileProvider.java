package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides quality gates
 * @author begarco
 */
public class QualityProfileProvider extends AbstractDataProvider {

    /**
     * Complete constructor
     * @param params Program's parameters
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public QualityProfileProvider(Params params) throws UnknownParameterException {
        super(params);
    }

    /**
     * Get all the quality profiles
     * @return Array containing all the quality profiles of a project
     * @throws IOException when connecting the server
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<QualityProfile> getQualityProfiles() throws IOException, BadSonarQubeRequestException {
        // initializing returned list
        ArrayList<QualityProfile> res = new ArrayList<>();

        // Get all quality profiles (metadata)
        String request = String.format(GET_QUALITY_PROFILES_REQUEST, getUrl(), getProjectKey());
        // perform the previous request
        JsonObject jo = request(request);

        // Get quality profiles data
        ProfileMetaData[] metaData = (getGson().fromJson(jo.get("profiles"), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaData) {
            ProfileData profileData = new ProfileData();
            // get configuration
            request = String.format(GET_QUALITY_PROFILES_CONFIGURATION_REQUEST,
                    getUrl(), profileMetaData.getLanguage().replaceAll(" ", "%20"), profileMetaData.getName().replaceAll(" ", "%20"));
            // perform request to sonarqube server
            String xml = stringRequest(request);
            // add configuration as string to the profile
            profileData.setConf(xml);

            // get the rules of the profile
            // stop condition
            boolean goon = true;
            // page result index
            int page = 1;
            // contain the resulted rules
            List<Rule> rules = new ArrayList<>();
            // continue until there are no more results
            while(goon) {
                // prepare the request
                request = String.format(GET_QUALITY_PROFILES_RULES_REQUEST,
                        getUrl(), profileMetaData.getKey().replaceAll(" ", "%20"), AbstractDataProvider.MAX_PER_PAGE_SONARQUBE, page);
                // perform the previous request to sonarqube server
                jo = request(request);
                // convert json to Rule objects
                Rule [] tmp = (getGson().fromJson(jo.get("rules"), Rule[].class));
                // add rules to the result list
                rules.addAll(Arrays.asList(tmp));

                // check if there are other pages
                int number = (jo.get("total").getAsInt());
                goon = page* AbstractDataProvider.MAX_PER_PAGE_SONARQUBE < number;
                page++;
            }
            profileData.setRules(rules);

            // get projects linked to the profile
            request = String.format(AbstractDataProvider.GET_QUALITY_PROFILES_PROJECTS_REQUEST, getUrl(), profileMetaData.getKey());
            // perform a request
            jo = request(request);
            // convert json to Project objects
            Project[] projects = (getGson().fromJson(jo.get("results"), Project[].class));

            // create and add the new quality profile
            QualityProfile qualityProfile = new QualityProfile(profileData, profileMetaData);
            qualityProfile.setProjects(projects);
            res.add(qualityProfile);
        }

        return res;
    }
}
