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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Utility class to convert a SonarQube date to an Excel date.
 */
public class DateConverter {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    private DateConverter() {
    }

    /**
     * Convert a SonarQube date to an Excel date.
     * @param sonarQubeDate The SonarQube date.
     * @return The Excel date.
     * @throws DateTimeParseException when string cannot be parsed
     */
    public static double sonarQubeDateToExcelDate(String sonarQubeDate) {
        return DateUtil.getExcelDate(parseDateTime(sonarQubeDate));
    }

    /**
     * Parse a DateTime.
     * @param s string in format yyyy-MM-dd'T'HH:mm:ssZ
     * @throws DateTimeParseException when string cannot be parsed
     */
    private static Date parseDateTime(String s) {
        return Date.from(parseOffsetDateTime(s).toInstant());
    }

    /**
     * Parse an OffsetDateTime.
     * @param s string in format yyyy-MM-dd'T'HH:mm:ssZ
     * @throws DateTimeParseException when string cannot be parsed
     */
    private static OffsetDateTime parseOffsetDateTime(String s) {
        return OffsetDateTime.parse(s, DATETIME_FORMATTER);
    }
}
