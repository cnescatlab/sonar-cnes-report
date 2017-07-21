package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter does not exist
 * @author begarco
 */
public class UnknownQualityGateException extends Exception {

    /**
     * Constructor
     * @param key name of the unknown quality gate
     */
    public UnknownQualityGateException(String key) {
        super("Quality gate "+key+" is unknown.");
    }

}
