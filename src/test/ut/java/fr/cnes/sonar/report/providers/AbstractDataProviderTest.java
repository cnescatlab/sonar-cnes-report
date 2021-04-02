package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.Test;

public class AbstractDataProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test
    public void createAbstractDataProvider() {
        ComponentProvider componentProvider = new ComponentProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);

        assertSame(componentProvider.getToken(), TOKEN);
        componentProvider.setToken("sqerv1654dr1gsert");
        assertSame(componentProvider.getToken(), "sqerv1654dr1gsert");

        assertSame(componentProvider.getProjectKey(), PROJECT_KEY);
        componentProvider.setProjectKey("test");
        assertSame(componentProvider.getProjectKey(), "test");

        componentProvider.setQualityGateName(QUALITY_GATE_NAME);
        assertSame(componentProvider.getQualityGateName(), QUALITY_GATE_NAME);

        assertSame(componentProvider.getBranch(), BRANCH);
        componentProvider.setBranch("develop");
        assertSame(componentProvider.getBranch(), "develop");
    }

}