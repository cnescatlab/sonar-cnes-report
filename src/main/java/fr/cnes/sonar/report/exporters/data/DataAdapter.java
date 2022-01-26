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

import static fr.cnes.sonar.report.utils.MeasureConverter.getIntMeasureFromString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.model.Report;

/**
 * Format resources in different structures to have an easier use in report
 * Exporters
 */
public final class DataAdapter {

    /**
     * Field in json response for number of code lines
     */
    private static final String NCLOC = "ncloc";
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
     * Technical debt display format
     */
    private static final String TECHNICAL_DEBT_FORMAT = "%sd %sh %smin";

    /**
     * Private constructor to forbid instantiation of this class
     */
    private DataAdapter() {
    }

    /**
     * Get formatted metrics to be printed
     * 
     * @param report Report from which to extract resources
     * @return list of list of string (metric,measure)
     */
    public static List<List<String>> getMetrics(Report report) {
        // result to return
        final List<List<String>> metrics = new ArrayList<>();

        // construct each metric
        for (Measure m : report.getMeasures()) {
            metrics.add(Arrays.asList(m.getMetric(), m.getValue()));
        }

        return metrics;
    }

    /**
     * Get formatted quality gate status summary
     * 
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
     * Load in a list all the volume metrics with the corresponding value
     * 
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
        for (List<String> l : volumes) {
            language = report.getProject().getLanguage(l.get(0));
            if (null != language) {
                l.set(0, language.getName());
            }
        }

        // add the total lines
        volumes.add(Arrays.asList(TOTAL, total));

        return volumes;
    }

    /**
     * Get formatted technical debt summary
     * 
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
            reliabilityDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, reliabilityDebt / 8 / 60,
                    reliabilityDebt / 60 % 8, reliabilityDebt % 60);
        } else {
            reliabilityDebtFormatted = "-";
        }

        if (securityDebt != 0) {
            securityDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, securityDebt / 8 / 60, securityDebt / 60 % 8,
                    securityDebt % 60);
        } else {
            securityDebtFormatted = "-";
        }

        if (maintainabilityDebt != 0) {
            maintainabilityDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, maintainabilityDebt / 8 / 60,
                    maintainabilityDebt / 60 % 8, maintainabilityDebt % 60);
        } else {
            maintainabilityDebtFormatted = "-";
        }

        if (totalTechnicalDebt != 0) {
            totalTechnicalDebtFormatted = String.format(TECHNICAL_DEBT_FORMAT, totalTechnicalDebt / 8 / 60,
                    totalTechnicalDebt / 60 % 8, totalTechnicalDebt % 60);
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
     * Return the value of a given metric
     * 
     * @param measures List of measures to browse
     * @param metric   metric to search
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
            if (current.getMetric().equals(metric)) {
                result = current.getValue();
            }
        }
        return result;
    }
}
