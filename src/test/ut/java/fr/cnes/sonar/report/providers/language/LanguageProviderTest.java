package fr.cnes.sonar.report.providers.language;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class LanguageProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetLanguagesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        LanguageProvider languageProvider = new LanguageProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY);
        languageProvider.getLanguages();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetLanguagesPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        LanguageProvider languageProvider = new LanguageProviderPlugin(wsClient, PROJECT_KEY);
        languageProvider.getLanguages();
    }
}