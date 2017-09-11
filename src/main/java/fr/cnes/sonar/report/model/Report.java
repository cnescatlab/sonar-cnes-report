package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.input.StringManager;

import java.util.*;

/**
 * Model of a report containing all information
 * @author lequal
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
        this.rawIssues = new ArrayList<>();
        this.project = new Project(StringManager.EMPTY, StringManager.EMPTY,StringManager.EMPTY,StringManager.EMPTY);
    }

    /**
     * Get number of issues by issue
     * @return issues
     */
    public Map<String, Long> getIssuesFacets() {
        // returned map containing issues key/number of issues
        Map<String, Long> facets = new HashMap<>();
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
            if(facets.containsKey(rule)) {
                counter = facets.get(rule) + 1;
            }
            facets.put(rule, counter);
        }

        return facets;
    }

    /**
     * Get issues
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
     * @return a string like profile1 [language1]; profile2 [language2]; profile3 [language3];
     */
    public String getQualityProfilesName() {
        // gather all names
        StringBuilder sb = new StringBuilder();

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
        StringBuilder sb = new StringBuilder();

        // append each quality profile filename
        for(ProfileMetaData q : project.getQualityProfiles()) {
            sb.append(q.getKey()).append(".json").append("; ");
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
        Iterator<QualityProfile> iterator = getQualityProfiles().iterator();
        QualityProfile qp;

        // search for the rule with the asking key
        while(iterator.hasNext() && rule==null) {
            // get next profile
            qp = iterator.next();
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
     * @param project data to set
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
