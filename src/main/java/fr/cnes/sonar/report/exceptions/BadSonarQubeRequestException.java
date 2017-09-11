package fr.cnes.sonar.report.exceptions;

/**
 * Thrown when a request is not recognize by SonarQube
 * @author lequal
 */
public class BadSonarQubeRequestException extends Exception {

    /**
     * Constructor
     * @param message the text to print (exception's details)
     */
    public BadSonarQubeRequestException(String message) {
        super(message);
    }
}
