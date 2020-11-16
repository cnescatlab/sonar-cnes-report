package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.After;
import org.junit.Test;

public class RequestManagerTest extends CommonTest {

    private static final String HOST = "http://sonarqube.fake:9393/";
    private static final String TOKEN = "token";

    @After
    public void cleanSystemProperties() {
        // Clear all System properties linked to proxy settings
        System.clearProperty(RequestManager.STR_PROXY_HOST);
        System.clearProperty(RequestManager.STR_PROXY_PORT);
        System.clearProperty(RequestManager.STR_PROXY_USER);
        System.clearProperty(RequestManager.STR_PROXY_PASS);
        System.clearProperty(RequestManager.STR_NON_PROXY_HOSTS);
    }

    @Test(expected = SonarQubeException.class)
    public void testFailedRequest() throws SonarQubeException, BadSonarQubeRequestException {
        // This SonarQube URL is unreachable
        RequestManager req = RequestManager.getInstance();
        req.get(HOST, TOKEN);
    }

    @Test(expected = SonarQubeException.class)
    public void testFailedProxy() throws SonarQubeException, BadSonarQubeRequestException {
        // These proxy settings are unreachable
        System.setProperty(RequestManager.STR_PROXY_HOST, "http://unit-test.proxy.fr");
        System.setProperty(RequestManager.STR_PROXY_PORT, "1234");
        System.setProperty(RequestManager.STR_PROXY_USER, "username");
        System.setProperty(RequestManager.STR_PROXY_PASS, "password");
        System.setProperty(RequestManager.STR_NON_PROXY_HOSTS, "127.0.0.1||*proxy*|*test|useless*");

        // This SonarQube URL is unreachable
        RequestManager req = RequestManager.getInstance();
        req.get(HOST, TOKEN);
    }
    
}
