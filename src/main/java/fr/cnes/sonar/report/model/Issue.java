package fr.cnes.sonar.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a violation of a rule
 * @author begarco
 */
public class Issue {

    /**
     * Severity of the corresponding rule
     */
    private String severity;
    /**
     * Key in SonarQube
     */
    private String key;
    /**
     * Name of the corresponding rule
     */
    private String rule;
    /**
     * Name of the affected file
     */
    private String component;
    /**
     * Name of the affected project
     */
    private String project;
    /**
     * Line of the issue
     */
    private String line;
    /**
     * Issue's status
     */
    private String status;
    /**
     * Issue's effort
     */
    private String effort;
    /**
     * Issue's type
     */
    private String type;
    /**
     * Issue's message
     */
    private String message;
    /**
     * Issue's programming language
     */
    private String language;

    /**
     * Default constructor
     */
    public Issue() {
        this.key = "";
        this.line = "";
        this.message = "";
        this.component = "";
        this.rule = "";
        this.severity = "";
        this.project = "";
        this.status = "";
        this.type = "";
        this.effort = "0";
        this.language = "";
    }

    /**
     * Overridden toString
     * @return all resources separated with tabulation
     */
    @Override
    public String toString() {
        return key + "\t" + project + "\t" + component + "\t" + type + "\t" + severity + "\t" + message + "\t" + line + "\t" + status + "\t" + "\t";
    }

    /**
     * Get a list of String containing details of each issue
     * @return list of strings
     */
    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        list.add(getKey());
        list.add(getProject());
        list.add(getComponent());
        list.add(getType());
        list.add(getSeverity());
        list.add(getMessage());
        list.add(getLine());
        list.add(getStatus());

        return list;
    }

    /**
     * Getter for severity
     * @return severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Setter for severity
     * @param severity value
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Getter for key
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter for key
     * @param key value
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Getter for component (often a file)
     * @return component
     */
    public String getComponent() {
        return component;
    }

    /**
     * Setter for component
     * @param component value
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * Getter for project
     * @return project
     */
    public String getProject() {
        return project;
    }

    /**
     * Setter for project
     * @param project value
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * Getter for line
     * @return line
     */
    public String getLine() {
        return line;
    }

    /**
     * Setter for line
     * @param line value
     */
    public void setLine(String line) {
        this.line = line;
    }

    /**
     * Getter for status
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter for status
     * @param status value
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getter for type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for type
     * @param type value
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for message
     * @param message value
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for rule
     * @return rule
     */
    public String getRule() {
        return rule;
    }

    /**
     * Setter for rule
     * @param rule value
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * Issue's effort
     * @return Effort as String and postfixed with 'min'
     */
    public String getEffort() {
        return effort;
    }

    /**
     * Setter for the effort or debt
     * @param effort debt to set
     */
    public void setEffort(String effort) {
        this.effort = effort;
    }

    /**
     * Getter for language
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Setter for language
     * @param language value
     */
    public void setLanguage(String language) {
        this.language = language;
    }
}
