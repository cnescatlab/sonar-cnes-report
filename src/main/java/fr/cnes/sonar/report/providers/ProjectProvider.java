
package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.ProfileMetaData;
import fr.cnes.sonar.report.model.Project;

import java.io.IOException;

/**
 * Provides basic project's information
 * @author lequal
 */
public class ProjectProvider extends AbstractDataProvider {

    private LanguageProvider languageProvider;

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public ProjectProvider(Params params, RequestManager singleton) throws UnknownParameterException {
        super(params, singleton);
        languageProvider = new LanguageProvider(params, singleton);
    }

    /**
     * Get the project corresponding to the given key
     * @param projectKey the key of the project
     * @return A simple project
     * @throws IOException when contacting the server
     * @throws BadSonarQubeRequestException when the server does not understand the request
     */
    public Project getProject(String projectKey) throws IOException, BadSonarQubeRequestException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        JsonObject jo = request(String.format(getRequest(GET_PROJECT_REQUEST),
                getUrl(), projectKey));

        // put json in a Project class
        Project project = (getGson().fromJson(jo, Project.class));

        // set language's name for profiles
        ProfileMetaData[] metaData = project.getQualityProfiles();
        String languageName;
        for(ProfileMetaData it : metaData){
            languageName = languageProvider.getLanguage(it.getLanguage());
            it.setLanguageName(languageName);
        }
        project.setQualityProfiles(metaData);

        // check description nullity
        if(null==project.getDescription()) {
            project.setDescription(StringManager.EMPTY);
        }

        return project;
    }
}
