package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.providers.*;
import org.junit.Assert;
import org.junit.Test;

public class ProviderFactoryTest extends CommonTest {

    private static final String TOKEN = "token";
    private static final String PROJECT = "project";

    @Test
    public void createTest() {
        final ProviderFactory providerFactory = new ProviderFactory(sonarQubeServer, TOKEN, PROJECT);
        Assert.assertNotNull(providerFactory);

        AbstractDataProvider provider = providerFactory.create(IssuesProvider.class);
        Assert.assertTrue(provider instanceof IssuesProvider);

        provider = providerFactory.create(MeasureProvider.class);
        Assert.assertTrue(provider instanceof MeasureProvider);

        provider = providerFactory.create(ProjectProvider.class);
        Assert.assertTrue(provider instanceof ProjectProvider);

        provider = providerFactory.create(QualityProfileProvider.class);
        Assert.assertTrue(provider instanceof QualityProfileProvider);

        provider = providerFactory.create(QualityGateProvider.class);
        Assert.assertTrue(provider instanceof QualityGateProvider);

        provider = providerFactory.create(LanguageProvider.class);
        Assert.assertTrue(provider instanceof LanguageProvider);
    }

}
