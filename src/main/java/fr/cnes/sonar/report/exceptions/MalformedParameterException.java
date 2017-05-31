package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter is not correct
 * @author begarco
 */
public class MalformedParameterException extends ParameterException {

    /**
     * Constructor
     * @param key Key of the malformed parameter
     */
    public MalformedParameterException(String key) {
        super(key);
        this.setMessageStarting("Le paramètre ");
        this.setMessageEnding(" n'est pas correct.");
    }

}
