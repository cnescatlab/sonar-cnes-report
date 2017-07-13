package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Format data in different structure to have an easier use
 * @author begarco
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
     * Prepare list of data to be print in a table
     * Data are lines containing the number of issues by severity and type
     * @param report report from which to extract data
     * @return list of lists of strings
     */
    public static List<List<String>> getTypes(Report report) {
        // result to return
        List<List<String>> results = new ArrayList<>();

        String[] types = {"VULNERABILITY", "BUG", "CODE_SMELL"};
        String[] severities = {"BLOCKER", "CRITICAL", "MAJOR", "MINOR", "INFO"};

        for(String type : types) {
            for (String severity : severities) {
                // accumulator for the number of occurrences
                long nb = 0;
                // we sum all issues with a type and a severity
                for(Issue issue : report.getIssues()) {
                    nb += (issue.getType().equals(type) && issue.getSeverity().equals(severity)) ? 1 :0;
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
     * @param report Report from which to extract data
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
     * @param report report from which to export data
     * @return issues list
     */
    public static List<List<String>> getIssues(Report report) {
        List<List<String>> issues = new ArrayList<>();  // result to return

        // Get the issues' id
        List<Value> items = getFacetValues(report.getFacets(), StringManager.RULES);

        for (Value v : items) { // construct each issues
            List<String> issue = new ArrayList<>();
            Rule rule = report.getRule(v.getVal());
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
                issue.add(Integer.toString(v.getCount()));
            } else { // else set just known information
                // add name
                issue.add(v.getVal());
                // add description
                issue.add(QUESTION_MARK);
                // add type
                issue.add(QUESTION_MARK);
                // add severity
                issue.add(QUESTION_MARK);
                // add number
                issue.add(Integer.toString(v.getCount()));
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
        Iterator iterator = facets.iterator();
        // list of results
        List<Value> items = null;
        while(iterator.hasNext() && items==null) {
            // get current facet
            Facet facet = (Facet) iterator.next();
            // check if current facet is the wanted one
            if(facet.getProperty().equals(facetName)) {
                items = facet.getValues();
            }
        }

        return items;
    }
}
