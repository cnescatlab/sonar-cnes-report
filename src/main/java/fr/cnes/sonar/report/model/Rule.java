package fr.cnes.sonar.report.model;

/**
 * Model of a quality code rule
 * @author lequal
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
     * The key of the language corresponding to the rule
     */
    private String lang;
    /**
     * The status of the rule
     */
    private String status;
    /**
     * The display name of the language corresponding to the rule
     */
    private String langName;

    /**
     * Default constructor
     */
    public Rule() {
        this.key = "";
        this.repo = "";
        this.name = "";
        this.severity = "";
        this.htmlDesc = "";
        this.lang = "";
        this.status = "";
        this.langName = "";
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

    /**
     * Represents the sonarqube's programming language of this rule (key)
     * @return key of the language relative to the rule
     */
    public String getLang() {
        return lang;
    }

    /**
     * Setter for the language name (key)
     * @param lang language to set
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * Represents the sonarqube's status of the rule
     * @return the status as a String
     */
    public String getStatus() {
        return status;
    }


    /**
     * Setter for the status of the rule
     * @param status status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Represents the sonarqube's programming language of this rule (display name)
     * @return a string containing th display name of the corresponding language
     */
    public String getLangName() {
        return langName;
    }

    /**
     * Setter for the language name
     * @param langName language to set
     */
    public void setLangName(String langName) {
        this.langName = langName;
    }
}
