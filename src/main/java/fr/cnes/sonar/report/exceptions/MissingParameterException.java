package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter is not reliable
 * @author begarco
 */
public class MissingParameterException extends ParameterException {

    /**
     * Constructor
     * @param key Key of the missing parameter
     */
    public MissingParameterException(String key) {
        super(key);
        this.setMessageStarting("Le paramètre ");
        this.setMessageEnding(" n'est pas renseigné.");
    }

}
