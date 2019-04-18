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

import java.util.*;

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
    private List<Issue> issues;
    /**
     * List of facets of the project
     */
    private List<Facet> facets;
    /**
     * List of measures on the project
     */
    private List<Measure> measures;
    /**
     * List of map representing issues
     */
    private List<Map> rawIssues;
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
    private List<Map> components;
    /**
     * Maps of metrics stats
     */
    private Map metricsStats;

    /**
     * Default constructor
     */
    public Report() {
        this.projectName = "";
        this.projectAuthor = "";
        this.projectDate = "";
        this.qualityProfiles = new ArrayList<>();
        this.qualityGate = new QualityGate();
        this.issues = new ArrayList<>();
        this.unconfirmed = new ArrayList<>();
        this.facets = new ArrayList<>();
        this.measures = new ArrayList<>();
        this.rawIssues = new ArrayList<>();
        this.components = new ArrayList<>();
        this.project = new Project(StringManager.EMPTY, StringManager.EMPTY,
                StringManager.EMPTY,StringManager.EMPTY,StringManager.EMPTY);
    }

    /**
     * Get number of issues by issue
     * @return issues
     */
    public Map<String, Long> getIssuesFacets() {
        // returned map containing issues key/number of issues
        final Map<String, Long> lFacets = new HashMap<>();
        // collect issues' occurrences number
        long counter;
        // collect the rule's id for each issue
        String rule;

        // we browse all the issues and for each issue,
        // if it is known then we increment its counter
        // otherwise we add it to the map
        for(Issue issue : getIssues()) {
            rule = issue.getRule();
            counter = 1;
            if(lFacets.containsKey(rule)) {
                counter = lFacets.get(rule) + 1;
            }
            lFacets.put(rule, counter);
        }

        return lFacets;
    }

    public void setMetricsStats(Map metricsStats){ this.metricsStats = metricsStats; }
    public Map getMetricsStats(){return metricsStats; }

    /**
     * Getter for components
     * @return components
     */
    public List<Map> getComponents() {return new ArrayList<>(components); }

    /**
     * Setteer for components
     * @param components
     */
    public void setComponents(List<Map> components){ this.components = new ArrayList<>(components); }

    /**
     * Get issues
     * @return issues
     */
    public List<Issue> getIssues() {
        return new ArrayList<>(issues);
    }

    /**
     * Setter for issues
     * @param pIssues value
     */
    public void setIssues(List<Issue> pIssues) {
        this.issues = new ArrayList<>(pIssues);
    }

    /**
     * Getter for projectName
     * @return projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Setter for projectName
     * @param pProjectName value
     */
    public void setProjectName(String pProjectName) {
        this.projectName = pProjectName;
    }

    /**
     * Getter for projectAuthor
     * @return projectAuthor
     */
    public String getProjectAuthor() {
        return projectAuthor;
    }

    /**
     * Setter for projectAuthor
     * @param pProjectAuthor value
     */
    public void setProjectAuthor(String pProjectAuthor) {
        this.projectAuthor = pProjectAuthor;
    }

    /**
     * Getter for projectDate
     * @return projectDate
     */
    public String getProjectDate() {
        return projectDate;
    }

    /**
     * Setter for projectDate
     * @param pProjectDate value
     */
    public void setProjectDate(String pProjectDate) {
        this.projectDate = pProjectDate;
    }

    /**
     * Getter for qualityProfiles
     * @return qualityProfiles
     */
    public List<QualityProfile> getQualityProfiles() {
        return new ArrayList<>(qualityProfiles);
    }

    /**
     * Setter for qualityProfiles
     * @param pQualityProfiles value
     */
    public void setQualityProfiles(List<QualityProfile> pQualityProfiles) {
        this.qualityProfiles = new ArrayList<>(pQualityProfiles);
    }

    /**
     * Getter for qualityGate
     * @return qualityGate
     */
    public QualityGate getQualityGate() {
        return qualityGate;
    }

    /**
     * Setter for qualityGate
     * @param pQualityGate value
     */
    public void setQualityGate(QualityGate pQualityGate) {
        this.qualityGate = pQualityGate;
    }

    /**
     * Getter for measures
     * @return measures
     */
    public List<Measure> getMeasures() {
        return new ArrayList<>(measures);
    }

    /**
     * Setter for measures
     * @param pMeasures value
     */
    public void setMeasures(List<Measure> pMeasures) {
        this.measures = new ArrayList<>(pMeasures);
    }

    /**
     * Getter for facets
     * @return facets
     */
    public List<Facet> getFacets() {
        return new ArrayList<>(facets);
    }

    /**
     * Setter for facets
     * @param pFacets value
     */
    public void setFacets(List<Facet> pFacets) {
        this.facets = new ArrayList<>(pFacets);
    }

    /**
     * Construct a string with all quality profiles' names
     * @return a string like profile1 [language1]; profile2 [language2]; profile3 [language3];
     */
    public String getQualityProfilesName() {
        // gather all names
        final StringBuilder sb = new StringBuilder();

        // append each quality profile name
        Language language;
        for(ProfileMetaData q : project.getQualityProfiles()) {
            language = project.getLanguage(q.getLanguage());
            sb.append(q.getName()).append(" [").append(language.getName()).append("]; ");
        }

        return sb.toString();
    }

    /**
     * Construct a string with all quality profiles' filenames
     * @return a string like profile1.json profile2.json profile3.json
     */
    public String getQualityProfilesFilename() {
        // gather all names
        final StringBuilder sb = new StringBuilder();

        // append each quality profile filename
        for(ProfileMetaData q : project.getQualityProfiles()) {
            sb.append(q.getKey()).append(".json").append("; ");
        }

        return sb.toString();
    }

    /**
     * Find a rule with its key
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
        while(iterator.hasNext() && rule==null) {
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
     * @return return the raw issues' list
     */
    public List<Map> getRawIssues() {
        return new ArrayList<>(rawIssues);
    }

    /**
     * Set the list of raw issues
     * @param pRawIssues list of map
     */
    public void setRawIssues(List<Map> pRawIssues) {
        this.rawIssues = new ArrayList<>(pRawIssues);
    }

    /**
     * Get the version of the project given by the user
     * @return a string
     */
    public String getProjectVersion() {
        return this.project.getVersion();
    }

    /**
     * Get the description of the project given by the user
     * @return a string
     */
    public String getProjectDescription() {
        return this.project.getDescription();
    }

    /**
     * Get the project as an object
     * @return Project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project data
     * @param pProject data to set
     */
    public void setProject(Project pProject) {
        this.project = pProject;
    }


    /**
     * Getter for unconfirmed
     * @return issues
     */
    public List<Issue> getUnconfirmed() {
        return new ArrayList<>(unconfirmed);
    }

    /**
     * Setter for unconfirmed
     * @param pIssues value
     */
    public void setUnconfirmed(List<Issue> pIssues) {
        this.unconfirmed = new ArrayList<>(pIssues);
    }
}
