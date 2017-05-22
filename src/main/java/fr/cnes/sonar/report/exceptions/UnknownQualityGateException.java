package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter does not exist
 * @author begarco
 */
public class UnknownQualityGateException extends ParameterException {

    public UnknownQualityGateException(String key) {
        super(key);
        this.setMessageStarting("Le quality gate ");
        this.setMessageEnding(" n'est pas reconnu.");
    }

}
