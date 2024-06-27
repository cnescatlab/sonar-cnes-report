package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.providers.facets.*;
import fr.cnes.sonar.report.providers.issues.*;
import fr.cnes.sonar.report.providers.measure.*;
import fr.cnes.sonar.report.providers.project.*;
import fr.cnes.sonar.report.providers.qualityprofile.*;
import fr.cnes.sonar.report.providers.qualitygate.*;
import fr.cnes.sonar.report.providers.language.*;
import fr.cnes.sonar.report.providers.component.*;
import fr.cnes.sonar.report.providers.securityhotspots.*;
import fr.cnes.sonar.report.providers.sonarqubeinfo.*;
import org.junit.Assert;
import org.junit.Test;

public class ProviderFactoryTest extends CommonTest {

    @Test
    public void createStandaloneTest() {

        FacetsProvider facetsProvider = standaloneProviderFactory.createFacetsProvider();
        Assert.assertTrue(facetsProvider instanceof FacetsProviderStandalone);

        IssuesProvider issuesProvider = standaloneProviderFactory.createIssuesProvider();
        Assert.assertTrue(issuesProvider instanceof IssuesProviderStandalone);

        MeasureProvider measureProvider = standaloneProviderFactory.createMeasureProvider();
        Assert.assertTrue(measureProvider instanceof MeasureProviderStandalone);

        ProjectProvider projectProvider = standaloneProviderFactory.createProjectProvider();
        Assert.assertTrue(projectProvider instanceof ProjectProviderStandalone);

        QualityProfileProvider qualityProfileProvider = standaloneProviderFactory.createQualityProfileProvider();
        Assert.assertTrue(qualityProfileProvider instanceof QualityProfileProviderStandalone);

        QualityGateProvider qualityGateProvider = standaloneProviderFactory.createQualityGateProvider();
        Assert.assertTrue(qualityGateProvider instanceof QualityGateProviderStandalone);

        LanguageProvider languageProvider = standaloneProviderFactory.createLanguageProvider();
        Assert.assertTrue(languageProvider instanceof LanguageProviderStandalone);

        ComponentProvider componentProvider = standaloneProviderFactory.createComponentProvider();
        Assert.assertTrue(componentProvider instanceof ComponentProviderStandalone);

        SecurityHotspotsProvider securityHotspotsProvider = standaloneProviderFactory.createSecurityHotspotsProvider();
        Assert.assertTrue(securityHotspotsProvider instanceof SecurityHotspotsProviderStandalone);

        SonarQubeInfoProvider sonarQubeInfoProvider = standaloneProviderFactory.createSonarQubeInfoProvider();
        Assert.assertTrue(sonarQubeInfoProvider instanceof SonarQubeInfoProviderStandalone);
    }

}
