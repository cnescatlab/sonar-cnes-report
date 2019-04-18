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
import fr.cnes.sonar.report.model.Component;
import fr.cnes.sonar.report.model.SonarQubeServer;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentProvider extends AbstractDataProvider {
    /**
     * Constructor.
     *
     * @param server  SonarQube server.
     * @param token   String representing the user token.
     * @param project The id of the component to report.
     */

    ArrayList<Map> components;
    public ComponentProvider(final SonarQubeServer server, final String token, final String project) {
        super(server, token, project);
        components =  new ArrayList<>();
    }

    /**
    * Getter for components, retrieve all component from a project and their metrics
     * @return components List of components
    */
    public List<Map> getComponents() throws BadSonarQubeRequestException, SonarQubeException {
        int page = 1;
        JsonObject jo;

        // For each page, we get the components
        boolean goOn = components.size() == 0;
        while(goOn){
            // Send request to server
            jo = request(String.format(getRequest(GET_COMPONENTS_REQUEST),
                    getServer().getUrl(), getProjectKey(), page, getRequest(MAX_PER_PAGE_SONARQUBE)));

            // Get components from response
            final Component[] tmp = (getGson().fromJson(jo.get(COMPONENTS), Component[].class));

            for (Component c:tmp) {
                Map map = c.toMap();
                components.add(map);
            }

            // Check if we reach the end
            final int number = jo.getAsJsonObject(PAGING).get(TOTAL).getAsInt();
            goOn =  page * Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)) < number;
            page++;
        }

        return components;
    }

    /**
     * Get min value for a specified metrics
     * */
    private double getMinMetric(String metric){
        double min = Double.valueOf((String)components.get(0).get(metric));
        for(Map c:components){
            min = Math.min(min, Double.valueOf((String)c.get(metric)));
        }
        return min;
    }

    /**
     * Get max value for a specified metrics
     * */
    private double getMaxMetric(String metric){
        double max = Double.valueOf((String)components.get(0).get(metric));
        for(Map c:components){
            max = Math.max(max, Double.valueOf((String)c.get(metric)));
        }
        return max;
    }

    /**
     * Get mean value for a specified metrics
     * */
    private double getMeanMetric(String metric){
        double sum = 0;
        for(Map c:components){
            sum += Double.valueOf((String)c.get(metric));
        }
        // Return mean with 2 digits
        return Math.floor(100 * sum / (double) components.size()) / 100.;
    }


    /**
     * Generate a map with all metrics stats (for numerical metrics)
     * */
    public Map<String, Double> getMetricStats(){
        if(components.size() == 0) return new HashMap<>();
        Map<String, Double> map = new HashMap();
        Object[] metrics = components.get(0).keySet().toArray();

        for(Object metric:metrics){
            if (NumberUtils.isCreatable(components.get(0).get(metric.toString()).toString())) {
                map.put("min" + metric.toString(), getMinMetric(metric.toString()));
                map.put("max" + metric.toString(), getMaxMetric(metric.toString()));
                map.put("mean" + metric.toString(), getMeanMetric(metric.toString()));
            }
        }

        return map;
    }

}
