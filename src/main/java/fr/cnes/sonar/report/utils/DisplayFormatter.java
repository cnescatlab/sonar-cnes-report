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

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Format log message to be displayed on the console.
 */
public class DisplayFormatter extends Formatter {

    /**
     * All printed message should comply to this format.
     */
    private static final String FORMAT_DEFAULT = "[%s] %s\n";

    /**
     * Following format is only for INFO level: we just print the message.
     */
    private static final String FORMAT_INFO = "%s\n";

    /**
     * Following format is only for SEVERE level: we just print the message.
     */
    private static final String FORMAT_SEVERE = "[ERROR] %s\n";

    /**
     * Inherited method to format messages.
     * @param logRecord Contain information provided to the logger.
     * @return The formatted string as defined in FORMAT constant.
     */
    @Override
    public String format(LogRecord logRecord) {
        // Default format applied at beginning
        String message = String.format(FORMAT_DEFAULT, logRecord.getLevel().getName(), logRecord.getMessage());

        // If logRecord level is FINE, we change the format.
        if(logRecord.getLevel().equals(Level.INFO)) {
            message = String.format(FORMAT_INFO, logRecord.getMessage());
        } else if(logRecord.getLevel().equals(Level.SEVERE)) {
            message = String.format(FORMAT_SEVERE, logRecord.getMessage());
        }

        return message;
    }
}
