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

import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a violation of a rule
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
     * Issue's status resolution
     */
    private String resolution;
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
        this.resolution = "";
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
        return key + StringManager.TAB + project + StringManager.TAB +
                component + StringManager.TAB + type + StringManager.TAB +
                severity + StringManager.TAB + message + StringManager.TAB + line +
                StringManager.TAB + status + StringManager.TAB + StringManager.TAB;
    }

    /**
     * Get a list of String containing details of each issue
     * @return list of strings
     */
    public List<String> getAll() {
        final List<String> list = new ArrayList<>();
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
     * @param pSeverity value
     */
    public void setSeverity(String pSeverity) {
        this.severity = pSeverity;
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
    public void setKey(String pKey) {
        this.key = pKey;
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
     * @param pComponent value
     */
    public void setComponent(String pComponent) {
        this.component = pComponent;
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
     * @param pProject value
     */
    public void setProject(String pProject) {
        this.project = pProject;
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
     * @param pLine value
     */
    public void setLine(String pLine) {
        this.line = pLine;
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
     * @param pStatus value
     */
    public void setStatus(String pStatus) {
        this.status = pStatus;
    }

    /**
     * Getter for resolution
     * @return resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * Setter for resolution
     * @param pResolution value
     */
    public void setResolution(String pResolution) {
        this.resolution = pResolution;
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
    public void setType(String pType) {
        this.type = pType;
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
     * @param pMessage value
     */
    public void setMessage(String pMessage) {
        this.message = pMessage;
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
     * @param pRule value
     */
    public void setRule(String pRule) {
        this.rule = pRule;
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
     * @param pEffort debt to set
     */
    public void setEffort(String pEffort) {
        this.effort = pEffort;
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
     * @param pLanguage value
     */
    public void setLanguage(String pLanguage) {
        this.language = pLanguage;
    }
}
