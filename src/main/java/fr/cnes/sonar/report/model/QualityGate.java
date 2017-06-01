package fr.cnes.sonar.report.model;


import com.google.gson.annotations.SerializedName;

/**
 * Contain all Quality Gate's details
 * @author begarco
 */
public class QualityGate {
    /**
     * ID in SonarQube
     */
    private String id;
    /**
     * Name in SonarQube
     */
    private String name;
    /**
     * True if it is the default Quality gate in SonarQube
     */
    @SerializedName("default")
    private boolean defaultQG;
    /**
     * Raw string containing xml configuration
     */
    private String conf;

    /**
     * Default constructor
     */
    public QualityGate() {
        this.id = "";
        this.name = "";
        this.defaultQG = false;
        this.conf = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return true if it is the default quality gate
     * @return boolean
     */
    public boolean isDefault() {
        return defaultQG;
    }

    /**
     * Set the default field
     * @param defaultQG true if it is the default gate
     */
    public void setDefault(boolean defaultQG) {
        this.defaultQG = defaultQG;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Overridden toString
     * @return a string containing all data separated by blanks
     */
    @Override
    public String toString() {
        return getId() + " " + getName() + " " + isDefault() + " " + getConf();
    }
}
