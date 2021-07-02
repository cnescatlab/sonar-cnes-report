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

package fr.cnes.sonar.report.providers.qualitygate;

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import org.sonarqube.ws.client.WsClient;
import org.apache.commons.math3.util.Precision;

/**
 * Contains common code for quality gate providers
 */
public abstract class AbstractQualityGateProvider extends AbstractDataProvider {

    /**
     * Field to search in json to get the boolean saying if a profile is the default one
     */
    protected static final String DEFAULT = "default";
    /**
     * Field to search in json to get quality gates
     */
    protected static final String QUALITYGATES = "qualitygates";
    /** 
     * Field to find in json to get quality gate
     */
    protected static final String QUALITY_GATE = "qualityGate";
    /**
     * Parameter "projectStatus" of the JSON response
     */
    protected static final String PROJECT_STATUS = "projectStatus";
    /**
     * Parameter "conditions" of the JSON response
     */
    protected static final String CONDITIONS = "conditions";
    /**
     * Parameter "status" of the JSON response
     */
    protected static final String STATUS = "status";
    /**
     * Parameter "metricKey" of the JSON response
     */
    protected static final String METRIC_KEY = "metricKey";
    /**
     * Parameter "metrics" of the JSON response
     */
    protected static final String METRICS = "metrics";
    /**
     * Parameter "name" of the JSON response
     */
    protected static final String NAME = "name";
    /**
     * Value of the parameter "status" of the JSON response
     */
    protected static final String ERROR = "ERROR";
    /**
     * Parameter "actualValue" of the JSON response
     */
    protected static final String ACTUAL_VALUE = "actualValue";
    /**
     * Parameter "errorThreshold" of the JSON response
     */
    protected static final String ERROR_THRESHOLD = "errorThreshold";
    /**
     * Parameter "comparator" of the JSON response
     */
    protected static final String COMPARATOR = "comparator";
    /**
     * Parameter "type" of the JSON response
     */
    protected static final String TYPE = "type";
    /**
     * Value of the parameter "type" of the JSON response
     */
    private static final String RATING = "RATING";
    /**
     * Value of the parameter "type" of the JSON response
     */
    private static final String WORK_DUR = "WORK_DUR";
    /**
     * Value of the parameter "type" of the JSON response
     */
    private static final String PERCENT = "PERCENT";
    /**
     * Value of the parameter "type" of the JSON response
     */
    private static final String MILLISEC = "MILLISEC";

    /**
     * Complete constructor.
     * @param pServer SonarQube server.
     * @param pToken String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch The branch of the project to report.
     */
    protected AbstractQualityGateProvider(final String pServer, final String pToken, final String pProject,
            final String pBranch) {
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Complete constructor.
     * @param wsClient The web client.
     * @param project The id of the project to report.
     * @param branch The branch of the project to report.
     */
    protected AbstractQualityGateProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Construct the sentence explaining why a condition failed
     * @param actualValue the actual value in the JSON response
     * @param errorThreshold the error threshold in the JSON response
     * @param comparator the comparator in the JSON response
     * @param type the type in the JSON response
     * @return a String containing the error explanation
     */
    protected String getErrorExplanation(String actualValue, String errorThreshold, String comparator, String type) {
        // format of the string to add in the status
        String format = " (%s is %s than %s)";
        
        String actual;
        String compare;
        String threshold;

        // compute the values of the strings to put in the explanation according to their types
        switch (type) {
            case RATING:
                actual = ratingToLetter(actualValue);
                compare = "worse";
                threshold = ratingToLetter(errorThreshold);
                break;
            case WORK_DUR:
                actual = workDurationToTime(actualValue);
                compare = comparatorToString(comparator);
                threshold = workDurationToTime(errorThreshold);
                break;
            case PERCENT:
                actual = String.valueOf(Precision.round(Double.valueOf(actualValue), 1)).concat("%");
                compare = comparatorToString(comparator);
                threshold = errorThreshold.concat("%");
                break;
            case MILLISEC:
                actual = actualValue.concat("ms");
                compare = comparatorToString(comparator);
                threshold = errorThreshold.concat("ms");
                break;
            default:
                actual = actualValue;
                compare = comparatorToString(comparator);
                threshold = errorThreshold;
                break;
        }
        
        return String.format(format, actual, compare, threshold);
    }

    /**
     * Convert a rating number to a String
     * @param rating the rating number
     * @return the letter corresponding to the rating number
     */
    private String ratingToLetter(String rating) {
        String res;
        switch (rating) {
            case "1":
                res = "A";
                break;
            case "2":
                res = "B";
                break;
            case "3":
                res = "C";
                break;
            case "4":
                res = "D";
                break;
            case "5":
                res = "E";
                break;
            default:
                res = rating;
                break;
        }
        return res;
    }

    /**
     * Convert a work duration number to days/hours/minutes format
     * @param workDuration the work duration number
     * @return the time corresponding to the work duration number
     */
    private String workDurationToTime(String workDuration) {
        String format = "%sd %sh %smin";
        int workDurationInt = Integer.parseInt(workDuration);
        return String.format(format, workDurationInt/8/60, workDurationInt/60%8, workDurationInt%60);
    }

    /**
     * Convert a comparator in a JSON response to an understandable string
     * @param comparator the comparator in the JSON response
     * @return the understandable string corresponding to the comparator
     */
    private String comparatorToString(String comparator) {
        String res;
        if (comparator.equals("GT")) {
            res = "greater";
        } else {
            res = "less";
        }
        return res;
    }
}