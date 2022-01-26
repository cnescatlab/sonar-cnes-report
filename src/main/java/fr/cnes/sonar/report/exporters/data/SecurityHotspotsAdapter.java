package fr.cnes.sonar.report.exporters.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.model.SecurityHotspot;
import fr.cnes.sonar.report.utils.StringManager;

/**
 * Class used to format Security Hotspots data for reports
 */
public class SecurityHotspotsAdapter {

    /**
     * Property to get the list of issues types
     */
    private static final String SECURITY_HOTSPOTS_PRIORITIES = "securityhotspots.priorities";
    /**
     * String for count
     */
    private static final String COUNT = "count";
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(SecurityHotspotsAdapter.class.getName());

    private SecurityHotspotsAdapter() {
    }

    /**
     * Prepare list of resources to be print in a table
     * Data are lines containing the number of security hotspots by review priority
     * and security category
     * 
     * @param report report from which to extract resources
     * @return list of lists of strings
     */
    public static List<List<String>> getSecurityHotspotsByCategoryAndPriority(Report report) {
        // result to return
        final List<List<String>> result = new ArrayList<>();

        final Map<String, String> categories = StringManager.getSecurityHotspotsCategories();
        final List<String> priorities = new ArrayList<>(
                Arrays.asList(StringManager.getProperty(SECURITY_HOTSPOTS_PRIORITIES).split(",")));

        // accumulator for the number of occurrences for each priority
        LinkedHashMap<String, Integer> countPerPriority = new LinkedHashMap<>();
        for (String priority : priorities) {
            countPerPriority.put(priority, 0);
        }

        for (Map.Entry<String, String> entry : categories.entrySet()) {
            String categoryKey = entry.getKey();
            String categoryName = entry.getValue();
            // list of items for a line of the table
            final List<String> row = new ArrayList<>();
            for (SecurityHotspot securityHotspot : report.getToReviewSecurityHotspots()) {
                if (securityHotspot.getSecurityCategory().equals(categoryKey)) {
                    // increment the count of the priority
                    countPerPriority.put(securityHotspot.getVulnerabilityProbability(),
                            countPerPriority.get(securityHotspot.getVulnerabilityProbability()) + 1);
                }
            }
            // add data to the row
            row.add(categoryName);
            for (String priority : priorities) {
                row.add(String.valueOf(countPerPriority.get(priority)));
                // reset the count
                countPerPriority.put(priority, 0);
            }
            // add row to the result
            result.add(row);
        }
        return result;
    }

    /**
     * Get formatted security hotspots summary
     * 
     * @param report report from which to export resources
     * @return security hotspots list
     */
    public static List<List<String>> getSecurityHotspots(Report report) {
        // result to return
        final List<List<String>> result = new ArrayList<>();

        // aggregated security hotspots data
        HashMap<String, LinkedHashMap<String, String>> data = new HashMap<>();

        for (SecurityHotspot securityHotspot : report.getToReviewSecurityHotspots()) {
            // get the key of the security hotspot corresponding rule
            String key = securityHotspot.getRule();
            if (!data.containsKey(key)) {
                // fill data
                final Rule rule = report.getRule(key);
                if (rule == null) {
                    LOGGER.warning(
                            "A security hotspot was ignored because its corresponding rule was not found in project quality profiles.");
                } else {
                    LinkedHashMap<String, String> fieldsValues = new LinkedHashMap<>();
                    fieldsValues.put("category",
                            StringManager.getSecurityHotspotsCategories().get(securityHotspot.getSecurityCategory()));
                    fieldsValues.put("name", rule.getName());
                    fieldsValues.put("priority", securityHotspot.getVulnerabilityProbability());
                    fieldsValues.put("severity", rule.getSeverity());
                    fieldsValues.put(COUNT, String.valueOf(1));
                    data.put(key, fieldsValues);
                }
            } else {
                // increment rule count
                String newCount = String.valueOf(Integer.valueOf(data.get(key).get(COUNT)) + 1);
                data.get(key).put(COUNT, newCount);
            }
        }
        // fill result with data
        for (LinkedHashMap<String, String> fieldsValues : data.values()) {
            List<String> row = new ArrayList<>(fieldsValues.values());
            result.add(row);
        }
        return result;
    }

}
