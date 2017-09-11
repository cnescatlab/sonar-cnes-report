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

import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.*;

import java.util.*;

/**
 * Format resources in different structure to have an easier use
 * @author lequal
 */
public class DataAdapter {

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
     * Placeholder for the project's version
     */
    private static final String VERSION_PLACEHOLDER = "XX-VERSION-XX";
    /**
     * Placeholder for the project's description
     */
    private static final String DESCRIPTION_PLACEHOLDER = "XX-DESCRIPTION-XX";
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
     * List of possible issue types
     */
    private static final String[] ISSUE_TYPES = {"VULNERABILITY", "BUG", "CODE_SMELL"};
    /**
     * List of possible issue severities
     */
    private static final String[] ISSUE_SEVERITIES = {"BLOCKER", "CRITICAL", "MAJOR", "MINOR", "INFO"};
    /**
     * Field in json response for number of code lines per language
     */
    private static final String NCLOC_PER_LANGUAGE = "ncloc_language_distribution";
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
     * Prepare list of resources to be print in a table
     * Data are lines containing the number of issues by severity and type
     * @param report report from which to extract resources
     * @return list of lists of strings
     */
    public static List<List<String>> getTypes(Report report) {
        // result to return
        List<List<String>> results = new ArrayList<>();

        String[] types = ISSUE_TYPES;
        String[] severities = ISSUE_SEVERITIES;

        for(String type : types) {
            for (String severity : severities) {
                // accumulator for the number of occurrences
                long nb = 0;
                // we sum all issues with a type and a severity
                for(Issue issue : report.getIssues()) {
                    if(issue.getType().equals(type) && issue.getSeverity().equals(severity)) {
                        nb++;
                    }
                }
                // we add it to the list
                List<String> item = new ArrayList<>();
                item.add(type);
                item.add(severity);
                item.add(String.valueOf(nb));
                // add the whole line to the results
                results.add(item);
            }
        }

        return results;
    }

    /**
     * Get formatted metrics to be printed
     * @param report Report from which to extract resources
     * @return list of list of string (metric,measure)
     */
    public static List<List<String>> getMetrics(Report report) {
        // result to return
        List<List<String>> metrics = new ArrayList<>();

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
        List<List<String>> issues = new ArrayList<>();  // result to return

        // Get the issues' id
        Map<String, Long> items = report.getIssuesFacets();

        for (Map.Entry<String, Long> v : items.entrySet()) { // construct each issues
            List<String> issue = new ArrayList<>();
            Rule rule = report.getRule(v.getKey());
            if(rule!=null) { // if the rule is found, fill information
                // add name
                issue.add(rule.getName());
                // add description
                issue.add(rule.getHtmlDesc().replaceAll(DELETE_HTML_TAGS_REGEX, StringManager.EMPTY));
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
     * Return values of a given facet
     * @param facets list of facets from which to extract values
     * @param facetName name of th facet to get
     * @return a list (can be empty)
     */
    public static List<Value> getFacetValues(List<Facet> facets, String facetName) {

        // iterate on facets' list
        Iterator<Facet> iterator = facets.iterator();
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

        return items;
    }

    /**
     * Load in a map all the placeholder (key) with the corresponding replacement value (value)
     * @param report Report from which resources are extracted
     * @return the placeholders map
     */
    public static Map<String, String> loadPlaceholdersMap(Report report) {
        // final map to return
        Map<String, String> replacementValues = new HashMap<>();
        // Replacement of placeholder
        // report meta resources placeholders
        replacementValues.put(AUTHOR_PLACEHOLDER, report.getProjectAuthor());
        replacementValues.put(VERSION_PLACEHOLDER, report.getProjectVersion());
        replacementValues.put(DESCRIPTION_PLACEHOLDER, report.getProjectDescription());
        replacementValues.put(DATE_PLACEHOLDER, report.getProjectDate());
        replacementValues.put(PROJECTNAME_PLACEHOLDER, report.getProjectName());
        // configuration placeholders
        replacementValues.put(QUALITYGATENAME_PLACEHOLDER, report.getQualityGate().getName());
        replacementValues.put(QUALITYGATEFILE_PLACEHOLDER, report.getQualityGate().getName() + XML_EXTENSION);
        replacementValues.put(QUALITYPROFILENAME_PLACEHOLDER, report.getQualityProfilesName());
        replacementValues.put(QUALITYPROFILEFILE_PLACEHOLDER, report.getQualityProfilesFilename());
        // Synthesis placeholders
        for (Measure m : report.getMeasures()) {
            String placeholder = getPlaceHolderName(m.getMetric());
            String value = m.getValue();

            // convert numerical mark to letter if necessary
            if(m.getMetric().contains(RATING)) {
                value = numberToLetter(m.getValue());
            }

            replacementValues.put(placeholder, value);
        }
        return replacementValues;
    }

    /**
     * Convert the numeric note to a letter
     * @param value numeric note
     * @return a letter
     */
    private static String numberToLetter(String value) {
        String res;
        // make the link between numbers and letters
        switch (value) {
            case MARK_1_NUMBER:
                res = MARK_1_LETTER;
                break;
            case MARK_2_NUMBER:
                res = MARK_2_LETTER;
                break;
            case MARK_3_NUMBER:
                res = MARK_3_LETTER;
                break;
            case MARK_4_NUMBER:
                res = MARK_4_LETTER;
                break;
            case MARK_5_NUMBER:
                res = MARK_5_LETTER;
                break;
            default:
                res = VALUE;
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
        String res;
        switch (metric) {
            case RELIABILITY_RATING:
                res = RELIABILITY_PLACEHOLDER;
                break;
            case DUPLICATED_LINES_DENSITY:
                res = DUPLICATION_PLACEHOLDER;
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
        List<List<String>> volumes = new ArrayList<>();

        // find metrics per language and for all
        String perLanguage = findMeasure(report.getMeasures(), NCLOC_PER_LANGUAGE);
        String total = findMeasure(report.getMeasures(), NCLOC);

        // split raw string data into list of list of string
        // to get a relevant table
        List<String> firstSplit = Arrays.asList(perLanguage.split(SEMICOLON));
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
     * Return the value of a given metrics
     * @param measures List of measures to browse
     * @param metric metric to search
     * @return a String containing the measure
     */
    private static String findMeasure(List<Measure> measures, String metric) {
        // result that can be null
        String result = EMPTY;
        // retrieve all measures to browse
        Iterator<Measure> iterator = measures.iterator();
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
}
