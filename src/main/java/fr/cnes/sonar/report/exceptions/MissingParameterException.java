package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter is not reliable
 * @author begarco
 */
public class MissingParameterException extends Exception {

    /**
     * Constructor
     * @param key Key of the missing parameter
     */
    public MissingParameterException(String key) {
        super("Parameter " + key + " is missing.");
    }

}
