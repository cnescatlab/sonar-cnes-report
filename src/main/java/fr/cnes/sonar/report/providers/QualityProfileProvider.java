package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.istack.internal.Nullable;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
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

    private Params params;
    private static final Logger LOGGER = Logger.getLogger(QualityProfileProvider.class.getCanonicalName());

    public QualityProfileProvider(Params params) {
        this.setParams(params);
    }

    /**
     * Get all the quality profiles
     * @return Array containing all the issues
     */
    public List<QualityProfile> getQualityProfiles() throws IOException, UnknownParameterException {
        ArrayList<QualityProfile> res = new ArrayList<>();
        Gson gson = new Gson();
        String url = getParams().get("sonar.url");

        // Get all quality profiles (metadata)
        String request = String.format("%s/api/qualityprofiles/search", url);
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

            // get rules
            boolean goon = true;
            int page = 1;
            List<Rule> rules = new ArrayList<>();
            while(goon) {
                request = String.format("%s/api/rules/search?qprofile=%s&f=htmlDesc,name,repo,severity&ps=%d&p=%d",
                        url, profileMetaData.getKey().replaceAll(" ", "%20"), IDataProvider.MAX_PER_PAGE_SONARQUBE, page);
                raw = RequestManager.getInstance().get(request);
                json = gson.fromJson(raw, JsonElement.class);
                jo = json.getAsJsonObject();
                Rule [] tmp = (gson.fromJson(jo.get("rules"), Rule[].class));
                for (Rule i : tmp) { rules.add(i); }
                int number = (json.getAsJsonObject().get("total").getAsInt());
                goon = page*IDataProvider.MAX_PER_PAGE_SONARQUBE < number;
                page++;
            }
            profileData.setRules(rules);

            // get projects
            request = String.format("%s/api/qualityprofiles/projects?key=%s", url, profileMetaData.getKey());
            raw = RequestManager.getInstance().get(request);
            json = gson.fromJson(raw, JsonElement.class);
            jo = json.getAsJsonObject();
            Project[] projects = (gson.fromJson(jo.get("results"), Project[].class));

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
     * Return the quality gate corresponding to the project
     * @return The Quality Gate
     * @throws IOException A stream exception
     * @throws UnknownParameterException A parameter is not known
     * @throws UnknownQualityProfileException A quality profile is not known
     */
    @Nullable
    public QualityProfile getProjectQualityProfile() throws IOException, UnknownParameterException, UnknownQualityProfileException {
        QualityProfile res = null;
        QualityProfile tmp;
        Boolean find = false;
        List<QualityProfile> qualityProfileList = getQualityProfiles();
        String profileName = getParams().get("sonar.project.quality.profile");
        String projectName = getParams().get("sonar.project.id");

        Iterator<QualityProfile> iterator = qualityProfileList.iterator();
        while (iterator.hasNext() && !find) {
            tmp = iterator.next();
            if(tmp.getName().equals(profileName)) {
                res = tmp;
                find = true;
            }
        }

        if(!find || !checkProfileProjectBinding(res, projectName)) {
            throw new UnknownQualityProfileException(profileName);
        }

        return res;
    }

    /**
     * CHeck that the quality profile is bound to the project
     * @param qualityProfile QualityProfile
     * @param projectName String
     * @return true if the project is bound
     */
    private boolean checkProfileProjectBinding(QualityProfile qualityProfile, String projectName) {
        boolean res = false;

        Project[] list = qualityProfile.getProjects();
        int i = 0;
        while(!res && i < list.length) {
            res = list[i++].getName().equals(projectName);
        }

        return res;
    }
}
