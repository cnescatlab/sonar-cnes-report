package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter is not correct
 * @author begarco
 */
public class MalformedParameterException extends Exception {

    /**
     * Constructor
     * @param key Key of the malformed parameter
     */
    public MalformedParameterException(String key) {
        super("Le paramètre " + key + " n'est pas correct.");
    }

}
