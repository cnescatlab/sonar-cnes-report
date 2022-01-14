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

import static fr.cnes.sonar.report.utils.MeasureConverter.getIntMeasureFromString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.math3.util.Precision;

import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.model.QualityProfile;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.model.SecurityHotspot;
import fr.cnes.sonar.report.utils.StringManager;

/**
 * Format resources in different structure to have an easier use
 */
public final class DataAdapter {

    /**
     * Regex to delete html tags
     */
    private static final String DELETE_HTML_TAGS_REGEX = "<[^>]*>";
    /**
     * just a question mark
     */
    private static final String QUESTION_MARK = "?";
    /**
     * Placeholder for author
     */
    private static final String AUTHOR_PLACEHOLDER = "XX-AUTHOR-XX";
    /**
     * Placeholder for date
     */
    private static final String DATE_PLACEHOLDER = "XX-DATE-XX";
    /**
     * Placeholder for project's name
     */
    private static final String PROJECTNAME_PLACEHOLDER = "XX-PROJECTNAME-XX";
    /**
     * Placeholder for the quality gate's anme
     */
    private static final String QUALITYGATENAME_PLACEHOLDER = "XX-QUALITYGATENAME-XX";
    /**
     * Placeholder for quality gate's filename
     */
    private static final String QUALITYGATEFILE_PLACEHOLDER = "XX-QUALITYGATEFILE-XX";
    /**
     * Placeholder for quality profile's name
     */
    private static final String QUALITYPROFILENAME_PLACEHOLDER = "XX-QUALITYPROFILENAME-XX";
    /**
     * Placeholder for quality profile's filenames
     */
    private static final String QUALITYPROFILEFILE_PLACEHOLDER = "XX-QUALITYPROFILEFILE-XX";
    /**
     * Placeholder for compliance
     */
    private static final String COMPLIANCE_PLACEHOLDER = "XX-COMPLIANCE-XX";

    /**
     * Placeholders for complexity metrics
     */
    private static final String MINCOMPLEXITY_PLACEHOLDER = "XX-MINCOMPLEXITY-XX";
    private static final String MAXCOMPLEXITY_PLACEHOLDER = "XX-MAXCOMPLEXITY-XX";
    /**
    * Key to get complexity in metricstats
    */
    private static final String MINCOMPLEXITY_STATKEY = "mincomplexity";
    private static final String MAXCOMPLEXITY_STATKEY = "maxcomplexity";
    /**
     * Placeholders for NCLOC metrics (number of line of codes)
     */
    private static final String MINNCLOC_PLACEHOLDER = "XX-MINNCLOC-XX";
    private static final String MAXNCLOC_PLACEHOLDER = "XX-MAXNCLOC-XX";
    private static final String MEDIANNCLOC_PLACEHOLDER = "XX-MEDIANNCLOC-XX";
    /**
     * Key to get number of line of codes in metricstats
     */
    private static final String MINNCLOC_STATKEY = "minncloc";
    private static final String MAXNCLOC_STATKEY = "maxncloc";
    private static final String MEDIANNCLOC_STATKEY = "medianncloc";
    /**
     * Placeholders for comment density metrics
     */
    private static final String MINCOMMENTDENSITY_PLACEHOLDER = "XX-MINCOMMENTDENSITY-XX";
    private static final String MAXCOMMENTDENSITY_PLACEHOLDER = "XX-MAXCOMMENTDENSITY-XX";
    /**
     * Key to get comment_lines_density in metricstats
     */
    private static final String MINCOMMENTDENSITY_STATKEY = "mincomment_lines_density";
    private static final String MAXCOMMENTDENSITY_STATKEY = "maxcomment_lines_density";
    /**
     * Placeholders for duplications metrics
     */
    private static final String MINDUPLICATION_PLACEHOLDER = "XX-MINDUPLICATION-XX";
    private static final String MAXDUPLICATION_PLACEHOLDER = "XX-MAXDUPLICATION-XX";
    /**
     * Key to get duplications in metricstats
     */
    private static final String MINDUPLICATION_STATKEY = "minduplicated_lines_density";
    private static final String MAXDUPLICATION_STATKEY = "maxduplicated_lines_density";
    /**
     * Placeholders for cognitive complexity metrics
     */
    private static final String MINCOGNITIVECOMPLEXITY_PLACEHOLDER = "XX-MINCOGNITIVECOMPLEXITY-XX";
    private static final String MAXCOGNITIVECOMPLEXITY_PLACEHOLDER = "XX-MAXCOGNITIVECOMPLEXITY-XX";
    /**
     * Key to get cognitive complexity in metricstats
     */
    private static final String MINCOGNITIVECOMPLEXITY_STATKEY = "mincognitive_complexity";
    private static final String MAXCOGNITIVECOMPLEXITY_STATKEY = "maxcognitive_complexity";
    /**
     * Placeholders for coverage metrics
     */
    private static final String MINCOVERAGE_PLACEHOLDER = "XX-MINCOVERAGE-XX";
    private static final String MAXCOVERAGE_PLACEHOLDER = "XX-MAXCOVERAGE-XX";
    /**
     * Key to get coverage in metricstats
     */
    private static final String MINCOVERAGE_STATKEY = "mincoverage";
    private static final String MAXCOVERAGE_STATKEY = "maxcoverage";
    /**
     * extension for xml file
     */
    private static final String XML_EXTENSION = ".xml";
    /**
     * Field name of a measure rating
     */
    private static final String RATING = "rating";
    /**
     * String value of numerical mark given by SonarQube
     */
    private static final String MARK_1_NUMBER = "1.0";
    /**
     * String value of numerical mark given by SonarQube
     */
    private static final String MARK_2_NUMBER = "2.0";
    /**
     * String value of numerical mark given by SonarQube
     */
    private static final String MARK_3_NUMBER = "3.0";
    /**
     * String value of numerical mark given by SonarQube
     */
    private static final String MARK_4_NUMBER = "4.0";
    /**
     * String value of numerical mark given by SonarQube
     */
    private static final String MARK_5_NUMBER = "5.0";
    /**
     * field name containing the mark for a measure
     */
    private static final String VALUE = "value";
    /**
     * String value of letter mark given by SonarQube
     */
    private static final String MARK_1_LETTER = "A";
    /**
     * String value of letter mark given by SonarQube
     */
    private static final String MARK_2_LETTER = "B";
    /**
     * String value of letter mark given by SonarQube
     */
    private static final String MARK_3_LETTER = "C";
    /**
     * String value of letter mark given by SonarQube
     */
    private static final String MARK_4_LETTER = "D";
    /**
     * String value of letter mark given by SonarQube
     */
    private static final String MARK_5_LETTER = "E";
    /**
     * Placeholder for reliability mark
     */
    private static final String RELIABILITY_PLACEHOLDER = "XX-RELIABILITY-XX";
    /**
     * Placeholder for duplication rate
     */
    private static final String DUPLICATION_PLACEHOLDER = "XX-DUPLICATION-XX";
    /**
     * Placeholder for comment density
     */
    private static final String COMMENTDENSITY_PLACEHOLDER = "XX-COMMENTDENSITY-XX";
    /**
     * Placeholder for maintainability mark
     */
    private static final String MAINTAINABILITY_PLACEHOLDER = "XX-MAINTAINABILITY-XX";
    /**
     * Placeholder for coverage rate
     */
    private static final String COVERAGE_PLACEHOLDER = "XX-COVERAGE-XX";
    /**
     * Placeholder for complexity mark
     */
    private static final String LINES_PLACEHOLDER = "XX-LINES-XX";
    /**
     * Placeholder for quality gate's status
     */
    private static final String QUALITYGATE_PLACEHOLDER = "XX-QUALITYGATE-XX";
    /**
     * Placeholder for security mark
     */
    private static final String SECURITY_PLACEHOLDER = "XX-SECURITY-XX";
    /**
     * Placeholder for security review mark
     */
    private static final String SECURITY_REVIEW_PLACEHOLDER = "XX-SECURITYREVIEW-XX";
    /**
     * Placeholder for the project's version
     */
    private static final String VERSION_PLACEHOLDER = "XX-VERSION-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String DESCRIPTION_PLACEHOLDER = "XX-DESCRIPTION-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String TOTAL_TESTS_PLACEHOLDER = "XX-TOTAL-TESTS-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String TEST_SUCCESS_RATE_PLACEHOLDER = "XX-TEST-SUCCESS-RATE-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String SKIPPED_TESTS_PLACEHOLDER = "XX-SKIPPED-TESTS-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String TEST_ERRORS_PLACEHOLDER = "XX-TEST-ERRORS-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String TEST_FAILURES_PLACEHOLDER = "XX-TEST-FAILURES-XX";
    /**
     * Default placeholder
     */
    private static final String DEFAULT_PLACEHOLDER = "XX-XXXXXXXXXXXXXXX-XX";
    /**
     * Field in json response for reliability mark
     */
    private static final String RELIABILITY_RATING = "reliability_rating";
    /**
     * Field in json response for duplications
     */
    private static final String DUPLICATED_LINES_DENSITY = "duplicated_lines_density";
    /**
     * Field in json response for comment density
     */
    private static final String COMMENT_LINES_DENSITY = "comment_lines_density";
    /**
     * Field in json response for maintainability mark
     */
    private static final String SQALE_RATING = "sqale_rating";
    /**
     * Field in json response for coverage
     */
    private static final String COVERAGE = "coverage";
    /**
     * Field in json response for number of code lines
     */
    private static final String NCLOC = "ncloc";
    /**
     * Field in json response for quality gate's status
     */
    private static final String ALERT_STATUS = "alert_status";
    /**
     * Field in json response for security mark
     */
    private static final String SECURITY_RATING = "security_rating";
    /**
     * Field in json response for security review mark
     */
    private static final String SECURITY_REVIEW_RATING = "security_review_rating";
    /**
     * Property to get the list of issues severities
     */
    private static final String ISSUES_SEVERITIES = "issues.severities";
    /**
     * Property to get the list of issues types
     */
    private static final String ISSUES_TYPES = "issues.types";
    /**
     * Property to get the list of issues types
     */
    private static final String SECURITY_HOTSPOTS_PRIORITIES = "securityhotspots.priorities";
    /**
     * Field in json response for number of code lines per language
     */
    private static final String NCLOC_PER_LANGUAGE = "ncloc_language_distribution";
    /**
     * Field in json response for reliability remediation effort
     */
    private static final String RELIABILITY_REMEDIATION_EFFORT = "reliability_remediation_effort";
    /**
     * Field in json response for security remediation effort
     */
    private static final String SECURITY_REMEDIATION_EFFORT = "security_remediation_effort";
    /**
     * Field in json response for sqale index
     */
    private static final String SQALE_INDEX = "sqale_index";
    /**
     * Field in json response for total tests
     */
    private static final String TOTAL_TESTS = "tests";
    /**
     * Field in json response for total tests
     */
    private static final String TEST_SUCCESS_RATE = "test_success_density";
    /**
     * Field in json response for total tests
     */
    private static final String SKIPPED_TESTS = "skipped_tests";
    /**
     * Field in json response for total tests
     */
    private static final String TEST_ERRORS = "test_errors";
    /**
     * Field in json response for total tests
     */
    private static final String TEST_FAILURES = "test_failures";
    /**
     * Just an equals sign
     */
    private static final String EQUALS = "=";
    /**
     * Just a semicolon
     */
    private static final String SEMICOLON = ";";
    /**
     * Label for the total line
     */
    private static final String TOTAL = "Total";
    /**
     * Just an empty string
     */
    private static final String EMPTY = "";
    /**
     * String for count
     */
    private static final String COUNT = "count";
    /**
     * PNG extension
     */
    private static final String PNG_EXTENSION = ".png";
    /**
     * Technical debt display format
     */
    private static final String TECHNICAL_DEBT_FORMAT = "%sd %sh %smin";
    /**
     * String contained in a metric with a status
     */
    private static final String STATUS = "status";
    /**
     * Value "OK" of the parameter "status" of the JSON response
     */
    private static final String OK = "OK";
    /**
     * Value "ERROR" of the parameter "status" of the JSON response
     */
    private static final String ERROR = "ERROR";
    /** Logger of this class */
    private static final Logger LOGGER = Logger.getLogger(DataAdapter.class.getName());
    /**
     * Private constructor to forbid instantiation of this class
     */
    private DataAdapter(){}

    /**
     * Prepare list of resources to be print in a table
     * Data are lines containing the number of issues by severity and type
     * @param report report from which to extract resources
     * @return list of lists of strings
     */
    public static List<List<String>> getTypes(Report report) {
        // result to return
        final List<List<String>> results = new ArrayList<>();

        final List<String> types = new ArrayList<>(Arrays.asList(StringManager.getProperty(ISSUES_TYPES).split(",")));
        final List<String> severities = getReversedIssuesSeverities();

        // accumulator for the number of occurrences for each severity
        LinkedHashMap<String,Integer> countPerSeverity = new LinkedHashMap<>();
        for (String severity : severities) {
            countPerSeverity.put(severity, 0);
        }

        for(String type : types) {
            //List of items for each line of the table
            final List<String> row = new ArrayList<>();
            for(Issue issue : report.getIssues()) {
                if(issue.getType().equals(type)) {
                    // increment the count of the severity
                    countPerSeverity.put(issue.getSeverity(),
                            countPerSeverity.get(issue.getSeverity()) + 1);
                }
            }
            // add data to the row
            row.add(type);
            for (String severity : severities) {
                row.add(String.valueOf(countPerSeverity.get(severity)));
                // reset the count
                countPerSeverity.put(severity, 0);
            }
            // add row to the result
            results.add(row);
        }
        return results;
    }

    /**
     * Getter for reversed ISSUES_SEVERITIES
     * @return reversed ISSUES_SEVERITIES
     */
    public static List<String> getReversedIssuesSeverities() {
        List<String> issuesSeverities = new ArrayList<>(Arrays.asList(StringManager.getProperty(ISSUES_SEVERITIES).split(",")));
        Collections.reverse(issuesSeverities);
        return issuesSeverities;
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
     * Get formatted metrics to be printed
     * @param report Report from which to extract resources
     * @return list of list of string (metric,measure)
     */
    public static List<List<String>> getMetrics(Report report) {
        // result to return
        final List<List<String>> metrics = new ArrayList<>();

        // construct each metric
        for (Measure m : report.getMeasures()) {
            metrics.add(Arrays.asList(m.getMetric(),m.getValue()));
        }

        return metrics;
    }

    /**
     * Get formatted issues summary
     * @param report report from which to export resources
     * @return issues list
     */
    public static List<List<String>> getIssues(Report report) {
        final List<List<String>> issues = new ArrayList<>();  // result to return

        // Get the issues' id
        final Map<String, Long> items = report.getIssuesFacets();

        Map<String,Long> sortedItems =  new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(items);

        for (Map.Entry<String, Long> v : sortedItems.entrySet()) { // construct each issues
            final List<String> issue = new ArrayList<>();
            final Rule rule = report.getRule(v.getKey());
            if(rule!=null) { // if the rule is found, fill information
                // add name
                issue.add(rule.getName());
                // add description
                issue.add(rule.getHtmlDesc()
                        .replaceAll(DELETE_HTML_TAGS_REGEX, StringManager.EMPTY));
                // add type
                issue.add(rule.getType());
                // add severity
                issue.add(rule.getSeverity());
                // add number
                issue.add(Long.toString(v.getValue()));
            } else { // else set just known information
                // add name
                issue.add(v.getKey());
                // add description
                issue.add(QUESTION_MARK);
                // add type
                issue.add(QUESTION_MARK);
                // add severity
                issue.add(QUESTION_MARK);
                // add number
                issue.add(Long.toString(v.getValue()));
            }

            issues.add(issue);
        }

        return issues;
    }

    /**
     * Get formatted security hotspots summary
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
            if(!data.containsKey(key)) {
                // fill data
                final Rule rule = report.getRule(key);
                if (rule == null) {
                    LOGGER.warning("A security hotspot was ignored because its corresponding rule was not found in project quality profiles.");
                } else {
                    LinkedHashMap<String, String> fieldsValues = new LinkedHashMap<>();
                    fieldsValues.put("category", StringManager.getSecurityHotspotsCategories().get(securityHotspot.getSecurityCategory()));
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

    /**
     * Get formatted quality gate status summary
     * @param report report from which to export resources
     * @return quality gate conditions statuses
     */
    public static List<List<String>> getQualityGateStatus(Report report) {
        // result to return
        final List<List<String>> result = new ArrayList<>();
        // add a row for each condition
        for (Map.Entry<String, String> entry : report.getQualityGateStatus().entrySet()) {
            final List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue());
            result.add(row);
        }
        return result;
    }

    /**
     * Load in a map all the placeholder (key) with the corresponding replacement value (value)
     * @param report Report from which resources are extracted
     * @return the placeholders map
     */
    public static Map<String, String> loadPlaceholdersMap(Report report) {
        // final map to return
        final Map<String, String> replacementValues = new HashMap<>();
        // Replacement of placeholder
        // report meta resources placeholders
        replacementValues.put(
                AUTHOR_PLACEHOLDER,
                report.getProjectAuthor());
        replacementValues.put(
                VERSION_PLACEHOLDER,
                report.getProjectVersion());
        replacementValues.put(
                DESCRIPTION_PLACEHOLDER,
                report.getProjectDescription());
        replacementValues.put(
                DATE_PLACEHOLDER,
                report.getProjectDate());
        replacementValues.put(
                PROJECTNAME_PLACEHOLDER,
                report.getProjectName());
        // configuration placeholders
        replacementValues.put(
                QUALITYGATENAME_PLACEHOLDER,
                report.getQualityGate().getName());
        replacementValues.put(
                QUALITYGATEFILE_PLACEHOLDER,
                report.getQualityGate().getName() + XML_EXTENSION);
        replacementValues.put(
                QUALITYPROFILENAME_PLACEHOLDER,
                report.getQualityProfilesName());
        replacementValues.put(
                QUALITYPROFILEFILE_PLACEHOLDER,
                report.getQualityProfilesFilename());
        try {
            // complexity metrics
            replacementValues.put(
                    MINCOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOMPLEXITY_STATKEY).toString()
            );
            replacementValues.put(
                    MAXCOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOMPLEXITY_STATKEY).toString()
            );

            // number of line of codes metrics
            replacementValues.put(
                    MINNCLOC_PLACEHOLDER,
                    report.getMetricsStats().get(MINNCLOC_STATKEY).toString()
            );
            replacementValues.put(
                    MAXNCLOC_PLACEHOLDER,
                    report.getMetricsStats().get(MAXNCLOC_STATKEY).toString()
            );
            replacementValues.put(MEDIANNCLOC_PLACEHOLDER,
                    report.getMetricsStats().get(MEDIANNCLOC_STATKEY).toString()
            );

            //comment density
            replacementValues.put(
                    MINCOMMENTDENSITY_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOMMENTDENSITY_STATKEY).toString()
            );
            replacementValues.put(
                    MAXCOMMENTDENSITY_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOMMENTDENSITY_STATKEY).toString()
            );

            // duplications
            replacementValues.put(
                    MINDUPLICATION_PLACEHOLDER,
                    report.getMetricsStats().get(MINDUPLICATION_STATKEY).toString()
            );
            replacementValues.put(
                    MAXDUPLICATION_PLACEHOLDER,
                    report.getMetricsStats().get(MAXDUPLICATION_STATKEY).toString()
            );

            // cognitive complexity
            replacementValues.put(
                    MINCOGNITIVECOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOGNITIVECOMPLEXITY_STATKEY).toString()
            );
            replacementValues.put(
                    MAXCOGNITIVECOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOGNITIVECOMPLEXITY_STATKEY).toString()
            );

            // coverage
            replacementValues.put(
                    MINCOVERAGE_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOVERAGE_STATKEY).toString()
            );
            replacementValues.put(
                    MAXCOVERAGE_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOVERAGE_STATKEY).toString()
            );
        }
        catch (NullPointerException e){
            ArrayList<String> placeholders = new ArrayList<>();
            placeholders.add(MINCOMMENTDENSITY_PLACEHOLDER);
            placeholders.add(MAXCOMMENTDENSITY_PLACEHOLDER);
            placeholders.add(MINCOMPLEXITY_PLACEHOLDER);
            placeholders.add(MAXCOMPLEXITY_PLACEHOLDER);
            placeholders.add(MINNCLOC_PLACEHOLDER);
            placeholders.add(MAXNCLOC_PLACEHOLDER);
            placeholders.add(MEDIANNCLOC_PLACEHOLDER);
            placeholders.add(MINDUPLICATION_PLACEHOLDER);
            placeholders.add(MAXDUPLICATION_PLACEHOLDER);
            placeholders.add(MINCOGNITIVECOMPLEXITY_PLACEHOLDER);
            placeholders.add(MAXCOGNITIVECOMPLEXITY_PLACEHOLDER);
            for(String placeholder: placeholders){
                replacementValues.put(
                        placeholder,
                        "unknown"
                );
            }
        }


        // Synthesis placeholders
        for (Measure m : report.getMeasures()) {
            final String placeholder = getPlaceHolderName(m.getMetric());
            String value;

            // convert the metric value to a PNG if possible
            if(m.getMetric().contains(RATING)) {
                value = numberToLetter(m.getValue());
            } else if(m.getMetric().contains(STATUS)) {
                value = formatStatus(m.getValue());
            } else {
                value = m.getValue();
            }

            replacementValues.put(
                    placeholder,
                    value);

            // Sometime, project did not have coverage
            if (replacementValues.get(COVERAGE_PLACEHOLDER) == null){
                replacementValues.put(COVERAGE_PLACEHOLDER, QUESTION_MARK);
            }
        }
        replacementValues.put(COMPLIANCE_PLACEHOLDER, getCompliance(report));
        return replacementValues;
    }

    /**
     * Convert the numeric note to a letter
     * @param value numeric note
     * @return a letter
     */
    private static String numberToLetter(String value) {
        final String res;
        // make the link between numbers and letters
        switch (value) {
            case MARK_1_NUMBER:
                res = MARK_1_LETTER.concat(PNG_EXTENSION);
                break;
            case MARK_2_NUMBER:
                res = MARK_2_LETTER.concat(PNG_EXTENSION);
                break;
            case MARK_3_NUMBER:
                res = MARK_3_LETTER.concat(PNG_EXTENSION);
                break;
            case MARK_4_NUMBER:
                res = MARK_4_LETTER.concat(PNG_EXTENSION);
                break;
            case MARK_5_NUMBER:
                res = MARK_5_LETTER.concat(PNG_EXTENSION);
                break;
            default:
                res = VALUE;
                break;
        }
        return res;
    }

    /**
     * Format a status
     * @param status status value
     * @return a formatted status
     */
    private static String formatStatus(String status) {
        final String res;
        switch (status) {
            case OK:
                res = OK.concat(PNG_EXTENSION);
                break;
            case ERROR:
                res = ERROR.concat(PNG_EXTENSION);
                break;
            default:
                res = status;
                break;
        }
        return res;
    }

    /**
     * Give the corresponding placeholder
     * @param metric value whose it have to find the placeholder
     * @return value of the place holder
     */
    private static String getPlaceHolderName(String metric) {
        final String res;
        switch (metric) {
            case RELIABILITY_RATING:
                res = RELIABILITY_PLACEHOLDER;
                break;
            case DUPLICATED_LINES_DENSITY:
                res = DUPLICATION_PLACEHOLDER;
                break;
            case COMMENT_LINES_DENSITY:
                res = COMMENTDENSITY_PLACEHOLDER;
                break;
            case SQALE_RATING:
                res = MAINTAINABILITY_PLACEHOLDER;
                break;
            case COVERAGE:
                res = COVERAGE_PLACEHOLDER;
                break;
            case NCLOC:
                res = LINES_PLACEHOLDER;
                break;
            case ALERT_STATUS:
                res = QUALITYGATE_PLACEHOLDER;
                break;
            case SECURITY_RATING:
                res = SECURITY_PLACEHOLDER;
                break;
            case SECURITY_REVIEW_RATING:
                res = SECURITY_REVIEW_PLACEHOLDER;
                break;
            case TOTAL_TESTS:
                res = TOTAL_TESTS_PLACEHOLDER;
                break;
            case TEST_SUCCESS_RATE:
                res = TEST_SUCCESS_RATE_PLACEHOLDER;
                break;
            case SKIPPED_TESTS:
                res = SKIPPED_TESTS_PLACEHOLDER;
                break;
            case TEST_ERRORS:
                res = TEST_ERRORS_PLACEHOLDER;
                break;
            case TEST_FAILURES:
                res = TEST_FAILURES_PLACEHOLDER;
                break;
            default:
                res = DEFAULT_PLACEHOLDER;
                break;
        }
        return res;
    }


    /**
     * Load in a list all the volume metrics with the corresponding value (value)
     * @param report Report from which resources are extracted
     * @return the volumes list
     */
    public static List<List<String>> getVolumes(Report report) {
        // result to return
        final List<List<String>> volumes = new ArrayList<>();

        // find metrics per language and for all
        final String perLanguage = findMeasure(report.getMeasures(), NCLOC_PER_LANGUAGE);
        final String total = findMeasure(report.getMeasures(), NCLOC);

        // split raw string data into list of list of string
        // to get a relevant table
        final List<String> firstSplit = Arrays.asList(perLanguage.split(SEMICOLON));
        firstSplit.forEach(x -> volumes.add(Arrays.asList(x.split(EQUALS))));

        // replace language's key by language's name
        Language language;
        for(List<String> l : volumes) {
            language = report.getProject().getLanguage(l.get(0));
            if(null!=language) {
                l.set(0, language.getName());
            }
        }

        // add the total lines
        volumes.add(Arrays.asList(TOTAL, total));

        return volumes;
    }

    /**
     * Get formatted technical debt summary
     * @param report Report from which resources are extracted
     * @return detailed technical debt
     */
    public static List<List<String>> getDetailedTechnicalDebt(Report report) {
        // result to return
        final List<List<String>> detailedTechnicalDebt = new ArrayList<>();

        // get metrics values needed
        List<Measure> measures = report.getMeasures();
        int reliabilityDebt = getIntMeasureFromString(findMeasure(measures, RELIABILITY_REMEDIATION_EFFORT));
        int securityDebt = getIntMeasureFromString(findMeasure(measures, SECURITY_REMEDIATION_EFFORT));
        int maintainabilityDebt = getIntMeasureFromString(findMeasure(measures, SQALE_INDEX));
        int totalTechnicalDebt = reliabilityDebt + securityDebt + maintainabilityDebt;

        String reliabilityDebtFormatted;
        String securityDebtFormatted;
        String maintainabilityDebtFormatted;
        String totalTechnicalDebtFormatted;

        // convert metrics values to days/hours/minutes format
        if (reliabilityDebt != 0) {
            reliabilityDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, reliabilityDebt/8/60, reliabilityDebt/60%8, reliabilityDebt%60);
        } else {
            reliabilityDebtFormatted = "-";
        }
        
        if (securityDebt != 0) {
            securityDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, securityDebt/8/60, securityDebt/60%8, securityDebt%60);
        } else {
            securityDebtFormatted = "-";
        }
        
        if (maintainabilityDebt != 0) {
            maintainabilityDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, maintainabilityDebt/8/60, maintainabilityDebt/60%8, maintainabilityDebt%60);
        } else {
            maintainabilityDebtFormatted = "-";
        }
        
        if (totalTechnicalDebt != 0) {
            totalTechnicalDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, totalTechnicalDebt/8/60, totalTechnicalDebt/60%8, totalTechnicalDebt%60);
        } else {
            totalTechnicalDebtFormatted = "-";
        }

        // create the row
        final List<String> row = new ArrayList<>();
        row.add(reliabilityDebtFormatted);
        row.add(securityDebtFormatted);
        row.add(maintainabilityDebtFormatted);
        row.add(totalTechnicalDebtFormatted);

        // add the row to the result
        detailedTechnicalDebt.add(row);

        return detailedTechnicalDebt;
    }

    /**
     * Return the value of a given metrics
     * @param measures List of measures to browse
     * @param metric metric to search
     * @return a String containing the measure
     */
    private static String findMeasure(List<Measure> measures, String metric) {
        // result that can be null
        String result = EMPTY;
        // retrieve all measures to browse
        final Iterator<Measure> iterator = measures.iterator();
        Measure current;
        // search by name the measure corresponding to metric
        while (iterator.hasNext() && result.equals(EMPTY)) {
            current = iterator.next();
            if(current.getMetric().equals(metric)) {
                result = current.getValue();
            }
        }
        return result;
    }

    /**
     * Return the compliance to the coding standard (% of rules in all Quality Profiles that are not violated)
     * @param report Report from which resources are extracted
     * @return the compliance
     */
    private static String getCompliance(Report report) {
        int rulesNumber = 0;
        double compliance;

        for (QualityProfile qp : report.getQualityProfiles()) {
            rulesNumber += qp.getRules().size();
        }

        if (rulesNumber != 0) {
            Set<String> violatedRules = new HashSet<>();
            for (Issue issue : report.getIssues()) {
                violatedRules.add(issue.getRule());
            }
            for (SecurityHotspot securityHotspot : report.getToReviewSecurityHotspots()) {
                violatedRules.add(securityHotspot.getRule());
            }
            compliance = ((double)(rulesNumber - violatedRules.size()) / rulesNumber) * 100;
        } else {
            compliance = 0;
        }

        return String.valueOf(Precision.round(compliance, 1));
    }
}

/**
 * RuleComparator is used to compare 2 issues to sort them by type & severity
 */
class RuleComparator implements Comparator<String>{
    Report report;

    RuleComparator(Report report){
        this.report = report;
    }

    public int compare(String o1, String o2) {
        int compare = 0;

        if(o1.isEmpty() || o2.isEmpty()){compare = 1;}

        //If rule is removed in quality gate, the issue is send to the end of list
        if(report.getRule(o1) == null){compare = 1;}
        else if(report.getRule(o2) == null){compare = -1;}

        if (compare == 0){
            compare = report
                    .getRule(o1)
                    .getType()
                    .compareTo(
                            report.getRule(o2).getType()
                    );
        }

        if (compare == 0) {
            compare = report.getRule(o1).getSeverity().compareTo(
                    report.getRule(o2).getSeverity()
            );
        }

        if (compare == 0){
            compare = report.getRule(o1).getKey().compareTo(
                    report.getRule(o2).getKey()
            );
        }

        return compare;
    }
}
