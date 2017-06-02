package fr.cnes.sonar.report.model;

/**
 * Represents a measure
 * @author begarco
 */
public class Measure {
    /**
     * Name of the metric
     */
    private String metric;
    /**
     * Value of the metric
     */
    private String value;

    /**
     * Default constructor
     */
    public Measure() {
        this.metric = "";
        this.value = "";
    }

    /**
     * Complete constructor
     * @param metric value for metric
     * @param value value for value
     */
    public Measure(String metric, String value) {
        this.metric = metric;
        this.value = value;
    }

    /**
     * Getter for metric's name
     * @return metric's name
     */
    public String getMetric() {
        return metric;
    }

    /**
     * Setter for metric's name
     * @param metric value
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * Getter for value
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for value
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Overridden method toString
     * @return "metric":"value"
     */
    @Override
    public String toString() {
        return getMetric()+":"+getValue();
    }
}
