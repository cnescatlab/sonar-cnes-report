package fr.cnes.sonar.report.model;


import com.google.gson.annotations.SerializedName;

/**
 * Contain all Quality Gate's details
 * @author begarco
 */
public class QualityGate {
    private Integer id;
    private String name;
    @SerializedName("default")
    private Boolean defaultQG;
    private String conf;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isDefault() {
        return defaultQG;
    }

    public void setDefault(Boolean defaultQG) {
        this.defaultQG = defaultQG;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getId() + " " + getName() + " " + isDefault() + " " + getConf();
    }
}
