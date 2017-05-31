package fr.cnes.sonar.report.exceptions;

/**
 * Parent class for exception about parameters
 * @author begarco
 */
public class ParameterException extends Exception {

    /**
     * Start of the message
     */
    private String start;
    /**
     * Key of the message
     */
    private String key;
    /**
     * End of the message
     */
    private String end;

    /**
     * Constructor
     * @param key Key to print
     */
    public ParameterException(String key) {
        this.key = key;
    }

    protected void setMessageStarting(String start) {
        this.start = start;
    }

    protected void setMessageEnding(String end) {
        this.end = end;
    }

    @Override
    public String getMessage() {
        return start + key + end;
    }
}
