package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import org.junit.Test;

public class ReportModelFactoryTest extends CommonTest {

    @Test(expected = SonarQubeException.class)
    public void createStandaloneTest()
            throws BadSonarQubeRequestException, SonarQubeException, UnknownQualityGateException {
        ReportModelFactory reportModelFactory = new ReportModelFactory(conf.getProject(), conf.getBranch(), conf.getAuthor(), conf.getDate(), standaloneProviderFactory);
        reportModelFactory.create();
    }

    @Test(expected = IllegalStateException.class)
    public void createPluginTest()
            throws BadSonarQubeRequestException, SonarQubeException, UnknownQualityGateException {
        ReportModelFactory reportModelFactory = new ReportModelFactory(conf.getProject(), conf.getBranch(), conf.getAuthor(), conf.getDate(), pluginProviderFactory);
        reportModelFactory.create();
    }
}
