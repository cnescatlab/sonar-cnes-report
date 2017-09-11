package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter does not exist
 * @author lequal
 */
public class UnknownParameterException extends Exception {

    /**
     * Constructor
     * @param key key of the unknown parameter
     */
    public UnknownParameterException(String key) {
        super("Parameter " + key + " is unknown.");
    }

}
