package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.CommonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TimeValueTest extends CommonTest {

    @Test
    public void getFacetValuesEmptyTest() {
        TimeFacets timeFacets = new TimeFacets();
        assertEquals(0, timeFacets.getFacetValues("empty").size());
    }

    @Test
    public void getFacetValuesNotFoundTest() {
        // No values should be found
        assertEquals(0, report.getTimeFacets().getFacetValues("non-existent").size());
    }

    @Test
    public void getFacetValuesNormalTest() {
        // Test data
        List<TimeValue> values1 = new ArrayList<>();
        values1.add(new TimeValue(1.0, "1.0"));
        values1.add(new TimeValue(2.3, "4.5"));
        List<TimeValue> values2 = new ArrayList<>();
        values2.add(new TimeValue(1.0, "1"));
        values2.add(new TimeValue(2.3, "4"));
        values2.add(new TimeValue(5.6, "7"));

        List<TimeFacet> facetList = new ArrayList<>();
        facetList.add(new TimeFacet("existent", values1));
        facetList.add(new TimeFacet("other", values2));
        
        TimeFacets timeFacets = new TimeFacets();
        timeFacets.setTimeFacets(facetList);

        // Values should be found
        assertEquals(2, report.getTimeFacets().getFacetValues("existent").size());
    }
    
}
