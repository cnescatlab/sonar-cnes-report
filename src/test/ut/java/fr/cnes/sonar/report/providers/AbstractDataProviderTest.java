package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class AbstractDataProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test
    public void createAbstractDataProvider() {
        ComponentProvider componentProvider = new ComponentProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);

        assertTrue(componentProvider.getToken() == TOKEN);
        componentProvider.setToken("sqerv1654dr1gsert");
        assertTrue(componentProvider.getToken() == "sqerv1654dr1gsert");

        assertTrue(componentProvider.getProjectKey() == PROJECT_KEY);
        componentProvider.setProjectKey("test");
        assertTrue(componentProvider.getProjectKey() == "test");

        componentProvider.setQualityGateName(QUALITY_GATE_NAME);
        assertTrue(componentProvider.getQualityGateName() == QUALITY_GATE_NAME);

        assertTrue(componentProvider.getBranch() == BRANCH);
        componentProvider.setBranch("develop");
        assertTrue(componentProvider.getBranch() == "develop");
    }

}