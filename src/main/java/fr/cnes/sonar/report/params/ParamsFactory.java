package fr.cnes.sonar.report.params;

import fr.cnes.sonar.report.exceptions.MalformedParameterException;
import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;

import java.util.logging.Logger;

/**
 * Prepares command line's arguments
 * @author begarco
 */
public class ParamsFactory {

    private static final Logger LOGGER = Logger.getLogger(ParamsFactory.class.getName());
    private static final String PARAMETER_START = "--";

    /**
     * Generates a full parameters object from user cli
     * @param args
     * @return
     * @throws UnknownParameterException
     * @throws MalformedParameterException
     */
    public Params create(String[] args) throws UnknownParameterException, MalformedParameterException, MissingParameterException {
        Params params = new Params();
        String parameter = null;

        loadDefault(params);

        for (String arg : args) {
            if(parameter==null) {
                if(checkParameter(arg)) {
                    parameter = extractParameterName(arg);
                } else {
                    throw new MalformedParameterException(arg);
                }
            } else {
                if(params.contains(parameter)) {
                    params.put(parameter, arg);
                    parameter = null;
                } else {
                    throw new UnknownParameterException(parameter);
                }
            }
        }

        if(parameter!=null) {
            throw new UnknownParameterException(parameter);
        }

        if(params.isReliable()) {
            LOGGER.info("Paramètres traités avec succès.");
        }

        return params;
    }

    /**
     * Check validity of parameter
     * @param param
     * @return
     */
    private boolean checkParameter(String param) {
        return param.startsWith(PARAMETER_START) && param.length() > PARAMETER_START.length();
    }

    /**
     * Extract correct name of a parameter
     * @param param
     * @return
     */
    private String extractParameterName(String param) {
        return param.substring(PARAMETER_START.length());
    }

    /**
     * Load default configuration
     * @param params
     */
    private void loadDefault(Params params) {
        params.put("sonar.url", "");
        params.put("sonar.project.id", "");
        params.put("project.name", "default");
        params.put("report.author", "default");
        params.put("report.date", "default");
    }
}
