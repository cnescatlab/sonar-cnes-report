package fr.cnes.sonar.report.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Model of a report containing all information
 * @author begarco
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
     * Default constructor
     */
    public Report() {
        this.projectName = "";
        this.projectAuthor = "";
        this.projectDate = "";
        this.qualityProfiles = new ArrayList<>();
        this.qualityGate = new QualityGate();
        this.issues = new ArrayList<>();
        this.facets = new ArrayList<>();
        this.measures = new ArrayList<>();
    }

    /**
     * Getter for issues
     * @return issues
     */
    public List<Issue> getIssues() {
        return new ArrayList<>(issues);
    }

    /**
     * Setter for issues
     * @param issues value
     */
    public void setIssues(List<Issue> issues) {
        this.issues = new ArrayList<>(issues);
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
     * @param projectName value
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
     * @param projectAuthor value
     */
    public void setProjectAuthor(String projectAuthor) {
        this.projectAuthor = projectAuthor;
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
     * @param projectDate value
     */
    public void setProjectDate(String projectDate) {
        this.projectDate = projectDate;
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
     * @param qualityProfiles value
     */
    public void setQualityProfiles(List<QualityProfile> qualityProfiles) {
        this.qualityProfiles = new ArrayList<>(qualityProfiles);
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
     * @param qualityGate value
     */
    public void setQualityGate(QualityGate qualityGate) {
        this.qualityGate = qualityGate;
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
     * @param measures value
     */
    public void setMeasures(List<Measure> measures) {
        this.measures = new ArrayList<>(measures);
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
     * @param facets value
     */
    public void setFacets(List<Facet> facets) {
        this.facets = new ArrayList<>(facets);
    }

    /**
     * Construct a string with all quality profiles' names
     * @return a string like profile1 profile2 profile3
     */
    public String getQualityProfilesName() {
        // gather all names
        StringBuilder sb = new StringBuilder();

        // append each quality profile name
        for(QualityProfile q : getQualityProfiles()) {
            sb.append(q.getName()).append(' ');
        }

        return sb.toString();
    }

    /**
     * Construct a string with all quality profiles' filenames
     * @return a string like profile1.json profile2.json profile3.json
     */
    public String getQualityProfilesFilename() {
        // gather all names
        StringBuilder sb = new StringBuilder();

        // append each quality profile filename
        for(QualityProfile q : getQualityProfiles()) {
            sb.append(q.getKey()).append(".json").append(' ');
        }

        return sb.toString();
    }

    /**
     * Find a rule with its key
     * @param key key of the rule
     * @return the rule or null if not found
     */
    public Rule getRule(String key) {
        // result initialization
        Rule rule = null;

        // browse all quality profile
        Iterator iterator = getQualityProfiles().iterator();

        // search for the rule with the asking key
        while(iterator.hasNext() && rule==null) {
            // get next profile
            QualityProfile qp = (QualityProfile) iterator.next();
            // check if the rule is in this profile
            rule = qp.find(key);
        }

        // return the found rule, can be null
        return rule;
    }

    /**
     * Get a list of map representing issues
     */
    public List<Map> getRawIssues() {
        return new ArrayList<>(rawIssues);
    }

    /**
     * Set the list of raw issues
     * @param rawIssues list of map
     */
    public void setRawIssues(List<Map> rawIssues) {
        this.rawIssues = new ArrayList<>(rawIssues);
    }
}
