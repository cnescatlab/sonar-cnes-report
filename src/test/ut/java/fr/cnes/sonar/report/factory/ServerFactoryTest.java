package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import org.junit.Assert;
import org.junit.Test;

public class ServerFactoryTest {

    @Test
    public void createTest() throws BadSonarQubeRequestException {
        try {
            ServerFactory serverFactory = new ServerFactory("http://biiiiiim", "noauth");
            serverFactory.create();
        } catch (SonarQubeException e) {
            Assert.assertEquals("Impossible to reach SonarQube instance.", e.getMessage());
        }
    }

}
