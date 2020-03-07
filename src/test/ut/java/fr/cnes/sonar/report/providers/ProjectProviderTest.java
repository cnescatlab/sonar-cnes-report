package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class ProjectProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetProject() throws SonarQubeException, BadSonarQubeRequestException {
        ProjectProvider projectProvider = new ProjectProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        projectProvider.getProject(PROJECT_KEY, BRANCH);
    }

}