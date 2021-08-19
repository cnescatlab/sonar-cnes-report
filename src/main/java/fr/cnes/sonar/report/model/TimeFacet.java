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
 * A SonarQube metric with its measures through time
 */
public class TimeFacet {
    /**
     * List of time values as list of pairs
     */
    private List<TimeValue> values;
    /**
     * Name of the facet
     */
    private String property;

    /**
     * Parameterized constructor
     * @param pProperty The name of the facet
     * @param pValues The values of the facet
     */
    public TimeFacet(String pProperty, List<TimeValue> pValues) {
        values = new ArrayList<>(pValues);
        property = pProperty;
    }

    /**
     * Return the values of the facet
     * @return  list of values
     */
    public List<TimeValue> getValues() {
        return new ArrayList<>(values);
    }

    /**
     * @return the name of the facet
     */
    public String getProperty() {
        return property;
    }
}
