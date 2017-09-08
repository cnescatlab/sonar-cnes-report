package fr.cnes.sonar.report.input;

import fr.cnes.sonar.report.exceptions.MalformedParameterException;
import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static fr.cnes.sonar.report.input.StringManager.*;

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
                if(!params.contains(parameter)) {
                    // if it does not exist throw exception
                    throw new UnknownParameterException(parameter);
                } else if(!arg.isEmpty()) {
                    // check if the parameter is not empty
                    params.put(parameter, arg);
                }
                // the current parameter's processing is terminated
                parameter = null;
            }
        }

        // check if we were handling a parameter when exiting the for
        if(parameter!=null) {
            throw new UnknownParameterException(parameter);
        }

        // if all parameters are ok log success
        if(params.isReliable()) {
            LOGGER.info(StringManager.SUCCESSFULLY_PROCESSED_PARAMETERS);
            // and we set locale
            String locale[] = params.get(REPORT_LOCALE).split("_");
            if(locale.length==2) {
                changeLocale(locale[0], locale[1]);
            }
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
        // contain raw resources to process
        List<String> raw = new ArrayList<>();

        // fill out raw
        raw.addAll(Arrays.asList(args));

        // construction of new input
        Iterator<String> it = raw.iterator();
        StringBuilder param;
        while(it.hasNext()) {

            // construction of blank separated input
            param = new StringBuilder(it.next());
            if(param.toString().startsWith(QUOTES) && !(param.toString().endsWith(QUOTES))) {
                while (it.hasNext() && !(param.toString().endsWith(QUOTES))) {
                    param.append(SPACE).append(it.next());
                }
            }

            // add the param
            checked.add(param.toString().replaceAll(QUOTES,EMPTY));
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
        return param.startsWith(StringManager.PARAMETER_START) && param.length() > StringManager.PARAMETER_START.length();
    }

    /**
     * Extract correct name of a parameter
     * @param param name to check
     * @return the correct name
     */
    private String extractParameterName(String param) {
        return param.substring(StringManager.PARAMETER_START.length());
    }

    /**
     * Load default configuration
     * @param params parameters to set
     */
    private void loadDefault(Params params) {
        // append all default configuration
        params.put(StringManager.SONAR_URL, StringManager.EMPTY);
        params.put(StringManager.SONAR_PROJECT_ID, StringManager.EMPTY);
        params.put(StringManager.REPORT_AUTHOR, getProperty(StringManager.REPORT_AUTHOR));
        params.put(StringManager.REPORT_DATE, new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()));
        params.put(StringManager.REPORT_CONF, getProperty(StringManager.REPORT_CONF));
        params.put(StringManager.REPORT_PATH, getProperty(StringManager.REPORT_PATH));
        params.put(StringManager.REPORT_LOCALE, getProperty(StringManager.REPORT_LOCALE));
        params.put(StringManager.REPORT_TEMPLATE, getProperty(StringManager.REPORT_TEMPLATE));
        params.put(StringManager.ISSUES_TEMPLATE, getProperty(StringManager.ISSUES_TEMPLATE));
    }
}
