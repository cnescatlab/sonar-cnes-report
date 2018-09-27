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

import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.lang.StringUtils;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsResponse;

/**
 * Manage http requests.
 *
 */
public final class RequestManager {

    /**
     * Instance of the singleton
     */
    private static RequestManager ourInstance = null;

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
     */
    public String get(final String url, final String token) {
        String baseUrl = StringUtils.substringBeforeLast(url, "/");
        String path = StringUtils.substringAfterLast(url, "/");
        final HttpConnector.Builder builder = HttpConnector.newBuilder()
            .userAgent("cnesreport")
            .url(baseUrl);
        if(!StringManager.getProperty(StringManager.SONAR_TOKEN).equals(token)) {
            builder.credentials(token, null);
        }
        final HttpConnector httpConnector = builder.build();
        WsResponse response = httpConnector.call(new GetRequest(path));
        return response.content();
    }
}
