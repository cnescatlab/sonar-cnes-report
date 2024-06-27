package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Assert;
import org.junit.Test;

public class ServerFactoryTest extends CommonTest {

    @Test
    public void createStandaloneTest() throws BadSonarQubeRequestException {
        try {
            ServerFactory serverFactory = new ServerFactory("http://biiiiiim", standaloneProviderFactory);
            serverFactory.create();
        } catch (SonarQubeException e) {
            Assert.assertEquals("Impossible to reach SonarQube instance.", e.getMessage());
        }
    }
}
