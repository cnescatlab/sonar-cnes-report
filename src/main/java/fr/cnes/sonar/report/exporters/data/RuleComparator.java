package fr.cnes.sonar.report.exporters.data;

import java.util.Comparator;

import fr.cnes.sonar.report.model.Report;

/**
 * RuleComparator is used to compare 2 issues to sort them by type & severity
 */
public class RuleComparator implements Comparator<String> {

    Report report;

    RuleComparator(Report report) {
        this.report = report;
    }

    public int compare(String o1, String o2) {
        int compare = 0;

        if (o1.isEmpty() || o2.isEmpty()) {
            compare = 1;
        }

        // If rule is removed in quality gate, the issue is send to the end of list
        if (report.getRule(o1) == null) {
            compare = 1;
        } else if (report.getRule(o2) == null) {
            compare = -1;
        }

        if (compare == 0) {
            compare = report
                    .getRule(o1)
                    .getType()
                    .compareTo(
                            report.getRule(o2).getType());
        }

        if (compare == 0) {
            compare = report.getRule(o1).getSeverity().compareTo(
                    report.getRule(o2).getSeverity());
        }

        if (compare == 0) {
            compare = report.getRule(o1).getKey().compareTo(
                    report.getRule(o2).getKey());
        }

        return compare;
    }
}
