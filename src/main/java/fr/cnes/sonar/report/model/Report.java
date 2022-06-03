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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Precision;

import fr.cnes.sonar.report.utils.StringManager;

/**
 * Model of a report containing all information
 */
public class Report {
    /**
     * Name of the project/report
     */
    private String projectName;
    /**
     * Name of the author
     */
    private String projectAuthor;
    /**
     * Date to write in the project
     */
    private String projectDate;
    /**
     * List of quality profiles used in the project
     */
    private List<QualityProfile> qualityProfiles;
    /**
     * Quality gate used in the project
     */
    private QualityGate qualityGate;
    /**
     * List of issues detected in the project
     */
    private Issues issues;
    /**
     * List of facets of the project
     */
    private Facets facets;
    /**
     * List of facets of the project
     */
    private TimeFacets timeFacets;
    /**
     * List of security hotspots detected in the project
     */
    private List<SecurityHotspot> toReviewSecurityHotspots;
    /**
     * List of reviewed security hotspots issues in the project
     */
    private List<SecurityHotspot> reviewedSecurityHotspots;
    /**
     * List of measures on the project
     */
    private List<Measure> measures;
    /**
     * List of map representing issues
     */
    private List<Map<String, String>> rawIssues;
    /**
     * Data about the project
     */
    private Project project;
    /**
     * List of unconfirmed issues in the project like false positives and wont fix
     */
    private List<Issue> unconfirmed;
    /**
     * List of components in the project and their metrics
     */
    private List<Map<String, String>> components;
    /**
     * Maps of metrics stats
     */
    private Map<String, Double> metricsStats;
    /**
     * Map of quality gate conditions statuses
     */
    private Map<String, String> qualityGateStatus;

    /**
     * Default constructor
     */
    public Report() {
        this.projectName = "";
        this.projectAuthor = "";
        this.projectDate = "";
        this.qualityProfiles = new ArrayList<>();
        this.qualityGate = new QualityGate();
        this.issues = new Issues();
        this.unconfirmed = new ArrayList<>();
        this.facets = new Facets();
        this.timeFacets = new TimeFacets();
        this.toReviewSecurityHotspots = new ArrayList<>();
        this.reviewedSecurityHotspots = new ArrayList<>();
        this.measures = new ArrayList<>();
        this.rawIssues = new ArrayList<>();
        this.components = new ArrayList<>();
        this.metricsStats = new HashMap<>();
        this.qualityGateStatus = new HashMap<>();
        this.project = new Project(StringManager.EMPTY, StringManager.EMPTY,
                StringManager.EMPTY, StringManager.EMPTY, StringManager.EMPTY, StringManager.EMPTY);
    }

    /**
     * Return the compliance to the coding standard (% of rules in all Quality
     * Profiles that are not violated)
     * 
     * @param report Report from which resources are extracted
     * @return the compliance
     */
    public String getCompliance() {
        int rulesNumber = 0;
        double compliance;

        for (QualityProfile qp : this.getQualityProfiles()) {
            rulesNumber += qp.getRules().size();
        }

        if (rulesNumber != 0) {
            Set<String> violatedRules = new HashSet<>();
            for (Issue issue : this.issues.getIssuesList()) {
                violatedRules.add(issue.getRule());
            }
            for (SecurityHotspot securityHotspot : this.getToReviewSecurityHotspots()) {
                violatedRules.add(securityHotspot.getRule());
            }
            compliance = ((double) (rulesNumber - violatedRules.size()) / rulesNumber) * 100;
        } else {
            compliance = 0;
        }

        return String.valueOf(Precision.round(compliance, 1));
    }

    /**
     * Getter for metrics stats
     * 
     * @param metricsStats maps with min, max, mean all numerical metric
     */
    public void setMetricsStats(Map<String, Double> metricsStats) {
        this.metricsStats = metricsStats;
    }

    /**
     * Setter for components
     * 
     * @return maps with min, max, mean all numerical metric
     */
    public Map<String, Double> getMetricsStats() {
        return metricsStats;
    }

    /**
     * Getter for components
     * 
     * @return components
     */
    public List<Map<String, String>> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * Setteer for components
     * 
     * @param components
     */
    public void setComponents(List<Map<String, String>> components) {
        this.components = new ArrayList<>(components);
    }

    /**
     * Get issues
     * 
     * @return issues
     */
    public Issues getIssues() {
        return issues;
    }

    /**
     * Setter for issues
     * 
     * @param pIssues value
     */
    public void setIssues(List<Issue> pIssues) {
        this.issues.setIssuesList(pIssues);
    }

    /**
     * Get security hotspots with TO_REVIEW status
     * 
     * @return security hotspots
     */
    public List<SecurityHotspot> getToReviewSecurityHotspots() {
        return toReviewSecurityHotspots;
    }

    /**
     * Setter for toReviewSecurityHotspots
     * 
     * @param pToReviewSecurityHotspots value
     */
    public void setToReviewSecurityHotspots(List<SecurityHotspot> pToReviewSecurityHotspots) {
        this.toReviewSecurityHotspots = pToReviewSecurityHotspots;
    }

    /**
     * Get security hotspots with REVIEWED status
     * 
     * @return security hotspots
     */
    public List<SecurityHotspot> getReviewedSecurityHotspots() {
        return reviewedSecurityHotspots;
    }

    /**
     * Setter for reviewedSecurityHotspots
     * 
     * @param pReviewedSecurityHotspots value
     */
    public void setReviewedSecurityHotspots(List<SecurityHotspot> pReviewedSecurityHotspots) {
        this.reviewedSecurityHotspots = pReviewedSecurityHotspots;
    }

    /**
     * Getter for projectName
     * 
     * @return projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Setter for projectName
     * 
     * @param pProjectName value
     */
    public void setProjectName(String pProjectName) {
        this.projectName = pProjectName;
    }

    /**
     * Getter for projectAuthor
     * 
     * @return projectAuthor
     */
    public String getProjectAuthor() {
        return projectAuthor;
    }

    /**
     * Setter for projectAuthor
     * 
     * @param pProjectAuthor value
     */
    public void setProjectAuthor(String pProjectAuthor) {
        this.projectAuthor = pProjectAuthor;
    }

    /**
     * Getter for projectDate
     * 
     * @return projectDate
     */
    public String getProjectDate() {
        return projectDate;
    }

    /**
     * Setter for projectDate
     * 
     * @param pProjectDate value
     */
    public void setProjectDate(String pProjectDate) {
        this.projectDate = pProjectDate;
    }

    /**
     * Getter for qualityProfiles
     * 
     * @return qualityProfiles
     */
    public List<QualityProfile> getQualityProfiles() {
        return new ArrayList<>(qualityProfiles);
    }

    /**
     * Setter for qualityProfiles
     * 
     * @param pQualityProfiles value
     */
    public void setQualityProfiles(List<QualityProfile> pQualityProfiles) {
        this.qualityProfiles = new ArrayList<>(pQualityProfiles);
    }

    /**
     * Getter for qualityGate
     * 
     * @return qualityGate
     */
    public QualityGate getQualityGate() {
        return qualityGate;
    }

    /**
     * Setter for qualityGate
     * 
     * @param pQualityGate value
     */
    public void setQualityGate(QualityGate pQualityGate) {
        this.qualityGate = pQualityGate;
    }

    /**
     * Getter for measures
     * 
     * @return measures
     */
    public List<Measure> getMeasures() {
        return new ArrayList<>(measures);
    }

    /**
     * Setter for measures
     * 
     * @param pMeasures value
     */
    public void setMeasures(List<Measure> pMeasures) {
        this.measures = new ArrayList<>(pMeasures);
    }

    /**
     * Getter for facets
     * 
     * @return facets
     */
    public Facets getFacets() {
        return facets;
    }

    /**
     * Setter for facets
     * 
     * @param pFacets value
     */
    public void setFacets(Facets pFacets) {
        this.facets = pFacets;
    }

    /**
     * Getter for time facets
     * 
     * @return timeFacets
     */
    public TimeFacets getTimeFacets() {
        return timeFacets;
    }

    /**
     * Setter for time facets
     * 
     * @param pTimeFacets value
     */
    public void setTimeFacets(TimeFacets pTimeFacets) {
        this.timeFacets = pTimeFacets;
    }

    /**
     * Construct a string with all quality profiles' names
     * 
     * @return a string like profile1 [language1]; profile2 [language2]; profile3
     *         [language3];
     */
    public String getQualityProfilesName() {
        // gather all names
        final StringBuilder sb = new StringBuilder();

        // append each quality profile name
        for (ProfileMetaData q : project.getQualityProfiles()) {
            sb.append(q.getName()).append(" [").append(q.getLanguageName()).append("]; ");
        }

        return sb.toString();
    }

    /**
     * Construct a string with all quality profiles' filenames
     * 
     * @return a string like profile1.json profile2.json profile3.json
     */
    public String getQualityProfilesFilename() {
        // gather all names
        final StringBuilder sb = new StringBuilder();

        // append each quality profile filename
        for (ProfileMetaData q : project.getQualityProfiles()) {
            sb.append(q.getKey()).append(".json").append("; ");
        }

        return sb.toString();
    }

    /**
     * Find a rule with its key
     * 
     * @param pKey key of the rule
     * @return the rule or null if not found
     */
    public Rule getRule(String pKey) {
        // result initialization
        Rule rule = null;

        // browse all quality profile
        final Iterator<QualityProfile> iterator = getQualityProfiles().iterator();
        QualityProfile qp;

        // search for the rule with the asking key
        while (iterator.hasNext() && rule == null) {
            // get next profile
            qp = iterator.next();
            // check if the rule is in this profile
            rule = qp.find(pKey);
        }

        // return the found rule, can be null
        return rule;
    }

    /**
     * Get a list of map representing issues
     * 
     * @return return the raw issues' list
     */
    public List<Map<String, String>> getRawIssues() {
        return new ArrayList<>(rawIssues);
    }

    /**
     * Set the list of raw issues
     * 
     * @param pRawIssues list of map
     */
    public void setRawIssues(List<Map<String, String>> pRawIssues) {
        this.rawIssues = new ArrayList<>(pRawIssues);
    }

    /**
     * Get the version of the project given by the user
     * 
     * @return a string
     */
    public String getProjectVersion() {
        return this.project.getVersion();
    }

    /**
     * Get the description of the project given by the user
     * 
     * @return a string
     */
    public String getProjectDescription() {
        return this.project.getDescription();
    }

    /**
     * Get the project as an object
     * 
     * @return Project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project data
     * 
     * @param pProject data to set
     */
    public void setProject(Project pProject) {
        this.project = pProject;
    }

    /**
     * Getter for unconfirmed
     * 
     * @return issues
     */
    public List<Issue> getUnconfirmed() {
        return new ArrayList<>(unconfirmed);
    }

    /**
     * Setter for unconfirmed
     * 
     * @param pIssues value
     */
    public void setUnconfirmed(List<Issue> pIssues) {
        this.unconfirmed = new ArrayList<>(pIssues);
    }

    /**
     * Getter for qualityGateStatus
     * 
     * @return qualityGateStatus
     */
    public Map<String, String> getQualityGateStatus() {
        return qualityGateStatus;
    }

    /**
     * Setter for unconfirmed
     * 
     * @param pQualityGateStatus value
     */
    public void setQualityGateStatus(Map<String, String> pQualityGateStatus) {
        this.qualityGateStatus = pQualityGateStatus;
    }
}
