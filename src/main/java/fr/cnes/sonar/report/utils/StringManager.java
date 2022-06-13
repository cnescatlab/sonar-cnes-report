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
import java.util.Map;
import java.util.HashMap;

/**
 * Public class which contains all public String used by internal classes
 */
public final class StringManager {

    /** Just an empty string. */
    public static final String EMPTY = "";
    /** Just a tabulation. */
    public static final String TAB = "\t";
    /** Just a space. */
    public static final String SPACE = " ";
    /** Placeholder for no-branch. */
    public static final String NO_BRANCH = "%";
    /** Just a space for URI. */
    public static final String URI_SPACE = "%20";
    /** Name for properties' file about report. */
    public static final String REPORT_PROPERTIES = "report.properties";
    /** Name of the property giving the server server. */
    public static final String SONAR_URL = "sonar.url";
    /** Name of the property giving the token to authenticate to SonarQube. */
    public static final String SONAR_TOKEN = "sonar.token";
    /** Logged message when there are too much issues to export. */
    public static final String ISSUES_OVERFLOW_MSG = "log.overflow.msg";
    /** Pattern to format the date. */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /** Regular expression to match exactly the date format */
    public static final String DATE_REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
    /** Default path to the target diretory for report files. */
    public static final String DEFAULT_OUTPUT = "report.path";
    /** Default language for the report. */
    public static final String DEFAULT_LANGUAGE = "report.locale";
    /** Default name for the author. */
    public static final String DEFAULT_AUTHOR = "report.author";
    /** The only severity decided by us for the hotspot security */
    public static final String HOTSPOT_SEVERITY = "CRITICAL";
     /** Security hotspot type (different from issue type) */
    public static final String HOTSPOT_TYPE = "SECURITY_HOTSPOT";

    /** Logger for StringManager. */
    private static final Logger LOGGER = Logger.getLogger(StringManager.class.getCanonicalName());
    
    /** Contain all the properties related to the report. */
    private static Properties properties;

    /** Contains internationalized fields. */
    private static ResourceBundle messages;

    /** Unique instance of this class (singleton). */
    private static StringManager ourInstance = null;

    /**
     * List of possible security hotspot categories
     */
    private static final Map<String, String> SECURITY_HOTSPOT_CATEGORIES = new HashMap<>();

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
            if(input!=null) {
                // load properties from the stream in an adapted structure
                properties.load(input);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if(input!=null) {
                try {
                    // close the stream if necessary (not null)
                    input.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        // load internationalized strings, default is defined in the properties file
        changeLocale(properties.getProperty(DEFAULT_LANGUAGE));

        // initialize security hotspots categories
        initSecurityHotspotsCategories();
    }

    /**
     * Private constructor to singletonize the class.
     */
    private StringManager() {}

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
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    public static String getProperty(final String property) {
        return properties.getProperty(property);
    }

    /**
     * Change the locale and reload messages
     * @param language String in lowercase
     * @param country String in upper case
     */
    public static synchronized void changeLocale(final String language, final String country) {
        // change locale
        Locale currentLocale = new Locale(language,country);
        // reload messages
        messages = ResourceBundle.getBundle("messages", currentLocale);
    }

    /**
     * Change the locale and reload messages
     * @param language String containing both the language and country, e.g. en_US
     */
    public static void changeLocale(String language) {
        String[] locale = language.split("_");

        try {
            changeLocale(locale[0], locale[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Unable to change the locale due to malformed command line parameter : " + language);
        }
    }

    /**
     * Return string corresponding to the given key according the locale
     * @param key name of the property in the bundle messages
     * @return a String
     */
    public static String string(final String key) {
        return messages.getString(key);
    }

    /**
     * Initialize security hotspots categories
     */
    public static void initSecurityHotspotsCategories() {
        SECURITY_HOTSPOT_CATEGORIES.put("buffer-overflow", "Buffer Overflow");
        SECURITY_HOTSPOT_CATEGORIES.put("sql-injection", "SQL Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("rce", "Code Injection (RCE)");
        SECURITY_HOTSPOT_CATEGORIES.put("object-injection", "Object Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("command-injection", "Command Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("path-traversal-injection", "Path Traversal Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("ldap-injection", "LDAP Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("xpath-injection", "XPath Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("log-injection", "Log Injection");
        SECURITY_HOTSPOT_CATEGORIES.put("xxe", "XML External Entity (XXE)");
        SECURITY_HOTSPOT_CATEGORIES.put("xss", "Cross-Site Scripting (XSS)");
        SECURITY_HOTSPOT_CATEGORIES.put("dos", "Denial of Service (DoS)");
        SECURITY_HOTSPOT_CATEGORIES.put("ssrf", "Server-Side Request Forgery (SSRF)");
        SECURITY_HOTSPOT_CATEGORIES.put("csrf", "Cross-Site Request Forgery (CSRF)");
        SECURITY_HOTSPOT_CATEGORIES.put("http-response-splitting", "HTTP Response Splitting");
        SECURITY_HOTSPOT_CATEGORIES.put("open-redirect", "Open Redirect");
        SECURITY_HOTSPOT_CATEGORIES.put("weak-cryptography", "Weak Cryptography");
        SECURITY_HOTSPOT_CATEGORIES.put("auth", "Authentication");
        SECURITY_HOTSPOT_CATEGORIES.put("insecure-conf", "Insecure Configuration");
        SECURITY_HOTSPOT_CATEGORIES.put("file-manipulation", "File Manipulation");
        SECURITY_HOTSPOT_CATEGORIES.put("others", "Others");
        SECURITY_HOTSPOT_CATEGORIES.put("permission", "Permission");
        SECURITY_HOTSPOT_CATEGORIES.put("encrypt-data", "Encryption of Sensitive Data");
        SECURITY_HOTSPOT_CATEGORIES.put("traceability", "Traceability");
    }

    /**
     * Return a map containing the security hotspots categories names associated to their key
     * @return the map
     */
    public static Map<String,String> getSecurityHotspotsCategories() {
        return SECURITY_HOTSPOT_CATEGORIES;
    }
}
