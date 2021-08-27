package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.CommonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;


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
        // Values should be found
        assertEquals(2, report.getTimeFacets().getFacetValues("sqale_debt_ratio").size());
    }
    
}
