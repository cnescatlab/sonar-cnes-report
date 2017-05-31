package fr.cnes.sonar.report.model;

/**
 * A simple pair
 * @author begarco
 */
public class Value {
    /**
     * Value to count
     */
    private String val;
    /**
     * Number of occurrences
     */
    private int count;

    public Value(String val, int count) {
        this.val = val;
        this.count = count;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
