package fr.cnes.sonar.report.input;

import fr.cnes.sonar.report.exceptions.MalformedParameterException;
import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prepares command line's arguments
 * @author begarco
 */
public class ParamsFactory {

    /**
     * Logger for ParamFactory
     */
    private static final Logger LOGGER = Logger.getLogger(ParamsFactory.class.getCanonicalName());
    /**
     * Prefix of all parameters
     */
    private static final String PARAMETER_START = "--";

    /**
     * Name for properties' file about report
     */
    private static final String REPORT_PROPERTIES = "report.properties";

    /**
     * Contain all the properties related to the report
     */
    private static Properties properties;

    /**
     * Static initialization block for reading .properties
     */
    static {
        // store properties
        properties = new Properties();
        // read the file
        InputStream input = null;

        try {
            // load properties file as a stream
            input = AbstractDataProvider.class.getClassLoader().getResourceAsStream(REPORT_PROPERTIES);
            if(input!=null) {
                // load properties from the stream in an adapted structure
                properties.load(input);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if(input!=null) {
                try {
                    // close the stream if necessary (not null)
                    input.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Give the value of the property corresponding to the key passed as parameter.
     * It gives only properties related to the report.
     * @param property Key of the property you want.
     * @return The value of the property you want as a String.
     */
    public static String getProperty(String property) {
        return properties.getProperty(property);
    }

    /**
     * Generates a full parameters object from user cli
     * @param args arguments to process
     * @return handled parameters
     * @throws UnknownParameterException A parameter is not known
     * @throws MalformedParameterException A parameter is not correct
     * @throws MissingParameterException A parameter is missing
     */
    public Params create(String[] args)
            throws UnknownParameterException, MalformedParameterException, MissingParameterException {
        // output input
        Params params = new Params();

        // currently handled parameter
        String parameter = null;

        // load default value for parameters
        loadDefault(params);

        // check reliability of arguments (blanks)
        List<String> preProcessedArgs = checkBlank(args);

        // handle each raw args
        for (String arg : preProcessedArgs) {
            // if there are no currently handled parameter
            if(parameter==null) {
                if(checkParameter(arg)) {
                    parameter = extractParameterName(arg);
                } else {
                    throw new MalformedParameterException(arg);
                }
                // continue handling current parameter
            } else {
                // add the value if the parameter exist
                if(params.contains(parameter)) {
                    // check if the parameter is not empty
                    if(!"".equals(arg)) {
                        params.put(parameter, arg);
                    }
                    // the current parameter's processing is terminated
                    parameter = null;
                } else {
                    // if it does not exist throw exception
                    throw new UnknownParameterException(parameter);
                }
            }
        }

        // check if we were handling a parameter when exiting the for
        if(parameter!=null) {
            throw new UnknownParameterException(parameter);
        }

        // if all parameters are ok log success
        if(params.isReliable()) {
            LOGGER.info("Paramètres traités avec succès.");
        }

        // return the final parameters
        return params;
    }

    /**
     * Handle blanks in parameters
     * @param args list of arguments
     * @return the new arg array
     */
    private List<String> checkBlank(String[] args) {
        // contain the final result with correct input
        List<String> checked = new ArrayList<>();
        // contain raw data to process
        List<String> raw = new ArrayList<>();

        // fill out raw
        raw.addAll(Arrays.asList(args));

        // construction of new input
        Iterator it = raw.iterator();
        while(it.hasNext()) {

            // construction of blank separated input
            StringBuilder param = new StringBuilder((String) it.next());
            if(param.toString().startsWith("\"") && !(param.toString().endsWith("\""))) {
                while (it.hasNext() && !(param.toString().endsWith("\""))) {
                    param.append(' ').append(it.next());
                }
            }

            // add the param
            checked.add(param.toString().replaceAll("\"",""));
        }

        return checked;
    }

    /**
     * Check validity of parameter
     * @param param parameter to check
     * @return true if param is correct
     */
    private boolean checkParameter(String param) {
        // check that the parameter begins with PARAMETER_START and is not empty
        return param.startsWith(PARAMETER_START) && param.length() > PARAMETER_START.length();
    }

    /**
     * Extract correct name of a parameter
     * @param param name to check
     * @return the correct name
     */
    private String extractParameterName(String param) {
        return param.substring(PARAMETER_START.length());
    }

    /**
     * Load default configuration
     * @param params parameters to set
     */
    private void loadDefault(Params params) {
        // append all default configuration
        params.put("sonar.url", "");
        params.put("sonar.project.id", "");
        params.put("sonar.project.quality.profile", getProperty("sonar.project.quality.profile"));
        params.put("sonar.project.quality.gate", getProperty("sonar.project.quality.gate"));
        params.put("project.name", getProperty("project.name"));
        params.put("report.author", getProperty("report.author"));
        params.put("report.date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        params.put("report.path", getProperty("report.path"));
        params.put("report.template", getProperty("report.template"));
        params.put("issues.template", getProperty("issues.template"));
    }
}
