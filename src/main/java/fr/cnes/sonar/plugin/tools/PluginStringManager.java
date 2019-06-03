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

package fr.cnes.sonar.plugin.tools;

import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * String manager for cnes-report in plugin mode
 */
public class PluginStringManager {
    private static Properties properties = new Properties();
    private PluginStringManager(){
        throw new IllegalStateException("Utility class");
    }
    protected static final Logger LOGGER = Logger.getLogger(PluginStringManager.class.getCanonicalName());
    static {

        final ClassLoader classLoader = AbstractDataProvider.class.getClassLoader();

        // load properties file as a stream
        try (
            InputStream input = classLoader.getResourceAsStream("plugin.properties")){
            if(input!=null) {
                // load properties from the stream in an adapted structure
                properties.load(input);
            }
        } catch (IOException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static String getProperty(String p){
        return properties.getProperty(p);
    }
}
