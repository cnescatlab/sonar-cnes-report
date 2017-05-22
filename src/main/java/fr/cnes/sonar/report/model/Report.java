package fr.cnes.sonar.report.model;

import java.util.List;

/**
 * Model of a report containing all information
 * @author begarco
 */
public class Report {
    private String projectName;
    private String projectAuthor;
    private String projectDate;
    private QualityProfile qualityProfile;
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

    public QualityProfile getQualityProfile() {
        return qualityProfile;
    }

    public void setQualityProfile(QualityProfile qualityProfile) {
        this.qualityProfile = qualityProfile;
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
}
