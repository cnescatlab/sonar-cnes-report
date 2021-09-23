package fr.cnes.sonar.report.providers.issues;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class IssuesProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetIssuesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetRawIssuesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getRawIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetUnconfirmedIssuesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getUnconfirmedIssues();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetIssuesPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        issuesProvider.getIssues();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetRawIssuesPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        issuesProvider.getRawIssues();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetUnconfirmedIssuesPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderPlugin(wsClient, PROJECT_KEY, BRANCH);
        issuesProvider.getUnconfirmedIssues();
    }
}