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
 * Represents a measure
 */
public class Measure {
    /**
     * Name of the metric
     */
    private String metric;
    /**
     * Value of the metric
     */
    private String value;

    /**
     * Default constructor
     */
    public Measure() {
        this.metric = "";
        this.value = "";
    }

    /**
     * Complete constructor
     * @param pMetric value for metric
     * @param pValue value for value
     */
    public Measure(String pMetric, String pValue) {
        this.metric = pMetric;
        this.value = pValue;
    }

    /**
     * Getter for metric's name
     * @return metric's name
     */
    public String getMetric() {
        return metric;
    }

    /**
     * Setter for metric's name
     * @param pMetric value
     */
    public void setMetric(String pMetric) {
        this.metric = pMetric;
    }

    /**
     * Getter for value
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for value
     * @param pValue value
     */
    public void setValue(String pValue) {
        this.value = pValue;
    }

    /**
     * Overridden method toString
     * @return "metric":"value"
     */
    @Override
    public String toString() {
        return getMetric()+":"+getValue();
    }
}
