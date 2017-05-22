package fr.cnes.sonar.report.model;

/**
 * Represents a project
 * @author begarco
 */
public class Project {
    private String key;
    private String name;

    public Project(String key, String value) {
        setKey(key);
        setName(value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
