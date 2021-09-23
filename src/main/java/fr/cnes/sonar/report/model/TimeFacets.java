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
import java.util.Iterator;
import java.util.List;

public class TimeFacets {

    /**
     * List of time facets
     */
    private List<TimeFacet> facets;

    /**
     * Default constructor
     */
    public TimeFacets() {
        this.facets = new ArrayList<>();
    }

    /**
     * Setter for facets
     * @param pTimeFacet List of related facets
     */
    public void setTimeFacets(List<TimeFacet> pTimeFacet) {
        this.facets = new ArrayList<>(pTimeFacet);
    }

    /**
     * Extract the values of a specific facet
     * @param facetName The name of the facet we want values from
     * @return The list of values contained inside the wanted facet
     */
    public List<TimeValue> getFacetValues(String facetName) {
        // iterate on facets' list
        final Iterator<TimeFacet> iterator = this.facets.iterator();

        // list of results
        List<TimeValue> items = new ArrayList<>();

        TimeFacet timeFacet;
        while(iterator.hasNext() && items.isEmpty()) {
            // get current facet
            timeFacet = iterator.next();
            // check if current facet is the wanted one
            if(timeFacet.getProperty().equals(facetName)) {
                items = timeFacet.getValues();
            }
        }
        
        return items;
    }
    
}
