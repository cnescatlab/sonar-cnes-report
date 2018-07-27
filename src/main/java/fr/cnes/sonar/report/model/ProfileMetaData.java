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

    /**
     * Setter for key
     * @param pKey value to set
     */
    public void setKey(String pKey) {
        this.key = pKey;
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
     * @param pName value to set
     */
    public void setName(String pName) {
        this.name = pName;
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
     * @param pLanguage language
     */
    public void setLanguage(String pLanguage) {
        this.language = pLanguage;
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
     * @param pLanguageName  language's name
     */
    public void setLanguageName(String pLanguageName) {
        this.languageName = pLanguageName;
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
     * @param pInherited inheritance boolean
     */
    public void setInherited(Boolean pInherited) {
        isInherited = pInherited;
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
     * @param pIsDefault default language boolean
     */
    public void setDefault(Boolean pIsDefault) {
        this.isDefault = pIsDefault;
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
     * @param pDeprecatedRules deprecated rules number
     */
    public void setDeprecatedRules(int pDeprecatedRules) {
        this.deprecatedRules = pDeprecatedRules;
    }
}
