package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.providers.language.*;
import fr.cnes.sonar.report.providers.project.*;

import org.junit.Test;

public class ProjectProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetProject() throws SonarQubeException, BadSonarQubeRequestException {
        LanguageProvider languageProvider = new LanguageProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY);
        ProjectProvider projectProvider = new ProjectProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, languageProvider);
        projectProvider.getProject(PROJECT_KEY, BRANCH);
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyHasProject() throws SonarQubeException, BadSonarQubeRequestException {
        LanguageProvider languageProvider = new LanguageProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY);
        ProjectProvider projectProvider = new ProjectProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, languageProvider);
        projectProvider.hasProject(PROJECT_KEY, BRANCH);
    }
}