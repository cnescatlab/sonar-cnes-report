/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.model;

/**
 * Model of a quality code rule
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
     * @param pKey value
     */
    public void setKey(final String pKey) {
        this.key = pKey;
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
     * @param pDebt value
     */
    public void setDebt(final String pDebt) {
        this.debtRemFnCoeff = pDebt;
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
     * @param pRepo value
     */
    public void setRepo(final String pRepo) {
        this.repo = pRepo;
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
     * @param pName value
     */
    public void setName(final String pName) {
        this.name = pName;
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
     * @param pSeverity value
     */
    public void setSeverity(final String pSeverity) {
        this.severity = pSeverity;
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
     * @param pType value
     */
    public void setType(final String pType) {
        this.type = pType;
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
     * @param pHtmlDesc value
     */
    public void setHtmlDesc(final String pHtmlDesc) {
        this.htmlDesc = pHtmlDesc;
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
     * @param pLang language to set
     */
    public void setLang(final String pLang) {
        this.lang = pLang;
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
     * @param pStatus status to set
     */
    public void setStatus(final String pStatus) {
        this.status = pStatus;
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
     * @param pLangName language to set
     */
    public void setLangName(final String pLangName) {
        this.langName = pLangName;
    }
}
