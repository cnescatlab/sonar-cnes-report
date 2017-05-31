package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter does not exist
 * @author begarco
 */
public class UnknownQualityProfileException extends ParameterException {

    /**
     * Constructor
     * @param key Name of the unknown quality profile
     */
    public UnknownQualityProfileException(String key) {
        super(key);
        this.setMessageStarting("Le profil qualité ");
        this.setMessageEnding(" n'est pas reconnu.");
    }

}
