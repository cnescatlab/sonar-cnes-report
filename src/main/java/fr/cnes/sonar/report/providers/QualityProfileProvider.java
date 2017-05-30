package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityProfileException;
import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides quality gates
 * @author begarco
 */
public class QualityProfileProvider implements IDataProvider {

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = Logger.getLogger(QualityProfileProvider.class.getCanonicalName());

    /**
     * Params of the program itself
     */
    private Params params;

    public QualityProfileProvider(Params params) {
        this.setParams(params);
    }

    /**
     * Get all the quality profiles
     * @return Array containing all the quality profiles of a project
     */
    public List<QualityProfile> getQualityProfiles() throws IOException, UnknownParameterException {
        // initializing returned list
        ArrayList<QualityProfile> res = new ArrayList<>();
        // used to parse json
        Gson gson = new Gson();
        // get parameters
        String url = getParams().get("sonar.url");
        String projectKey = getParams().get("sonar.project.id");

        // Get all quality profiles (metadata)
        String request = String.format("%s/api/qualityprofiles/search?projectKey=%s", url, projectKey);
        String raw = RequestManager.getInstance().get(request);
        JsonElement json = gson.fromJson(raw, JsonElement.class);
        JsonObject jo = json.getAsJsonObject();

        // Get quality profiles data
        ProfileMetaData[] metaDatas = (gson.fromJson(jo.get("profiles"), ProfileMetaData[].class));
        for (ProfileMetaData profileMetaData : metaDatas) {
            ProfileData profileData = new ProfileData();
            // get configuration
            request = String.format("%s/api/qualityprofiles/export?language=%s&name=%s",
                    url, profileMetaData.getLanguage().replaceAll(" ", "%20"), profileMetaData.getName().replaceAll(" ", "%20"));
            raw = RequestManager.getInstance().get(request);
            profileData.setConf(raw);

            // get the rules of the profile
            // stop condition
            boolean goon = true;
            // page result index
            int page = 1;
            // contain the resulted rules
            List<Rule> rules = new ArrayList<>();
            // continue until there are no more results
            while(goon) {
                request = String.format("%s/api/rules/search?qprofile=%s&f=htmlDesc,name,repo,severity&ps=%d&p=%d",
                        url, profileMetaData.getKey().replaceAll(" ", "%20"), IDataProvider.MAX_PER_PAGE_SONARQUBE, page);
                raw = RequestManager.getInstance().get(request);
                json = gson.fromJson(raw, JsonElement.class);
                jo = json.getAsJsonObject();
                Rule [] tmp = (gson.fromJson(jo.get("rules"), Rule[].class));
                rules.addAll(Arrays.asList(tmp));

                // check if there are other pages
                int number = (json.getAsJsonObject().get("total").getAsInt());
                goon = page*IDataProvider.MAX_PER_PAGE_SONARQUBE < number;
                page++;
            }
            profileData.setRules(rules);

            // get projects linked to the profile
            request = String.format("%s/api/qualityprofiles/projects?key=%s", url, profileMetaData.getKey());
            raw = RequestManager.getInstance().get(request);
            json = gson.fromJson(raw, JsonElement.class);
            jo = json.getAsJsonObject();
            Project[] projects = (gson.fromJson(jo.get("results"), Project[].class));

            // create and add the new quality profile
            QualityProfile qualityProfile = new QualityProfile(profileData, profileMetaData);
            qualityProfile.setProjects(projects);
            res.add(qualityProfile);
        }

        return res;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    /**
     * Return the quality profile corresponding to the project
     * @return The Quality Profile
     * @throws IOException A stream exception
     * @throws UnknownParameterException A parameter is not known
     * @throws UnknownQualityProfileException A quality profile is not known
     */
    public QualityProfile getProjectQualityProfile() throws IOException, UnknownParameterException, UnknownQualityProfileException {
        // final result
        QualityProfile res = null;
        // true if the quality profile is found
        Boolean find = false;
        // get the quality profiles list
        List<QualityProfile> qualityProfileList = getQualityProfiles();
        // get data from parameters
        String profileName = getParams().get("sonar.project.quality.profile");
        String projectName = getParams().get("sonar.project.id");

        // look for a quality profile whose name is profileName
        Iterator<QualityProfile> iterator = qualityProfileList.iterator();
        while (iterator.hasNext() && !find) {
            QualityProfile tmp = iterator.next();
            if(tmp.getName().equals(profileName)) {
                res = tmp;
                find = true;
            }
        }

        boolean bool1 = !find;
        boolean bool2 = !checkProfileProjectBinding(res, projectName);

        // check if the result is correct
        if(!find || !checkProfileProjectBinding(res, projectName)) {
            throw new UnknownQualityProfileException(profileName);
        }

        return res;
    }

    /**
     * Check that the quality profile is bound to the project
     * @param qualityProfile QualityProfile
     * @param projectName String
     * @return true if the project is bound
     */
    private boolean checkProfileProjectBinding(QualityProfile qualityProfile, String projectName) {
        // result initialized to false
        boolean res = false;

        // get the profiles
        Project[] list = qualityProfile.getProjects();
        int i = 0;
        while(!res && i < list.length) {
            // check if each project has the good key
            res = list[i++].getKey().equals(projectName);
        }
        // res == true if we go out of the while before the end
        return res;
    }
}
