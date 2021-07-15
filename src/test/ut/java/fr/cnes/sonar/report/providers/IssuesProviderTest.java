package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.providers.issues.*;

import org.junit.Test;

public class IssuesProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetIssues() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetFacets() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getFacets();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetRawIssues() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getRawIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetUnconfirmedIssues() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getUnconfirmedIssues();
    }
}