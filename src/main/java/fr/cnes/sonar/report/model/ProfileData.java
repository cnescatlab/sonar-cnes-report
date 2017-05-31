package fr.cnes.sonar.report.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Contain all rules of the profile
 * @author begarco
 */
public class ProfileData {
    /**
     * List of rules activated in the profile
     */
    private List<Rule> rules;
    /**
     * Configuration file as string
     */
    private String conf;

    /**
     * Default constructor
     */
    public ProfileData() {
        this.rules = new ArrayList<>();
        this.conf = "";
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }
}
