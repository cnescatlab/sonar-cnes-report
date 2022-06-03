package fr.cnes.sonar.report.exporters.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.utils.StringManager;

/**
 * Class used to format Issues data for reports
 */
public class IssuesAdapter {

    /**
     * Property to get the list of issues types
     */
    private static final String ISSUES_TYPES = "issues.types";
    /**
     * Property to get the list of issues severities
     */
    private static final String ISSUES_SEVERITIES = "issues.severities";
    /**
     * Regex to delete html tags
     */
    private static final String DELETE_HTML_TAGS_REGEX = "<[^>]*>";
    /**
     * just a question mark
     */
    private static final String QUESTION_MARK = "?";

    private IssuesAdapter() {
    }

    /**
     * Getter for reversed ISSUES_SEVERITIES
     * 
     * @return reversed ISSUES_SEVERITIES
     */
    public static List<String> getReversedIssuesSeverities() {
        List<String> issuesSeverities = new ArrayList<>(
                Arrays.asList(StringManager.getProperty(ISSUES_SEVERITIES).split(",")));
        Collections.reverse(issuesSeverities);
        return issuesSeverities;
    }

    /**
     * Prepare list of resources to be print in a table
     * Data are lines containing the number of issues by severity and type
     * 
     * @param report report from which to extract resources
     * @return list of lists of strings
     */
    public static List<List<String>> getTypes(Report report) {
        // result to return
        final List<List<String>> results = new ArrayList<>();

        final List<String> types = new ArrayList<>(Arrays.asList(StringManager.getProperty(ISSUES_TYPES).split(",")));
        final List<String> severities = getReversedIssuesSeverities();

        // accumulator for the number of occurrences for each severity
        LinkedHashMap<String, Integer> countPerSeverity = new LinkedHashMap<>();
        for (String severity : severities) {
            countPerSeverity.put(severity, 0);
        }

        for (String type : types) {
            // List of items for each line of the table
            final List<String> row = new ArrayList<>();
            for (Issue issue : report.getIssues().getIssuesList()) {
                if (issue.getType().equals(type)) {
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
     * Get formatted issues summary
     * 
     * @param report report from which to export resources
     * @return issues list
     */
    public static List<List<String>> getIssues(Report report) {
        final List<List<String>> formattedIssues = new ArrayList<>(); // result to return

        // Get the issues' id
        final Map<String, Long> items = report.getIssues().getIssuesFacets();

        Map<String, Long> sortedItems = new TreeMap<>(new RuleComparator(report));
        sortedItems.putAll(items);

        List<String> formattedIssue = null;
        Rule rule = null;
        Issue issue = null;

        for (Map.Entry<String, Long> v : sortedItems.entrySet()) { // construct each issues
            formattedIssue = new ArrayList<>();
            rule = report.getRule(v.getKey());
            issue = report.getIssues().getFirstIssueMatchingRule(v.getKey());

            if (rule != null) { // if the rule is found, fill information
                // add name
                formattedIssue.add(rule.getName());
                // add description
                formattedIssue.add(rule.getHtmlDesc()
                        .replaceAll(DELETE_HTML_TAGS_REGEX, StringManager.EMPTY));
                // add type
                formattedIssue.add(rule.getType());
                // add severity
                formattedIssue.add(rule.getSeverity());
            } else if (issue != null) { // if if comes from an external analyzer
                // add name
                formattedIssue.add(issue.getRule());
                // add description
                formattedIssue.add(issue.getMessage()
                        .replaceAll(DELETE_HTML_TAGS_REGEX, StringManager.EMPTY));
                // add type
                formattedIssue.add(issue.getType());
                // add severity
                formattedIssue.add(issue.getSeverity());
            }else { // else set just known information
                // add name
                formattedIssue.add(v.getKey());
                // add description
                formattedIssue.add(QUESTION_MARK);
                // add type
                formattedIssue.add(QUESTION_MARK);
                // add severity
                formattedIssue.add(QUESTION_MARK);
            }

            // add number
            formattedIssue.add(Long.toString(v.getValue()));

            formattedIssues.add(formattedIssue);
        }

        return formattedIssues;
    }

}
