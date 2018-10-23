package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.SonarQubeServer;
import org.junit.Test;

public class ReportModelFactoryTest extends CommonTest {

    @Test(expected = SonarQubeException.class)
    public void createTest()
            throws BadSonarQubeRequestException, SonarQubeException, UnknownQualityGateException {
        SonarQubeServer sonarQubeServer = new SonarQubeServer();
        ReportModelFactory reportModelFactory = new ReportModelFactory(sonarQubeServer, conf);
        reportModelFactory.create();
    }

}
