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
 * A simple pair
 */
public class Value {
    /**
     * Value to count
     */
    private String val;
    /**
     * Number of occurrences
     */
    private int count;

    /**
     * Complete constructor
     * @param pVal value to give to val
     * @param pCount value to give to count
     */
    public Value(final String pVal, final int pCount) {
        this.val = pVal;
        this.count = pCount;
    }

    /**
     * Getter for val
     * @return val
     */
    public String getVal() {
        return val;
    }

    /**
     * Setter for val
     * @param pVal value to give to val
     */
    public void setVal(final String pVal) {
        this.val = pVal;
    }

    /**
     * Getter for count
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Setter to give to count
     * @param pCount count
     */
    public void setCount(final int pCount) {
        this.count = pCount;
    }
}
