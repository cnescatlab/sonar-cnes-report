package fr.cnes.sonar.report.params;

import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;

import java.util.HashMap;

/**
 * Parameters of the command
 * @author begarco
 */
public class Params {
    private HashMap<String,String> params;

    public Params() {
        params = new HashMap<>();
    }

    /**
     * @param key Key of the parameter to check
     * @return true if a parameter exists
     */
    public boolean contains(String key) {
        return this.params.containsKey(key);
    }

    /**
     * @param key key of the parameter to find
     * @return The parameter value if it exists
     * @throws UnknownParameterException when a parameter does not exist
     */
    public String get(String key) throws UnknownParameterException {
        if(!contains(key)) {
            throw new UnknownParameterException(key);
        }
        return params.get(key);
    }

    /**
     * Add a parameter to the list
     * @param key the key of the parameter
     * @param value the value of the parameter
     */
    public void put(String key, String value) {
        this.params.put(key, value);
    }

    /**
     * Check if is itself correct
     * @return yes if the params are correct
     * @throws MissingParameterException when a parameter is not given
     */
    public boolean isReliable() throws MissingParameterException {
        for(HashMap.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if("".equals(value)) {
                throw new MissingParameterException(key);
            }
        }
        return true;
    }
}
