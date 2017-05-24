package fr.cnes.sonar.report.model;

import java.util.Iterator;

/**
 * Contain all Quality Profile's details
 * @author begarco
 */
public class QualityProfile {
    private ProfileData data;
    private ProfileMetaData metaData;
    private Project[] projects;

    public QualityProfile(ProfileData data, ProfileMetaData metaData) {
        this.data = data;
        this.metaData = metaData;
    }

    public Project[] getProjects() {
        return projects;
    }

    public void setProjects(Project[] projects) {
        this.projects = projects;
    }

    public String getName() {
        return this.metaData.getName();
    }

    public String getConf() {
        return data.getConf();
    }

    public String getKey() {
        return metaData.getKey();
    }

    /**
     * Find a rule with its key
     * @param key key of the rule
     * @return the rule or null if not found
     */
    public Rule find(String key) {
        Rule rule = null;

        Iterator<Rule> iterator = data.getRules().iterator();

        // naive search
        while(iterator.hasNext() && rule==null) {
            Rule r = iterator.next();
            rule = r.getKey().equals(key) ? r : null;
        }

        return rule;
    }
}
