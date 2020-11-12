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

package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Value;
import fr.cnes.sonar.report.utils.StringManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAdapterTest extends CommonTest{
    
    private int nbFacet;
    private int nbValue;
    private List<Facet> facets;

    /**
     * Explore the facets and values
     */
    @Before
    public void setUp(){
        this.facets = report.getFacets();
        for(int i = 0; i < facets.size(); i ++){
            if(facets.get(i).getProperty().equals("severities")){
                this.nbFacet = i;
                List<Value> values = facets.get(i).getValues();
                for(int j = 0; j < values.size(); j ++){
                    if(values.get(j).getVal().equals(StringManager.HOTSPOT_SEVERITY)){
                        this.nbValue = j;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Assert that security hotspots are well considered for what they are
     */
    @Test
    public void editFacetForHotspotTest(){
        DataAdapter.editFacetForHotspot(facets, report);
        Assert.assertEquals(1, this.facets.get(this.nbFacet).getValues().get(this.nbValue).getCount());
    }

    /**
     * Assert that the method "loadPlaceholdersMap" fills correctly the placeholders
     * It also tests the commonTest implementation
     * By recursion, this method also tests the method "getPlaceHolderName"
     */
    @Test
    public void loadPlaceholdersMapTest(){
        Map<String,String> placeHolders = DataAdapter.loadPlaceholdersMap(report);
        Map<String,String> expected = new HashMap<>();
        // Fill manually the placeHolders depending on what we initialized in "CommonTest"
        expected.put("XX-PROJECTNAME-XX", "CNES Report");
        expected.put("XX-DUPLICATION-XX", "1.0");
        expected.put("XX-MAXNCLOC-XX", "unknown");
        expected.put("XX-QUALITYGATEFILE-XX", "CNES.xml");
        expected.put("XX-MAXCOGNITIVECOMPLEXITY-XX", "unknown");
        expected.put("XX-QUALITYPROFILENAME-XX", "");
        expected.put("XX-MINCOMMENTDENSITY-XX", "unknown");
        expected.put("XX-QUALITYGATE-XX", "1.0");
        expected.put("XX-MAXDUPLICATION-XX", "unknown");
        expected.put("XX-QUALITYGATENAME-XX", "CNES");
        expected.put("XX-QUALITYPROFILEFILE-XX", "");
        expected.put("XX-MINDUPLICATION-XX", "unknown");
        expected.put("XX-AUTHOR-XX", "Lequal");
        expected.put("XX-MINCOMPLEXITY-XX", "unknown");
        expected.put("XX-MAXCOMPLEXITY-XX", "unknown");
        expected.put("XX-SECURITY-XX", "C");
        expected.put("XX-MINCOGNITIVECOMPLEXITY-XX", "unknown");
        expected.put("XX-DATE-XX", new Date().toString());
        expected.put("XX-RELIABILITY-XX", "A");
        expected.put("XX-COVERAGE-XX", "1.0");
        expected.put("XX-VERSION-XX", "Version");
        expected.put("XX-LINES-XX", "1.0");
        expected.put("XX-MAINTAINABILITY-XX", "B");
        expected.put("XX-MINCOVERAGE-XX", "0.0");
        expected.put("XX-DESCRIPTION-XX", "Short description");
        expected.put("XX-MINNCLOC-XX", "unknown");
        expected.put("XX-MAXCOMMENTDENSITY-XX", "unknown");
        Assert.assertEquals(expected, placeHolders);

        // Add the maxcoverage to cancel the unknown error
        Map metricsStats = report.getMetricsStats();
        metricsStats.put("maxcoverage", 1.0);
        report.setMetricsStats(metricsStats);

        // Change the unknown placeholders by the real value
        expected.replace("XX-MAXNCLOC-XX", "1.0");
        expected.replace("XX-MAXCOGNITIVECOMPLEXITY-XX", "1.0");
        expected.replace("XX-MINCOMMENTDENSITY-XX", "0.0");
        expected.replace("XX-MAXDUPLICATION-XX", "1.0");
        expected.replace("XX-MINDUPLICATION-XX", "0.0");
        expected.replace("XX-MINCOMPLEXITY-XX", "0.0");
        expected.replace("XX-MAXCOMPLEXITY-XX", "1.0");
        expected.replace("XX-MINCOGNITIVECOMPLEXITY-XX", "0.0");
        expected.replace("XX-MINNCLOC-XX", "0.0");
        expected.replace("XX-MAXCOMMENTDENSITY-XX", "1.0");
        expected.put("XX-MAXCOVERAGE-XX", "1.0");
        Map<String,String> newPlaceHolders = DataAdapter.loadPlaceholdersMap(report);
        Assert.assertEquals(expected, newPlaceHolders);
    }
}
