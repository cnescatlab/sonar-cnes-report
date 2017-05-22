package fr.cnes.sonar.report.model;


import com.google.gson.annotations.SerializedName;

/**
 * Contain all profile
 * @author begarco
 */
public class ProfileMetaData {
    private String key;
    private String name;
    private String language;
    private String languageName;
    private Boolean isInherited;
    private Boolean isDefault;
    @SerializedName("activeDeprecatedRuleCount")
    private int deprecatedRules;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Boolean isInherited() {
        return isInherited;
    }

    public void setInherited(Boolean inherited) {
        isInherited = inherited;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getDeprecatedRules() {
        return deprecatedRules;
    }

    public void setDeprecatedRules(int deprecatedRules) {
        this.deprecatedRules = deprecatedRules;
    }
}
