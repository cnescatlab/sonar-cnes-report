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
 * Contain all Quality Gate's details
 * @author lequal
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

    /**
     * Getter for the quality gate's name
     * @return quality gate's name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the quality gate's name
     * @param name quality gate's name
     */
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

    /**
     * Getter for the raw configuration
     * @return quality gate's configuration as string in json format
     */
    public String getConf() {
        return conf;
    }

    /**
     * Setter for the raw configuration
     * @param conf quality gate's configuration as string in json format
     */
    public void setConf(String conf) {
        this.conf = conf;
    }

    /**
     * Getter for the quality gate's id
     * @return quality gate's id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the quality gate's id
     * @param id quality gate's id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Overridden toString
     * @return a string containing all resources separated by blanks
     */
    @Override
    public String toString() {
        return getId() + " " + getName() + " " + isDefault() + " " + getConf();
    }
}
