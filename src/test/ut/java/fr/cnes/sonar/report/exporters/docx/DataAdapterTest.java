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

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataAdapterTest extends CommonTest{

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
        expected.put("XX-COMMENTDENSITY-XX", "1.0");
        expected.put("XX-MAXNCLOC-XX", "unknown");
        expected.put("XX-QUALITYGATEFILE-XX", "CNES.xml");
        expected.put("XX-MAXCOGNITIVECOMPLEXITY-XX", "unknown");
        expected.put("XX-QUALITYPROFILENAME-XX", "");
        expected.put("XX-MINCOMMENTDENSITY-XX", "unknown");
        expected.put("XX-QUALITYGATE-XX", "OK.png");
        expected.put("XX-MAXDUPLICATION-XX", "unknown");
        expected.put("XX-QUALITYGATENAME-XX", "CNES");
        expected.put("XX-QUALITYPROFILEFILE-XX", "");
        expected.put("XX-MINDUPLICATION-XX", "unknown");
        expected.put("XX-AUTHOR-XX", "Lequal");
        expected.put("XX-MINCOMPLEXITY-XX", "unknown");
        expected.put("XX-MAXCOMPLEXITY-XX", "unknown");
        expected.put("XX-SECURITY-XX", "C.png");
        expected.put("XX-SECURITYREVIEW-XX", "D.png");
        expected.put("XX-MINCOGNITIVECOMPLEXITY-XX", "unknown");
        expected.put("XX-DATE-XX", new Date().toString().substring(0,16));
        expected.put("XX-RELIABILITY-XX", "A.png");
        expected.put("XX-COVERAGE-XX", "1.0");
        expected.put("XX-VERSION-XX", "Version");
        expected.put("XX-LINES-XX", "1.0");
        expected.put("XX-MAINTAINABILITY-XX", "B.png");
        expected.put("XX-MINCOVERAGE-XX", "0.0");
        expected.put("XX-DESCRIPTION-XX", "Short description");
        expected.put("XX-XXXXXXXXXXXXXXX-XX", "3224");
        expected.put("XX-MINNCLOC-XX", "unknown");
        expected.put("XX-MAXCOMMENTDENSITY-XX", "unknown");
        expected.put("XX-MEDIANNCLOC-XX", "unknown");
        expected.put("XX-COMPLIANCE-XX", "-66.7");
        Assert.assertEquals(expected, placeHolders);

        // Add the maxcoverage to cancel the unknown error
        Map<String, Double> metricsStats = report.getMetricsStats();
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
        expected.replace("XX-MEDIANNCLOC-XX", "1.0");
        expected.put("XX-MAXCOVERAGE-XX", "1.0");
        Map<String,String> newPlaceHolders = DataAdapter.loadPlaceholdersMap(report);
        Assert.assertEquals(expected, newPlaceHolders);
    }
}
