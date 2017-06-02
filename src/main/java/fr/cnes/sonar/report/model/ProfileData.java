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

    /**
     * Getter for rules' list
     * @return rules' list
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Setter for rules
     * @param rules a list of rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Getter for the configuration
     * @return configuration as String
     */
    public String getConf() {
        return conf;
    }

    /**
     * Setter for the configuration
     * @param conf configuration as String
     */
    public void setConf(String conf) {
        this.conf = conf;
    }
}
