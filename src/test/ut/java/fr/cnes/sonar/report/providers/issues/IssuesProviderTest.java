package fr.cnes.sonar.report.providers.issues;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class IssuesProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetIssuesStandalone() throws Exception, SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, ENABLE_ISSUES_MULTI_REQUESTS, MAX_URL_SIZE);
        issuesProvider.getIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetRawIssuesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, ENABLE_ISSUES_MULTI_REQUESTS, MAX_URL_SIZE);
        issuesProvider.getRawIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetUnconfirmedIssuesStandalone() throws SonarQubeException, BadSonarQubeRequestException, Exception {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH, ENABLE_ISSUES_MULTI_REQUESTS, MAX_URL_SIZE);
        issuesProvider.getUnconfirmedIssues();
    }


}