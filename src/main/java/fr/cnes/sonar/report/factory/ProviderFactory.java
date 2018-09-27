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

package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * Construct the correct providers based on SonarQube version.
 */
public class ProviderFactory {

    /** Logger of this class. */
    private static final Logger LOGGER = Logger.getLogger(ProviderFactory.class.getName());

    /** SonarQube server. */
    private SonarQubeServer server;

    /** Token to authenticate the user on the SonarQube server. */
    private String token;

    /** Key of the project to report. */
    private String projectKey;

    /**
     * Constructor.
     * @param server Represents the server.
     * @param token String representing the user token.
     * @param project The id of the project to report.
     */
    public ProviderFactory(final SonarQubeServer server, final String token, final String project) {
        // get sonar server
        this.server = server;
        // get user token
        this.token = token;
        // get project key
        this.projectKey = project;
    }

    /**
     * Create a data provider from its class. The DataProvider must have one of the following constructors:
     * - Constructor(final String server, final String token)
     * - Constructor(final String server, final String token, final String project)
     *
     * @param providerClass Class of DataProvider to instantiate.
     * @param <T> Class of the DataProvider.
     * @return An operational DataProvider.
     */
    public <T extends AbstractDataProvider> T create(final Class<T> providerClass) {
        // Get all available constructors.
        final Constructor<T>[] constructors = (Constructor<T>[]) providerClass.getConstructors();
        // Final result.
        T provider = null;

        try {
            if (null != constructors && 0 != constructors.length) {
                if (2 == constructors[0].getParameterCount()) {
                    provider = constructors[0].newInstance(server, token);
                } else if (3 == constructors[0].getParameterCount()) {
                    provider = constructors[0].newInstance(server, token, projectKey);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
        return provider;
    }

}
