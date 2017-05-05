package fr.cnes.sonar.report.exceptions;

/**
 * Parent class for exception about parameters
 * @author begarco
 */
public class ParameterException extends Exception {

    private String start;
    private String key;
    private String end;

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
