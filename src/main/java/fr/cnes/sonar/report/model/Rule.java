package fr.cnes.sonar.report.model;

/**
 * Model of a quality code rule
 * @author begarco
 */
public class Rule {
    /**
     * Represents the sonarqube's key of the rule
     */
    private String key;
    /**
     * Represents the sonarqube's repository in which the rule is
     */
    private String repo;
    /**
     * Represents the sonarqube's name of the rule
     */
    private String name;
    /**
     * Represents the sonarqube's severity of the rule
     */
    private String severity;
    /**
     * Represents the sonarqube's type of the rule
     */
    private String type;
    /**
     * Represents the sonarqube's HTML description of the rule
     */
    private String htmlDesc;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHtmlDesc() {
        return htmlDesc;
    }

    public void setHtmlDesc(String htmlDesc) {
        this.htmlDesc = htmlDesc;
    }
}
