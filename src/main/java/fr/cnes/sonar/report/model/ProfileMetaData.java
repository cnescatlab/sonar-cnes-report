/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.model;

import com.google.gson.annotations.SerializedName;

/**
 * Contain all profile
 * @author lequal
 */
public class ProfileMetaData {
    /**
     * sonarqube key of the profile
     */
    private String key;
    /**
     * profile's name in sonarqube
     */
    private String name;
    /**
     * related language's id
     */
    private String language;
    /**
     *  language full name
     */
    private String languageName;
    /**
     * is this profile inherited from another one
     */
    private boolean isInherited;
    /**
     * is this profile the default one
     */
    private boolean isDefault;
    /**
     * how many deprecated rules it contains
     */
    @SerializedName("activeDeprecatedRuleCount")
    private int deprecatedRules;

    /**
     * Default constructor
     */
    public ProfileMetaData() {
        super();
    }

    /**
     * Getter for key
     * @return key
     */
    public String getKey() {
        return key;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for language
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Setter for language
     * @param language  language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Getter for language's name
     * @return language's name
     */
    public String getLanguageName() {
        return this.languageName;
    }

    /**
     * Setter for language's name
     * @param languageName  language's name
     */
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    /**
     * Getter for inheritance boolean
     * @return inheritance boolean
     */
    public Boolean isInherited() {
        return isInherited;
    }

    /**
     * Getter for inheritance boolean
     * @param inherited inheritance boolean
     */
    public void setInherited(Boolean inherited) {
        isInherited = inherited;
    }

    /**
     * Getter for default language boolean
     * @return default language boolean
     */
    public Boolean isDefault() {
        return isDefault;
    }

    /**
     * Setter for default language boolean
     * @param isDefault default language boolean
     */
    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Getter for deprecated rules number
     * @return deprecated rules number
     */
    public int getDeprecatedRules() {
        return deprecatedRules;
    }

    /**
     * Setter for deprecated rules number
     * @param deprecatedRules deprecated rules number
     */
    public void setDeprecatedRules(int deprecatedRules) {
        this.deprecatedRules = deprecatedRules;
    }
}
