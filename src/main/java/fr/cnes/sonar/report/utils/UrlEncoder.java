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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides methods to encode strings in URL
 */
public class UrlEncoder {

    private UrlEncoder() {}

    /**
     * Logger for the class
     */
    protected static final Logger LOGGER = Logger.getLogger(UrlEncoder.class.getCanonicalName());

    /**
     * URL Encode a string to use with SonarQube API
     * @param pString String to encode
     * @return The URL encoded string
     */
    public static String urlEncodeString(String pString) {
        String encodedString = pString;

        try {
            // URL Encode the String to avoid errors with special characters in URL
            encodedString = URLEncoder.encode(pString, StandardCharsets.UTF_8.toString());
            /**
             * URLEncoder replace whitespaces with + to match HTML convention
             * So to avoid issues we replace the + with %20
             * as the + that may have been present in the initial string are now already encoded
             */ 
            encodedString = encodedString.replace("+", "%20");
        } catch(UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        
        return encodedString;
    }
    public static String sanitizeUrlSafe(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
    
}