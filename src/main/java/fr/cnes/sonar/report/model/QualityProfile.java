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

import java.util.Iterator;
import java.util.List;

/**
 * Contain all Quality Profile's details
 */
public class QualityProfile {
    /**
     * Contains resources like rules and configuration files
     */
    private ProfileData data;
    /**
     * Contains metadata of the profile
     */
    private ProfileMetaData metaData;
    /**
     * List of linked projects
     */
    private Project[] projects;

    /**
     * Complete constructor
     * @param pData all the rules and configuration
     * @param pMetaData resources like name, size, default, etc.
     */
    public QualityProfile(ProfileData pData, ProfileMetaData pMetaData) {
        this.data = pData;
        this.metaData = pMetaData;
    }

    /**
     * Getter for projects
     * @return projects
     */
    public Project[] getProjects() {
        return projects.clone();
    }

    /**
     * Setter for projects
     * @param pProjects value
     */
    public void setProjects(Project[] pProjects) {
        this.projects = pProjects.clone();
    }

    /**
     * Getter for project's name
     * @return project's name
     */
    public String getName() {
        return this.metaData.getName();
    }

    /**
     * Getter for configuration
     * @return configuration
     */
    public String getConf() {
        return data.getConf();
    }

    /**
     * Getter for rules
     * @return rules
     */
    public List<Rule> getRules() {
        return data.getRules();
    }

    /**
     * Getter for key
     * @return key
     */
    public String getKey() {
        return metaData.getKey();
    }

    /**
     * Find a rule with its key
     * @param key key of the rule
     * @return the rule or null if not found
     */
    public Rule find(String key) {
        // initialization of the result
        Rule rule = null;

        // iterator on profile's rules
        final Iterator<Rule> iterator = data.getRules().iterator();

        // search for the rule with the asking key
        while(iterator.hasNext() && rule==null) {
            // get current rule
            final Rule r = iterator.next();
            // check the current rule's key equals to wanted key
            if(r.getKey().equals(key)) {
                rule = r;
            }
        }

        return rule;
    }
}
