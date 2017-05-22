package fr.cnes.sonar.report.model;


import java.util.List;

/**
 * Contain all rules of the profile
 * @author begarco
 */
public class ProfileData {
    private List<Rule> rules;
    private String conf;

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
