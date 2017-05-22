package fr.cnes.sonar.report.model;

import java.util.List;

/**
 * A facet of sonarqube
 * @author garconb
 */
public class Facet {
    private List<Value> values;
    private String property;

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
