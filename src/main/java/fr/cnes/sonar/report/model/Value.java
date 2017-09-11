package fr.cnes.sonar.report.model;

/**
 * A simple pair
 * @author lequal
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

    /**
     * Complete constructor
     * @param val value to give to val
     * @param count value to give to count
     */
    public Value(String val, int count) {
        this.val = val;
        this.count = count;
    }

    /**
     * Getter for val
     * @return val
     */
    public String getVal() {
        return val;
    }

    /**
     * Setter for val
     * @param val value to give to val
     */
    public void setVal(String val) {
        this.val = val;
    }

    /**
     * Getter for count
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Setter to give to count
     * @param count count
     */
    public void setCount(int count) {
        this.count = count;
    }
}
