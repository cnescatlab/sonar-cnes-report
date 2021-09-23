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

import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Facets {

    /**
     * List of facets
     */
    private List<Facet> facetsList;

    /**
     * Default constructor
     */
    public Facets() {
        this.facetsList = new ArrayList<>();
    }

    /**
     * Setter for facets
     * @param pFacet List of related facets
     */
    public void setFacets(List<Facet> pFacets) {
        this.facetsList = new ArrayList<>(pFacets);
    }

    /**
     * Extract the values of a specific facet
     * @param facetName The name of the facet we want values from
     * @return The list of values contained inside the wanted facet
     */
    public List<Value> getFacetValues(String facetName) {
        // iterate on facets' list
        final Iterator<Facet> iterator = facetsList.iterator();
        // list of results
        List<Value> items = new ArrayList<>();
        Facet facet;
        while(iterator.hasNext() && items.isEmpty()) {
            // get current facet
            facet = iterator.next();
            // check if current facet is the wanted one
            if(facet.getProperty().equals(facetName)) {
                items = facet.getValues();
            }
        }

        // sort the result to fit docx template legends
        Collections.sort(items, new ValueComparator(facetName));
        
        return items;
    }
    
}

/**
 * ValueComparator is used to compare 2 values of a facet to sort them by severity or type
 */
class ValueComparator implements Comparator<Value>{

    /**
     * Property to get the list of issues severities
     */
    private static final String ISSUES_SEVERITIES = "issues.severities";
    /**
     * Property to get the list of issues types
     */
    private static final String ISSUES_TYPES = "issues.types";

    private String name;

    ValueComparator(String name){
        this.name = name;
    }

    public int compare(Value v1, Value v2) {
        int compare;

        if (this.name.equals("severities")) {
            List<String> issueSeverities = Arrays.asList(StringManager.getProperty(ISSUES_SEVERITIES).split(","));
            compare = issueSeverities.indexOf(v1.getVal()) - issueSeverities.indexOf(v2.getVal());
        } else if(this.name.equals("types")) {
            List<String> issueTypes = Arrays.asList(StringManager.getProperty(ISSUES_TYPES).split(","));
            compare = issueTypes.indexOf(v1.getVal()) - issueTypes.indexOf(v2.getVal());
        } else {
            compare = 0;
        }

        return compare;
    }
}
