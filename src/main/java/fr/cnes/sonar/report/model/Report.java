package fr.cnes.sonar.report.model;

import java.util.Iterator;
import java.util.List;

/**
 * Model of a report containing all information
 * @author begarco
 */
public class Report {
    private String projectName;
    private String projectAuthor;
    private String projectDate;
    private List<QualityProfile> qualityProfiles;
    private QualityGate qualityGate;
    private List<Issue> issues;
    private List<Facet> facets;
    private List<Measure> measures;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectAuthor() {
        return projectAuthor;
    }

    public void setProjectAuthor(String projectAuthor) {
        this.projectAuthor = projectAuthor;
    }

    public String getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(String projectDate) {
        this.projectDate = projectDate;
    }

    public List<QualityProfile> getQualityProfiles() {
        return qualityProfiles;
    }

    public void setQualityProfiles(List<QualityProfile> qualityProfiles) {
        this.qualityProfiles = qualityProfiles;
    }

    public QualityGate getQualityGate() {
        return qualityGate;
    }

    public void setQualityGate(QualityGate qualityGate) {
        this.qualityGate = qualityGate;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }

    public List<Facet> getFacets() {
        return facets;
    }

    public void setFacets(List<Facet> facets) {
        this.facets = facets;
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
            sb.append(q.getName()).append(" ");
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
            sb.append(q.getKey()).append(".json").append(" ");
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

        // browse all qualilty profile
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
}
