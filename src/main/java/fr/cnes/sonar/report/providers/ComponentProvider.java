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
import fr.cnes.sonar.report.exporters.xlsx.XlsXTools;
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
     * @param project The branch of the component to report.
     */
    ArrayList<Map> componentsList;
    public ComponentProvider(final SonarQubeServer server, final String token, final String project,
            final String branch) {
        super(server, token, project, branch);
        componentsList =  new ArrayList<>();
    }

    /**
    * Getter for componentsList, retrieve all component from a project and their metrics
     * @return componentsList List of componentsList
    */
    public List<Map> getComponents() throws BadSonarQubeRequestException, SonarQubeException {
        int page = 1;
        JsonObject jo;

        // For each page, we get the components
        boolean goOn = componentsList.isEmpty();
        while(goOn){
            // Send request to server
            jo = request(String.format(getRequest(GET_COMPONENTS_REQUEST),
                    getServer().getUrl(), getProjectKey(), page, getRequest(MAX_PER_PAGE_SONARQUBE), getBranch()));

            // Get components from response
            final Component[] tmp = getGson().fromJson(jo.get(COMPONENTS), Component[].class);
            for (Component c:tmp) {
                Map map = c.toMap();
                componentsList.add(map);
            }

            // Check if we reach the end
            final int number = jo.getAsJsonObject(PAGING).get(TOTAL).getAsInt();
            goOn =  page * Integer.valueOf(getRequest(MAX_PER_PAGE_SONARQUBE)) < number;
            page++;
        }

        return componentsList;
    }

    /**
     * Get min value for a specified metrics
     * */
    private double getMinMetric(String metric){
        double min = Double.MAX_VALUE;
        for(Map c: componentsList){
            final String rawValue = (String)c.get(metric);
            if(rawValue!=null){
                min = Math.min(min, Double.valueOf(rawValue));
            }
        }
        return min;
    }

    /**
     * Get max value for a specified metrics
     * */
    private double getMaxMetric(String metric){
        double max = -Double.MAX_VALUE;
        for(Map c: componentsList){
            final String rawValue = (String)c.get(metric);
            if(rawValue!=null){
                max = Math.max(max, Double.valueOf(rawValue));
            }
        }
        return max;
    }

    /**
     * Generate a map with all metrics stats (for numerical metrics)
     * Generate a map with `min<metric name>`, `max<metric name>`, `mean<metric name>`
     * as keys and min, max or mean as value (converted in double)
     * @return map with min, max and mean of each numerical metric in the project
     * */
    public Map<String, Double> getMetricStats(){
        
        Map<String, Double> map = new HashMap<>();
        // Use first component to gets all metrics names

        if(! componentsList.isEmpty()) {
            // for each metric
            for(Object metric: XlsXTools.extractHeader(componentsList)){
                // if metric is numerical
                if (isCountableMetric(metric.toString())) {
                    // Get min, max and mean of this metric on the current project
                    map.put("min" + metric.toString(), getMinMetric(metric.toString()));
                    map.put("max" + metric.toString(), getMaxMetric(metric.toString()));
                }
            }
        }

        return map;
    }


    private boolean isCountableMetric(String metric){
        // Get first non-null value of metrics
        int i=0;
        while(i<componentsList.size() && (componentsList.get(i) == null || componentsList.get(i).get(metric) == null)){
            ++i;
        }

        // If we didn't reach the end and we find a numerical value
        return i<componentsList.size() && NumberUtils.isCreatable(componentsList.get(i).get(metric).toString());
    }

}
