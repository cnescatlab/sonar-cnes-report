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

import java.util.ArrayList;
import java.util.List;

/**
 * Contain all rules of the profile
 */
public class ProfileData {
    /**
     * List of rules activated in the profile
     */
    private List<Rule> rules;
    /**
     * Configuration file as string
     */
    private String conf;

    /**
     * Default constructor
     */
    public ProfileData() {
        this.rules = new ArrayList<>();
        this.conf = "";
    }

    /**
     * Getter for rules' list
     * @return rules' list
     */
    public List<Rule> getRules() {
        return new ArrayList<>(rules);
    }

    /**
     * Setter for rules
     * @param pRules a list of rules
     */
    public void setRules(List<Rule> pRules) {
        this.rules = new ArrayList<>(pRules);
    }

    /**
     * Getter for the configuration
     * @return configuration as String
     */
    public String getConf() {
        return conf;
    }

    /**
     * Setter for the configuration
     * @param pConf configuration as String
     */
    public void setConf(String pConf) {
        this.conf = pConf;
    }
}
