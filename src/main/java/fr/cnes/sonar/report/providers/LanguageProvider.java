
package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.model.Language;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides languages
 * @author lequal
 */
public class LanguageProvider extends AbstractDataProvider {

    /**
     * Json's field containing the language's array
     */
    private static final String LANGUAGES_FIELD = "languages";

    private Map<String, Language> languages;

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public LanguageProvider(Params params, RequestManager singleton) throws UnknownParameterException {
        super(params, singleton);
        languages = new HashMap<>();
    }

    /**
     * Get the language corresponding to the given key
     * @param languageKey the key of the language
     * @return Language's name
     * @throws IOException when contacting the server
     * @throws BadSonarQubeRequestException when the server does not understand the request
     */
    public String getLanguage(final String languageKey) throws IOException, BadSonarQubeRequestException {
        if(languages.isEmpty()){
            this.getLanguages();
        }
        return this.languages.get(languageKey).getName();
    }

    /**
     * Get all the languages of SonarQube
     * @return a map with all the languages
     * @throws IOException when contacting the server
     * @throws BadSonarQubeRequestException when the server does not understand the request
     */
    public Map<String, Language> getLanguages() throws IOException, BadSonarQubeRequestException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception
        JsonObject jo = request(String.format(getRequest(GET_LANGUAGES),
                getUrl()));
        Language[] languagesList = getGson().fromJson(jo.get(LANGUAGES_FIELD), Language[].class);

        // put data in a map to access it faster
        this.languages = new HashMap<>();
        for(Language language : languagesList){
            this.languages.put(language.getKey(), language);
        }

        return this.languages;
    }


}
