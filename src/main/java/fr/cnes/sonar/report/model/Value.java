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
 * @author lequal
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
     * @param val value to give to val
     * @param count value to give to count
     */
    public Value(String val, int count) {
        this.val = val;
        this.count = count;
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
     * @param val value to give to val
     */
    public void setVal(String val) {
        this.val = val;
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
     * @param count count
     */
    public void setCount(int count) {
        this.count = count;
    }
}
