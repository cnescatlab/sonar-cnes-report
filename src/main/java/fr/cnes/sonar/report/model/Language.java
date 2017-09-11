package fr.cnes.sonar.report.model;

/**
 * An analyzed language of sonarqube
 * @author lequal
 */
public class Language {
    /** Language's key */
    private String key;
    /** Language's name*/
    private String name;

    /**
     * Default constructor
     */
    public Language() {
        this.key = "";
        this.name = "";
    }

    /**
     * Language's key
     * @return a string
     */
    public String getKey() {
        return key;
    }

    /**
     * Set language's key
     * @param key value to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Language's name
     * @return a String
     */
    public String getName() {
        return name;
    }

    /**
     * Set language's name
     * @param name value to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
