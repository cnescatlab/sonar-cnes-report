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
 * Represents a security breach
 */
public class Hotspot{
    
    //Key in SonarQube
    private String key;
    //Name of the affected file
    private String component;
    //Name of the affected project
    private String project;
    //Security category
    private String securityCategory;
    //Review priority
    private String vulnerabilityProbability;
    //Hotspot's status (the resolution or not)
    private String status;
    //Line of the hotspot
    private String line;
    //Hotspot's message
    private String message;
    //Name of the hotspot
    private String name;
    //Risk description
    private String riskDescription;
    //Vulnerability description
    private String vulnerabilityDescription;
    //Fix recommendations
    private String fixRecommendations;
    //Hotspot's comments
    private Comment[] comments;

    /**
     * Default constructor
     */
    public Hotspot() {
        this.key = "";
        this.component = "";
        this.project = "";
        this.securityCategory = "";
        this.vulnerabilityProbability = "";
        this.status = "";
        this.line = "";
        this.message = "";
        this.name = "";
        this.riskDescription = "";
        this.vulnerabilityDescription = "";
        this.fixRecommendations = "";
        this.comments = new Comment[0];
    }

    /**
     * Get a list of String containing details of each hotspot
     * @return list of Strings
     */
    public List<String> getAll(){
        final List<String> list = new ArrayList<>();
        list.add(this.getKey());
        list.add(this.getComponent());
        list.add(this.getProject());
        list.add(this.getSecurityCategory());
        list.add(this.getVulnerabilityProbability());
        list.add(this.getStatus());
        list.add(this.getLine());
        list.add(this.getMessage());
        list.add(this.getName());
        list.add(this.getRiskDescription());
        list.add(this.getVulnerabilityDescription());
        list.add(this.getFixRecommendations());
        return list;
    }

    
    /** 
     * @return String
     */
    public String getKey() {
        return this.key;
    }

    
    /** 
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    
    /** 
     * @return String
     */
    public String getComponent() {
        return this.component;
    }

    
    /** 
     * @param component
     */
    public void setComponent(String component) {
        this.component = component;
    }

    
    /** 
     * @return String
     */
    public String getProject() {
        return this.project;
    }

    
    /** 
     * @param project
     */
    public void setProject(String project) {
        this.project = project;
    }

    
    /** 
     * @return String
     */
    public String getSecurityCategory() {
        return this.securityCategory;
    }

    
    /** 
     * @param securityCategory
     */
    public void setSecurityCategory(String securityCategory) {
        this.securityCategory = securityCategory;
    }

    
    /** 
     * @return String
     */
    public String getVulnerabilityProbability() {
        return this.vulnerabilityProbability;
    }

    
    /** 
     * @param vulnerabilityProbability
     */
    public void setVulnerabilityProbability(String vulnerabilityProbability) {
        this.vulnerabilityProbability = vulnerabilityProbability;
    }

    
    /** 
     * @return String
     */
    public String getStatus() {
        return this.status;
    }

    
    /** 
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    
    /** 
     * @return String
     */
    public String getLine() {
        return this.line;
    }

    
    /** 
     * @param line
     */
    public void setLine(String line) {
        this.line = line;
    }

    
    /** 
     * @return String
     */
    public String getMessage() {
        return this.message;
    }

    
    /** 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    
    /** 
     * @return String
     */
    public String getName() {
        return this.name;
    }

    
    /** 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /** 
     * @return String
     */
    public String getRiskDescription() {
        return this.riskDescription;
    }

    
    /** 
     * @param riskDescription
     */
    public void setRiskDescription(String riskDescription) {
        this.riskDescription = riskDescription;
    }

    
    /** 
     * @return String
     */
    public String getVulnerabilityDescription() {
        return this.vulnerabilityDescription;
    }

    
    /** 
     * @param vulnerabilityDescription
     */
    public void setVulnerabilityDescription(String vulnerabilityDescription) {
        this.vulnerabilityDescription = vulnerabilityDescription;
    }

    
    /** 
     * @return String
     */
    public String getFixRecommendations() {
        return this.fixRecommendations;
    }

    
    /** 
     * @param fixRecommendations
     */
    public void setFixRecommendations(String fixRecommendations) {
        this.fixRecommendations = fixRecommendations;
    }

    
    /** 
     * Get comments as a String with one comment per line
     * @return A simple String
     */
    public String getComments() {
        final StringBuilder coms = new StringBuilder();

        for(final Comment comment : this.comments){
            coms.append("[").append(comment.getLogin()).append("] ").append(comment.getMarkdown()).append("\n");
        }
        return coms.toString();
    }

    /**
     * Overridden toString
     * @return all resources separated with tabulations
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.key).append(StringManager.TAB);
        stringBuilder.append(this.component).append(StringManager.TAB);
        stringBuilder.append(this.project).append(StringManager.TAB);
        stringBuilder.append(this.securityCategory).append(StringManager.TAB);
        stringBuilder.append(this.vulnerabilityProbability).append(StringManager.TAB);
        stringBuilder.append(this.status).append(StringManager.TAB);
        stringBuilder.append(this.line).append(StringManager.TAB);
        stringBuilder.append(this.message).append(StringManager.TAB);
        stringBuilder.append(this.name).append(StringManager.TAB);
        stringBuilder.append(this.riskDescription).append(StringManager.TAB);
        stringBuilder.append(this.vulnerabilityDescription).append(StringManager.TAB);
        stringBuilder.append(this.fixRecommendations).append(StringManager.TAB);
        stringBuilder.append(getComments()).append(StringManager.TAB);
        return stringBuilder.toString();
    }
}