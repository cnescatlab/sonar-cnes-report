package fr.cnes.sonar.report.exceptions;

/**
 * A given parameter does not exist
 * @author begarco
 */
public class UnknownQualityProfileException extends Exception {

    /**
     * Constructor
     * @param key Name of the unknown quality profile
     */
    public UnknownQualityProfileException(String key) {
        super("Le profil qualité "+key+" n'est pas reconnu.");
    }

}
