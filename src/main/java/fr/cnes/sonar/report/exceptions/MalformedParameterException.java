package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter is not correct
 * @author lequal
 */
public class MalformedParameterException extends Exception {

    /**
     * Constructor
     * @param key Key of the malformed parameter
     */
    public MalformedParameterException(String key) {
        super("Parameter " + key + " is incorrect.");
    }

}
