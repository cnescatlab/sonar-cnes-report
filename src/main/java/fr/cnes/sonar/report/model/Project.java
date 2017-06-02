package fr.cnes.sonar.report.model;

/**
 * Represents a project
 * @author begarco
 */
public class Project {
    /**
     * Key used by sonarqube
     */
    private String key;
    /**
     * Name of the project
     */
    private String name;

    /**
     * Constructor to set basics
     * @param key SonarQube key
     * @param name Name of the project
     */
    public Project(String key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * Getter for key
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter for name
     * @param key name
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name value
     */
    public void setName(String name) {
        this.name = name;
    }
}
