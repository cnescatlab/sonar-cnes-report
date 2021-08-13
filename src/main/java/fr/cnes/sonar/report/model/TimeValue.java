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

package fr.cnes.sonar.report.model;

/**
 * A simple pair date - number
 */
public class TimeValue {

    /**
     * Excel date of the measured value
     */
    private double date;

    /**
     * Measured value (Word charts expect value as String)
     */
    private String value;

    /**
     * Complete constructor
     * @param pDate date of the measure
     * @param pValue value of the measure
     */
    public TimeValue(final double pDate, final String pValue) {
        this.date = pDate;
        this.value = pValue;
    }

    /**
     * Getter for date
     * @return date
     */
    public double getDate() {
        return date;
    }

    /**
     * Getter for value
     * @return value
     */
    public String getValue() {
        return value;
    }
}
