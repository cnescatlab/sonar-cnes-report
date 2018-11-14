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

package fr.cnes.sonar.report.utils;

import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Public class which contains all public String used by internal classes
 */
public final class StringManager {

    /**
     * Just an empty string.
     */
    public static final String EMPTY = "";
    /**
     * Just a tabulation.
     */
    public static final String TAB = "\t";
    /**
     * Just a space.
     */
    public static final String SPACE = " ";
    /**
     * Just a space for URI.
     */
    public static final String URI_SPACE = "%20";
    /**
     * Name for properties' file about report.
     */
    public static final String REPORT_PROPERTIES = "report.properties";
    /**
     * Name of the property giving the server server.
     */
    public static final String SONAR_URL = "sonar.url";
    /**
     * Name of the property giving the token to authenticate to SonarQube.
     */
    public static final String SONAR_TOKEN = "sonar.token";
    /**
     * Logged message when there are too much issues to export.
     */
    public static final String ISSUES_OVERFLOW_MSG = "log.overflow.msg";
    /**
     * Pattern to format the date.
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * Default path to the target diretory for report files.
     */
    public static final String DEFAULT_OUTPUT = "report.path";
    /**
     * Default language for the report.
     */
    public static final String DEFAULT_LANGUAGE = "report.locale";
    /**
     * Default name for the author.
     */
    public static final String DEFAULT_AUTHOR = "report.author";

    /**
     * Logger for StringManager.
     */
    private static final Logger LOGGER = Logger.getLogger(StringManager.class.getCanonicalName());

    /**
     * Contain all the properties related to the report.
     */
    private static Properties properties;

    /**
     * Contains internationalized fields.
     */
    private static ResourceBundle messages;

    /**
     * Define the language of the program.
     */
    private static Locale currentLocale;

    /**
     * Unique instance of this class (singleton).
     */
    private static StringManager ourInstance = null;

    //
    // Static initialization block for reading .properties
    //
    static {
        // store properties
        properties = new Properties();
        // read the file
        InputStream input = null;

        final ClassLoader classLoader = AbstractDataProvider.class.getClassLoader();

        try {
            // load properties file as a stream
            input = classLoader.getResourceAsStream(StringManager.REPORT_PROPERTIES);
            if (input != null) {
                // load properties from the stream in an adapted structure
                properties.load(input);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (input != null) {
                try {
                    // close the stream if necessary (not null)
                    input.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        String[] locale = properties.getProperty(StringManager.DEFAULT_LANGUAGE).split("_");
        // load internationalized strings, french by default
        changeLocale(locale[0], locale[1]);
    }

    /**
     * Private constructor to singletonize the class.
     */
    private StringManager() {
    }

    /**
     * Get the singleton
     *
     * @return unique instance of StringManager
     */
    public static synchronized StringManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new StringManager();
        }
        return ourInstance;
    }

    /**
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to the report.
     *
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    public static String getProperty(final String property) {
        return properties.getProperty(property);
    }

    /**
     * Change the locale and reload messages
     *
     * @param language String in lowercase
     * @param country  String in upper case
     */
    public static synchronized void changeLocale(final String language, final String country) {
        // change locale
        currentLocale = new Locale(language, country);
        // reload messages
        messages = ResourceBundle.getBundle("messages", currentLocale);
    }

    /**
     * Return string corresponding to the given key according the locale
     *
     * @param key name of the property in the bundle messages
     * @return a String
     */
    public static String string(final String key) {
        return messages.getString(key);
    }
}
