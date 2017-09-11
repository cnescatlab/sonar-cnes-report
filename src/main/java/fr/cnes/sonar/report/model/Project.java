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

import java.util.*;

/**
 * Represents a project
 * @author lequal
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
     * Version of the project
     */
    private String version;
    /**
     * Description of the project
     */
    private String description;
    /**
     * Quality profiles linked to the project
     */
    private ProfileMetaData[] qualityProfiles;
    /**
     * Languages of the project
     */
    private Map<String, Language> languages;

    /**
     * Constructor to set basics
     * @param key SonarQube key
     * @param name Name of the project
     * @param version Version given by the user
     * @param description Project's description
     */
    public Project(String key, String name, String version, String description) {
        this.key = key;
        this.name = name;
        this.version = version;
        this.description = description;
        this.qualityProfiles = new ProfileMetaData[0];
        this.languages = new HashMap<>();
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

    /**
     * Getter for name
     * @return name
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter for version
     * @param version value
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Getter for name
     * @return name
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description
     * @param description value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get meta data about linked quality profiles
     * @return an array of quality profiles
     */
    public ProfileMetaData[] getQualityProfiles() {
        return qualityProfiles.clone();
    }

    /**
     * Set the value of quality profiles by making a copy
     * @param qualityProfiles value to set
     */
    public void setQualityProfiles(ProfileMetaData[] qualityProfiles) {
        this.qualityProfiles = qualityProfiles.clone();
    }

    /**
     * Find a Language by key
     * @param languageKey Key of the language to get
     * @return A Language object
     */
    public Language getLanguage(String languageKey) {
        return this.languages.get(languageKey);
    }

    /**
     * Return all languages as a list
     * @return list of languages
     */
    public List<Language> getLanguages() {
        return new ArrayList<>(languages.values());
    }

    /**
     * Set the languages' values
     * @param languages map to set
     */
    public void setLanguages(Map<String, Language> languages) {
        this.languages = new HashMap<>(languages);
    }
}
