package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter does not exist
 * @author begarco
 */
public class UnknownParameterException extends ParameterException {

    public UnknownParameterException(String key) {
        super(key);
        this.setMessageStarting("Le paramètre ");
        this.setMessageEnding(" n'est pas reconnu.");
    }

}
