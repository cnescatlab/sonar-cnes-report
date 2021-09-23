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

import fr.cnes.sonar.report.exporters.xlsx.XlsXTools;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * List of components
 */
public class Components {
    
    /**
     * Property to get the list of components to exclude
     */
    private static final String EXCLUDED_COMPONENTS = "components.excluded";

    /** Components list */
    private List<Map<String,String>> componentsList;
    /** Metrics to be excluded */
    private Set<String> excludeMetricSet;

    /**
     * Default constructor
     */
    public Components() {
        this.componentsList = new ArrayList<>();
        this.excludeMetricSet = new HashSet<>(Arrays.asList(StringManager.getProperty(EXCLUDED_COMPONENTS).split(",")));
    }

    /**
     * Components list
     * @return a list of maps repesenting components
     */
    public List<Map<String, String>> getComponentsList() {
        return componentsList;
    }

    /**
     * Set components list
     * @param componentsList
     */
    public void setComponentsList(List<Map<String, String>> componentsList) {
        this.componentsList = componentsList;
    }

    /**
     * Metrics to be excluded
     * @return a set of metrics to be excluded
     */
    public Set<String> getExcludeMetricSet() {
        return excludeMetricSet;
    }

    /**
     * Set metrics to be excluded
     * @param excludeMetricSet
     */
    public void setExcludeMetricSet(Set<String> excludeMetricSet) {
        this.excludeMetricSet = excludeMetricSet;
    }

    /**
     * Generate a map with all metrics stats (for numerical metrics)
     * Generate a map with `min<metric name>`, `max<metric name>`, `median<metric name>`
     * as keys and min, max or median as value (converted in double)
     * @return map with min, max and median of each numerical metric in the project
     * */
    public Map<String, Double> getMetricStats(){
        
        Map<String, Double> map = new HashMap<>();
        // Use first component to gets all metrics names

        if(! componentsList.isEmpty()) {
            // for each metric
            for(Object metric: XlsXTools.extractHeader(componentsList)){
                // if metric is numerical
                if (isCountableMetric(metric.toString())) {
                    // Get min, max and median of this metric on the current project
                    map.put("min" + metric.toString(), getMinMetric(metric.toString()));
                    map.put("max" + metric.toString(), getMaxMetric(metric.toString()));
                    map.put("median" + metric.toString(), getMedianMetric(metric.toString()));
                }
            }
        }

        return map;
    }

    protected boolean isCountableMetric(String metric){

        boolean isCountable;

        // The metric is to be excluded
        if(excludeMetricSet.contains(metric)){
            isCountable = false;
        } else {
            // Get first non-null value of metrics
            int i=0;
            while(i<componentsList.size() && (componentsList.get(i) == null || componentsList.get(i).get(metric) == null)){
                ++i;
            }

            // If we didn't reach the end and we find a numerical value
            isCountable = i<componentsList.size() && NumberUtils.isCreatable(componentsList.get(i).get(metric));
        }

        return isCountable;
    }

    /**
     * Get min value for a specified metric
     */
    protected double getMinMetric(String metric){
        double min = Double.MAX_VALUE;
        for(Map<String,String> c: componentsList){
            final String rawValue = c.get(metric);
            if(rawValue!=null){
                min = Math.min(min, Double.valueOf(rawValue));
            }
        }
        return min;
    }

    /**
     * Get max value for a specified metric
     */
    protected double getMaxMetric(String metric){
        double max = -Double.MAX_VALUE;
        for(Map<String,String> c: componentsList){
            final String rawValue = c.get(metric);
            if(rawValue!=null){
                max = Math.max(max, Double.valueOf(rawValue));
            }
        }
        return max;
    }

    /**
     * Get the median of a specified metric
     */
    protected double getMedianMetric(String metric) {
        List<Double> values = new ArrayList<>(); 
        for(Map<String,String> c: componentsList){
            final String rawValue = c.get(metric);
            if(rawValue!=null){
                values.add(Double.valueOf(rawValue));
            }
        }
        Median median = new Median();
        double[] valuesArray = ArrayUtils.toPrimitive(values.toArray(new Double[values.size()]));
        return Precision.round(median.evaluate(valuesArray), 1);
    }
}