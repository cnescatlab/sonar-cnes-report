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

package fr.cnes.sonar.report.exporters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Colors shared by the exported reports, so that a given severity or issue type
 * has the same color in the docx charts and in the xlsx sheets.
 * Colors are RGB hexadecimal strings without the leading '#'.
 */
public final class ReportPalette {

    /** Strong colors for issue severities (charts). */
    private static final Map<String, String> SEVERITY = new HashMap<>();
    /** Light colors for issue severities (cell backgrounds). */
    private static final Map<String, String> SEVERITY_LIGHT = new HashMap<>();
    /** Strong colors for issue types (charts). */
    private static final Map<String, String> TYPE = new HashMap<>();

    static {
        SEVERITY.put("BLOCKER", "8E1B1B");
        SEVERITY.put("CRITICAL", "D93025");
        SEVERITY.put("MAJOR", "E8710A");
        SEVERITY.put("MINOR", "F2B600");
        SEVERITY.put("INFO", "80868B");

        SEVERITY_LIGHT.put("BLOCKER", "F4CCCC");
        SEVERITY_LIGHT.put("CRITICAL", "FAD2CF");
        SEVERITY_LIGHT.put("MAJOR", "FDE3C8");
        SEVERITY_LIGHT.put("MINOR", "FEF1C4");
        SEVERITY_LIGHT.put("INFO", "E8EAED");

        TYPE.put("BUG", "D93025");
        TYPE.put("VULNERABILITY", "E8710A");
        TYPE.put("CODE_SMELL", "1A73E8");
    }

    /**
     * Private constructor to hide the public one
     */
    private ReportPalette() {}

    /**
     * Strong colors indexed by severity (BLOCKER, CRITICAL, MAJOR, MINOR, INFO).
     * @return an unmodifiable map
     */
    public static Map<String, String> severityColors() {
        return Collections.unmodifiableMap(SEVERITY);
    }

    /**
     * Light colors indexed by severity, suitable as a background behind dark text.
     * @return an unmodifiable map
     */
    public static Map<String, String> severityLightColors() {
        return Collections.unmodifiableMap(SEVERITY_LIGHT);
    }

    /**
     * Strong colors indexed by issue type (BUG, VULNERABILITY, CODE_SMELL).
     * @return an unmodifiable map
     */
    public static Map<String, String> typeColors() {
        return Collections.unmodifiableMap(TYPE);
    }

    /**
     * Convert a hexadecimal RGB color to bytes.
     * @param hex color such as "D93025"
     * @return the three RGB bytes
     */
    public static byte[] toRgb(final String hex) {
        final int value = Integer.parseInt(hex, 16);
        return new byte[] { (byte) (value >> 16), (byte) (value >> 8), (byte) value };
    }
}
