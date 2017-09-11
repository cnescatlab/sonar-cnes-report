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
