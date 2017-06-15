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
    /**
     * Represents the sonarqube's technical debt relative to this rule
     */
    private String debtRemFnCoeff;

    /**
     * Default constructor
     */
    public Rule() {
        this.key = "";
        this.repo = "";
        this.name = "";
        this.severity = "";
        this.htmlDesc = "";
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
     * Getter for debt
     * @return debt
     */
    public String getDebt() {
        return debtRemFnCoeff;
    }

    /**
     * Setter for debt
     * @param debt value
     */
    public void setDebt(String debt) {
        this.debtRemFnCoeff = debt;
    }

    /**
     * Getter for repo
     * @return repo
     */
    public String getRepo() {
        return repo;
    }

    /**
     * Setter for repo
     * @param repo value
     */
    public void setRepo(String repo) {
        this.repo = repo;
    }

    /**
     * Getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name value
     */
    public void setName(String name) {
        this.name = name;
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
     * Getter for htmlDesc
     * @return htmlDesc
     */
    public String getHtmlDesc() {
        return htmlDesc;
    }

    /**
     * Setter for htmlDesc
     * @param htmlDesc value
     */
    public void setHtmlDesc(String htmlDesc) {
        this.htmlDesc = htmlDesc;
    }
}
