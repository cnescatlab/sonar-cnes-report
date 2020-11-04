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

package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Rule;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides hotspot items
 */
public class HotspotsProvider extends AbstractDataProvider {

    /**
     * Correspond to the maximum number of hotspots that SonarQube allow
     * web api's users to collect
     */
    private static final int MAXIMUM_HOTSPOTS_LIMIT = 10000;

    /**
     * Complete constructor
     * @param pServer SonarQube server
     * @param pToken String representing the user token
     * @param pProject The id of the project to report
     * @param pBranch The branch of the project to report
     */
    public HotspotsProvider(final SonarQubeServer pServer, final String pToken, final String pProject,
            final String pBranch){
        super(pServer, pToken, pProject, pBranch);
    }

    /**
     * Get all the hotspots of a project in a raw format (map)
     * @return Array containing all the hotspots as maps
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable
     */
    public List<Map> getRawHotspots() throws BadSonarQubeRequestException, SonarQubeException {
        // results variable
        final List<Map> res = new ArrayList<>();

        // stop condition
        boolean goon = true;
        // flag when there are too many violation (> MAXIMUM_HOTSPOTS_LIMIT)
        boolean overflow = false;
        // current page
        int page = 1;

        //search all hotspots of the projects
        while(goon){
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));

            // prepare the server to get all the hotspots
            final String request = String.format(getRequest(GET_HOTSPOTS_REQUEST),
                    getServer().getUrl(), getProjectKey(), maxPerPage, page, getBranch());
            // perform the request to the server
            final JsonObject jo = request(request);
            // transform json to Hotspot objects
            final Map[] tmp = (getGson().fromJson(jo.get(HOTSPOTS), Map[].class));
            // add them to the final result
            res.addAll(Arrays.asList(tmp));
            // check next results' pages
            int number = jo.get(TOTAL).getAsInt();

            //check overflow
            if(number > MAXIMUM_HOTSPOTS_LIMIT){
                number = MAXIMUM_HOTSPOTS_LIMIT;
                overflow = true;
            }

            goon = page * maxPerPage < number;
            page++;
        }
        //in case of overflow we log the problem
        if(overflow){
            LOGGER.warning(StringManager.string(StringManager.HOTSPOTS_OVERFLOW_MSG));
        }

        //return the hotspots
        return res;
    }

   /**
     * Get all the stats on a project
     * @return A list of facets
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException When SonarQube server is not callable.
     */
    public List<Facet> getFacets() throws BadSonarQubeRequestException, SonarQubeException {

        // prepare the request
        final String request = String.format(getRequest(GET_FACETS_REQUEST),
                getServer().getUrl(), getProjectKey(), getBranch());
        // contact the server to request the resources as json
        final JsonObject jo = request(request);
        // put wanted resources in facets array and list
        final Facet [] tmp = (getGson().fromJson(jo.get(FACETS), Facet[].class));

        // return list of facets
        return new ArrayList<>(Arrays.asList(tmp));
    }
}
