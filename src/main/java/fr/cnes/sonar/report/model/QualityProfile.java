package fr.cnes.sonar.report.model;

import java.util.Iterator;

/**
 * Contain all Quality Profile's details
 * @author begarco
 */
public class QualityProfile {
    /**
     * Contains data like rules and configuration files
     */
    private ProfileData data;
    /**
     * Contains metadata of the profile
     */
    private ProfileMetaData metaData;
    /**
     * List of linked projects
     */
    private Project[] projects;

    /**
     * Complete constructor
     * @param data all the rules and configuration
     * @param metaData data like name, size, default, etc.
     */
    public QualityProfile(ProfileData data, ProfileMetaData metaData) {
        this.data = data;
        this.metaData = metaData;
    }

    /**
     * Getter for projects
     * @return projects
     */
    public Project[] getProjects() {
        return projects.clone();
    }

    /**
     * Setter for projects
     * @param projects value
     */
    public void setProjects(Project[] projects) {
        this.projects = projects.clone();
    }

    /**
     * Getter for project's name
     * @return project's name
     */
    public String getName() {
        return this.metaData.getName();
    }

    /**
     * Getter for configuration
     * @return configuration
     */
    public String getConf() {
        return data.getConf();
    }

    /**
     * Getter for key
     * @return key
     */
    public String getKey() {
        return metaData.getKey();
    }

    /**
     * Find a rule with its key
     * @param key key of the rule
     * @return the rule or null if not found
     */
    public Rule find(String key) {
        // initialization of the result
        Rule rule = null;

        // iterator on profile's rules
        Iterator<Rule> iterator = data.getRules().iterator();

        // search for the rule with the asking key
        while(iterator.hasNext() && rule==null) {
            // get current rule
            Rule r = iterator.next();
            // check the current rule's key equals to wanted key
            if(r.getKey().equals(key)) {
                rule = r;
            }
        }

        return rule;
    }
}
