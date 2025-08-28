package fr.cnes.sonar.report.model;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test to verify that max metrics are calculated correctly and not showing sum values.
 * 
 * This test addresses the issue where complexity, cognitive_complexity, and ncloc
 * metrics were showing the sum of all files instead of the maximum value per file.
 * The fix ensures the SonarQube API request includes qualifiers=FIL to get
 * file-level metrics rather than aggregated project-level metrics.
 */
public class MaxMetricsFixTest {

    @Test
    public void testMaxComplexityNotSum() {
        // Simulate multiple files with different complexity values
        ArrayList<Map<String,String>> componentsTest = new ArrayList<>();
        Components components = new Components();

        // File 1:
        Map<String,String> file1 = new HashMap<>();
        file1.put("complexity", "10");
        file1.put("cognitive_complexity", "15");
        file1.put("ncloc", "100");
        
        // File 2: 
        // complexity 32 (this should be the max)
        // cognitive_complexity 45 (this should be the max)
        // ncloc 200 (this should be the max)
        Map<String,String> file2 = new HashMap<>();
        file2.put("complexity", "32");  
        file2.put("cognitive_complexity", "45");
        file2.put("ncloc", "200");
        
        // File 3: 
        Map<String,String> file3 = new HashMap<>();
        file3.put("complexity", "8");
        file3.put("cognitive_complexity", "12");
        file3.put("ncloc", "50");
        
        componentsTest.add(file1);
        componentsTest.add(file2);
        componentsTest.add(file3);
        components.setComponentsList(componentsTest);

        Map<String, Double> metricStats = components.getMetricStats();
        
        // Test that max values are NOT equal to sum values
        double sumComplexity = 10 + 32 + 8; // = 50
        double sumCognitive = 15 + 45 + 12; // = 72  
        double sumNcloc = 100 + 200 + 50; // = 350
        
        // Verify max values are correct (not sums)
        assertTrue(metricStats.containsKey("maxcomplexity"), "maxcomplexity should be present");
        assertTrue(metricStats.containsKey("maxcognitive_complexity"), "maxcognitive_complexity should be present");
        assertTrue(metricStats.containsKey("maxncloc"), "maxncloc should be present");
        
        double maxComplexity = metricStats.get("maxcomplexity");
        double maxCognitive = metricStats.get("maxcognitive_complexity");
        double maxNcloc = metricStats.get("maxncloc");
        
        // These should be max values, not sums
        assertNotEquals(sumComplexity, maxComplexity, "maxcomplexity should not equal sum (would indicate bug)");
        assertNotEquals(sumCognitive, maxCognitive, "maxcognitive_complexity should not equal sum (would indicate bug)");
        assertNotEquals(sumNcloc, maxNcloc, "maxncloc should not equal sum (would indicate bug)");
        
        // Verify they are actually the correct max values
        assertTrue(maxComplexity == 32.0, "maxcomplexity should be 32 (the highest individual file complexity)");
        assertTrue(maxCognitive == 45.0, "maxcognitive_complexity should be 45 (the highest individual file cognitive complexity)");
        assertTrue(maxNcloc == 200.0, "maxncloc should be 200 (the highest individual file ncloc)");
        
        System.out.println("✓ Fix verified: Max metrics are calculated correctly");
        System.out.println("  maxcomplexity: " + maxComplexity + " (expected: 32, sum would be: " + sumComplexity + ")");
        System.out.println("  maxcognitive_complexity: " + maxCognitive + " (expected: 45, sum would be: " + sumCognitive + ")");
        System.out.println("  maxncloc: " + maxNcloc + " (expected: 200, sum would be: " + sumNcloc + ")");
    }
}