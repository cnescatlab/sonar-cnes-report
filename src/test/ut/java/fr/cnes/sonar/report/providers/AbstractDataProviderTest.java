package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.providers.component.*;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.Test;

public class AbstractDataProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test
    public void createAbstractDataProviderStandalone() {
        ComponentProviderStandalone componentProvider = new ComponentProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);

        assertSame(TOKEN, componentProvider.getToken());
        componentProvider.setToken("sqerv1654dr1gsert");
        assertSame("sqerv1654dr1gsert", componentProvider.getToken());

        assertSame(PROJECT_KEY, componentProvider.getProjectKey());
        componentProvider.setProjectKey("test");
        assertSame("test", componentProvider.getProjectKey());

        componentProvider.setQualityGateName(QUALITY_GATE_NAME);
        assertSame(QUALITY_GATE_NAME, componentProvider.getQualityGateName());

        assertSame(BRANCH, componentProvider.getBranch());
        componentProvider.setBranch("develop");
        assertSame("develop", componentProvider.getBranch());
    }

    @Test
    public void createAbstractDataProviderPlugin() {
        ComponentProviderPlugin componentProvider = new ComponentProviderPlugin(wsClient, PROJECT_KEY, BRANCH);

        assertSame(PROJECT_KEY, componentProvider.getProjectKey());
        componentProvider.setProjectKey("test");
        assertSame("test", componentProvider.getProjectKey());

        componentProvider.setQualityGateName(QUALITY_GATE_NAME);
        assertSame(QUALITY_GATE_NAME, componentProvider.getQualityGateName());

        assertSame(BRANCH, componentProvider.getBranch());
        componentProvider.setBranch("develop");
        assertSame("develop", componentProvider.getBranch());
    }

}