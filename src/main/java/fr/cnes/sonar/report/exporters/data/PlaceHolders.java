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

package fr.cnes.sonar.report.exporters.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.model.Report;

/**
 * Class used to link data with report templates placeholders
 */
public class PlaceHolders {

    private PlaceHolders() {
    }

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
     * Placeholder for the project's version
     */
    private static final String VERSION_PLACEHOLDER = "XX-VERSION-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String DESCRIPTION_PLACEHOLDER = "XX-DESCRIPTION-XX";
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
     * Field in json response for number of code lines
     */
    private static final String NCLOC = "ncloc";
    /**
     * Field in json response for duplications
     */
    private static final String DUPLICATED_LINES_DENSITY = "duplicated_lines_density";
    /**
     * Field in json response for comment density
     */
    private static final String COMMENT_LINES_DENSITY = "comment_lines_density";
    /**
     * Placeholder for coverage rate
     */
    private static final String COVERAGE_PLACEHOLDER = "XX-COVERAGE-XX";
    /**
     * Field in json response for maintainability mark
     */
    private static final String SQALE_RATING = "sqale_rating";
    /**
     * Field in json response for coverage
     */
    private static final String COVERAGE = "coverage";
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
     * String contained in a metric with a status
     */
    private static final String STATUS = "status";
    /**
     * Just a zero value for empty metrics
     */
    private static final String ZERO_VALUE = "0";
    /**
     * Value "OK" of the parameter "status" of the JSON response
     */
    private static final String OK = "OK";
    /**
     * Value "ERROR" of the parameter "status" of the JSON response
     */
    private static final String ERROR = "ERROR";
    /**
     * PNG extension
     */
    private static final String PNG_EXTENSION = ".png";
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
     * Load in a map all the placeholder (key) with the corresponding replacement
     * value (value)
     * 
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

        // In some cases, those metrics may not exist yet and those methods may throw an
        // exception
        try {
            // complexity metrics
            replacementValues.put(
                    MINCOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOMPLEXITY_STATKEY).toString());
            replacementValues.put(
                    MAXCOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOMPLEXITY_STATKEY).toString());

            // number of line of codes metrics
            replacementValues.put(
                    MINNCLOC_PLACEHOLDER,
                    report.getMetricsStats().get(MINNCLOC_STATKEY).toString());
            replacementValues.put(
                    MAXNCLOC_PLACEHOLDER,
                    report.getMetricsStats().get(MAXNCLOC_STATKEY).toString());
            replacementValues.put(MEDIANNCLOC_PLACEHOLDER,
                    report.getMetricsStats().get(MEDIANNCLOC_STATKEY).toString());

            // comment density
            replacementValues.put(
                    MINCOMMENTDENSITY_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOMMENTDENSITY_STATKEY).toString());
            replacementValues.put(
                    MAXCOMMENTDENSITY_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOMMENTDENSITY_STATKEY).toString());

            // duplications
            replacementValues.put(
                    MINDUPLICATION_PLACEHOLDER,
                    report.getMetricsStats().get(MINDUPLICATION_STATKEY).toString());
            replacementValues.put(
                    MAXDUPLICATION_PLACEHOLDER,
                    report.getMetricsStats().get(MAXDUPLICATION_STATKEY).toString());

            // cognitive complexity
            replacementValues.put(
                    MINCOGNITIVECOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOGNITIVECOMPLEXITY_STATKEY).toString());
            replacementValues.put(
                    MAXCOGNITIVECOMPLEXITY_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOGNITIVECOMPLEXITY_STATKEY).toString());

            // coverage
            replacementValues.put(
                    MINCOVERAGE_PLACEHOLDER,
                    report.getMetricsStats().get(MINCOVERAGE_STATKEY).toString());
            replacementValues.put(
                    MAXCOVERAGE_PLACEHOLDER,
                    report.getMetricsStats().get(MAXCOVERAGE_STATKEY).toString());
        } catch (NullPointerException e) {
            // If those metric does not exist yet, add empty values
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
            for (String placeholder : placeholders) {
                replacementValues.put(
                        placeholder,
                        "unknown");
            }
        }

        // Synthesis placeholders
        for (Measure m : report.getMeasures()) {
            final String placeholder = getPlaceHolderName(m.getMetric());
            String value;

            // convert the metric value to a PNG if possible
            if (m.getMetric().contains(RATING)) {
                value = numberToLetter(m.getValue());
            } else if (m.getMetric().contains(STATUS)) {
                value = formatStatus(m.getValue());
            } else {
                value = m.getValue();
            }

            replacementValues.put(
                    placeholder,
                    value);
        }

        // Sometimes, project do not have coverage or tests
        if (replacementValues.get(COVERAGE_PLACEHOLDER) == null) {
            replacementValues.put(COVERAGE_PLACEHOLDER, ZERO_VALUE);
            replacementValues.put(MINCOVERAGE_PLACEHOLDER, ZERO_VALUE);
            replacementValues.put(MAXCOVERAGE_PLACEHOLDER, ZERO_VALUE);
        }

        if (replacementValues.get(TOTAL_TESTS_PLACEHOLDER) == null) {
            replacementValues.put(TOTAL_TESTS_PLACEHOLDER, ZERO_VALUE);
            replacementValues.put(TEST_SUCCESS_RATE_PLACEHOLDER, ZERO_VALUE);
            replacementValues.put(SKIPPED_TESTS_PLACEHOLDER, ZERO_VALUE);
            replacementValues.put(TEST_ERRORS_PLACEHOLDER, ZERO_VALUE);
            replacementValues.put(TEST_FAILURES_PLACEHOLDER, ZERO_VALUE);            
        }

        replacementValues.put(COMPLIANCE_PLACEHOLDER, report.getCompliance());

        return replacementValues;
    }

    /**
     * Give the corresponding placeholder for a given metric
     * 
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
     * Convert the numeric note (maintainability, security, reliability) to a letter
     * (1.0 -> A, 2.0 -> B, ...)
     * 
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
     * Return Quality Gate status value dedicated PNG
     * 
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

}
