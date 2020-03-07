/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.lang3.StringUtils;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsResponse;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Manage http requests.
 */
public final class RequestManager {

    /**
     * Instance of the singleton
     */
    private static RequestManager ourInstance = null;
    /**
     * System property for proxy host
     */
    public static final String STR_PROXY_HOST = "https.proxyHost";

    /**
     * System property for proxy port
     */
    public static final String STR_PROXY_PORT = "https.proxyPort";

    /**
     * System property for proxy user
     */
    public static final String STR_PROXY_USER = "https.proxyUser";

    /**
     * System property for proxy password
     */
    public static final String STR_PROXY_PASS = "https.proxyPassword";

    /**
     * Use of private constructor to singletonize this class
     */
    private RequestManager() { }

    /**
     * Return the unique instance
     * @return the singleton
     */
    public static synchronized RequestManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new RequestManager();
        }
        return ourInstance;
    }

    /**
     * Execute a get http request
     * @param url server to request
     * @param token token to authenticate to SonarQube
     * @return response as string
     * @throws SonarQubeException When SonarQube server is not callable.
     * @throws BadSonarQubeRequestException if SonarQube Server sent an error
     */
    public String get(final String url, final String token) throws SonarQubeException, BadSonarQubeRequestException {
        // Initialize connexion information.
        final String baseUrl = StringUtils.substringBeforeLast(url, "/");
        final String path = StringUtils.substringAfterLast(url, "/");
        final String proxyHost = System.getProperty(STR_PROXY_HOST, StringManager.EMPTY);
        final String proxyPort = System.getProperty(STR_PROXY_PORT, StringManager.EMPTY);
        final String proxyUser = System.getProperty(STR_PROXY_USER, StringManager.EMPTY);
        final String proxyPass = System.getProperty(STR_PROXY_PASS, StringManager.EMPTY);

        // Initialize http connector builder.
        final HttpConnector.Builder builder = HttpConnector.newBuilder()
                .userAgent("cnesreport")
                .url(baseUrl);

        // Set SonarQube authentication token.
        if(!StringManager.getProperty(StringManager.SONAR_TOKEN).equals(token)) {
            builder.credentials(token, null);
        }

        // Set proxy settings.
        if(!proxyHost.isEmpty()) {
            int proxyUsedPort;
            try {
                proxyUsedPort = Integer.valueOf(proxyPort);
            } catch (NumberFormatException wrongPort) {
                proxyUsedPort = 80;
            }

            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyUsedPort));

            builder.proxy(proxy);

            if(!proxyUser.isEmpty()) {
                builder.proxyCredentials(proxyUser, proxyPass);
            }
        }

        // Execute the request.
        final HttpConnector httpConnector = builder.build();
        WsResponse response;
        try {
            response = httpConnector.call(new GetRequest(path));
        } catch (Exception e) {
            throw new SonarQubeException("Impossible to reach SonarQube instance.", e);
        }

        // Throws exception with advice to cnesreport user
        switch (response.code()) {
            case 401:
                throw new BadSonarQubeRequestException("Unauthorized error sent by SonarQube server (code 401), please provide a valid authentication token to cnesreport.");
            case 403:
                throw new BadSonarQubeRequestException("Insufficient privileges error sent by SonarQube server (code 403), please check your permissions in SonarQube configuration.");
            case 404:
                throw new BadSonarQubeRequestException(String.format("Not found error sent by SonarQube server (code 404, URL %s, Error %s), please check cnesreport compatibility with your SonarQube server version.",response.requestUrl(), response.content()));
            default:
                break;
        }

        return response.content();
    }
}
