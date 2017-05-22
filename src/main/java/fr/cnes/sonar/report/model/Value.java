package fr.cnes.sonar.report.model;

/**
 * A simple pair
 * @author begarco
 */
public class Value {
    private String val;
    private int count;

    public Value(String val, int count) {
        setVal(val);
        setCount(count);
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
