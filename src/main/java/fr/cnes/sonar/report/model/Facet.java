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

import java.util.ArrayList;
import java.util.List;

/**
 * A facet of sonarqube
 * @author lequal
 */
public class Facet {
    /**
     * List of values as list of pairs
     */
    private List<Value> values;
    /**
     * Name of the facet
     */
    private String property;

    /**
     * Default constructor
     */
    public Facet() {
        values = new ArrayList<>();
        property = "";
    }

    /**
     * Return the values of the facet
     * @return  list of values
     */
    public List<Value> getValues() {
        return new ArrayList<>(values);
    }

    /**
     * Set the values of the facet
     * @param values list of values
     */
    public void setValues(List<Value> values) {
        this.values = new ArrayList<>(values);
    }

    /**
     * @return the name of the facet
     */
    public String getProperty() {
        return property;
    }

    /**
     * Set the name of a facet
     * @param property name to set
     */
    public void setProperty(String property) {
        this.property = property;
    }
}
