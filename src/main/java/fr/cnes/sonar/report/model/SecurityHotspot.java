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

public class SecurityHotspot {

    /**
     * Key in SonarQube
     */
    private String key;
    /**
     * Name of the affected file
     */
    private String component;
    /**
     * Line of the security hotspot
     */
    private String line;
    /**
     * Security hotspot's security category
     */
    private String securityCategory;
    /**
     * Security hotspot's vulnerability probability
     */
    private String vulnerabilityProbability;
    /**
     * Security hotspot's status
     */
    private String status;
    /**
     * Security hotspot's message
     */
    private String message;
    /**
     * Security hotspot's status resolution
     */
    private String resolution;
    /**
     * Key of the corresponding rule
     */
    private String rule;
    /**
     * Security hotspot's comments
     */
    private Comment[] comments;
    /**
     * Severity of the corresponding rule
     */
    private String severity;
    /**
     * Security hotspot's programming language
     */
    private String language;

    /**
     * Default constructor
     */
    public SecurityHotspot() {
        this.key = "";
        this.component = "";
        this.line = "";
        this.securityCategory = "";
        this.vulnerabilityProbability = "";
        this.status = "";
        this.message = "";
        this.resolution = "";
        this.rule = "";
        this.comments = new Comment[0];
        this.severity = "";
        this.language = "";
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
     * Getter for securityCategory
     * @return securityCategory
     */
    public String getSecurityCategory() {
        return securityCategory;
    }

    /**
     * Setter for securityCategory
     * @param securityCategory value
     */
    public void setSecurityCategory(String securityCategory) {
        this.securityCategory = securityCategory;
    }

    /**
     * Getter for vulnerabilityProbability
     * @return vulnerabilityProbability
     */
    public String getVulnerabilityProbability() {
        return vulnerabilityProbability;
    }

    /**
     * Setter for vulnerabilityProbability
     * @param vulnerabilityProbability value
     */
    public void setVulnerabilityProbability(String vulnerabilityProbability) {
        this.vulnerabilityProbability = vulnerabilityProbability;
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
     * Getter for resolution
     * @return resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * Setter for resolution
     * @param resolution value
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
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
     * Get comments as a String with one comment per line.
     * @return A simple String.
     */
    public String getComments() {
        final StringBuilder coms = new StringBuilder();

        for(final Comment comment : this.comments) {
            coms.append("[").append(comment.getLogin()).append("] ").append(comment.getMarkdown()).append("\n");
        }

        return coms.toString();
    }

    /**
     * Setter for comments
     * @param comments value
     */
    public void setComments(Comment[] comments) {
        this.comments = comments;
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