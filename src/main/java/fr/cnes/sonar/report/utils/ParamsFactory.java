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

package fr.cnes.sonar.report.utils;

import fr.cnes.sonar.report.exceptions.MalformedParameterException;
import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Prepares command line's arguments
 * @author lequal
 */
public class ParamsFactory {

    /**
     * Generates a full parameters object from user cli
     * @param args arguments to process
     * @return handled parameters
     * @throws UnknownParameterException A parameter is not known
     * @throws MalformedParameterException A parameter is not correct
     * @throws MissingParameterException A parameter is missing
     */
    public Params create(String[] args)
            throws UnknownParameterException, MalformedParameterException,
            MissingParameterException {
        // output utils
        final Params params = new Params();

        // currently handled parameter
        String parameter = null;

        // load default value for parameters
        loadDefault(params);

        // check reliability of arguments (blanks)
        final List<String> preProcessedArgs = checkBlank(args);

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

        // if all parameters are ok
        if(params.isReliable()) {
            // we set locale
            final String[] locale = params.get(StringManager.REPORT_LOCALE).split("_");
            if(locale.length==2) {
                StringManager.changeLocale(locale[0], locale[1]);
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
        // contain the final result with correct utils
        final List<String> checked = new ArrayList<>();
        // contain raw resources to process
        final List<String> raw = new ArrayList<>();

        // fill out raw
        raw.addAll(Arrays.asList(args));

        // construction of new utils
        final Iterator<String> it = raw.iterator();
        StringBuilder param;
        while(it.hasNext()) {

            // construction of blank separated utils
            param = new StringBuilder(it.next());
            if(param.toString().startsWith(StringManager.QUOTES)
                    && !(param.toString().endsWith(StringManager.QUOTES))) {
                while (it.hasNext()
                        && !(param.toString().endsWith(StringManager.QUOTES))) {
                    param.append(StringManager.SPACE).append(it.next());
                }
            }

            // add the param
            checked.add(param.toString().replaceAll(StringManager.QUOTES,StringManager.EMPTY));
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
        return param.startsWith(StringManager.PARAMETER_START)
                && param.length() > StringManager.PARAMETER_START.length();
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
        params.put(
                StringManager.SONAR_URL,
                StringManager.EMPTY);
        params.put(
                StringManager.SONAR_TOKEN,
                StringManager.getProperty(StringManager.SONAR_TOKEN));
        params.put(
                StringManager.SONAR_PROJECT_ID,
                StringManager.EMPTY);
        params.put(
                StringManager.REPORT_AUTHOR,
                StringManager.getProperty(StringManager.REPORT_AUTHOR));
        params.put(
                StringManager.REPORT_DATE,
                new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()));
        params.put(
                StringManager.REPORT_CONF,
                StringManager.getProperty(StringManager.REPORT_CONF));
        params.put(
                StringManager.REPORT_PATH,
                StringManager.getProperty(StringManager.REPORT_PATH));
        params.put(
                StringManager.REPORT_LOCALE,
                StringManager.getProperty(StringManager.REPORT_LOCALE));
        params.put(
                StringManager.REPORT_TEMPLATE,
                StringManager.getProperty(StringManager.REPORT_TEMPLATE));
        params.put(
                StringManager.ISSUES_TEMPLATE,
                StringManager.getProperty(StringManager.ISSUES_TEMPLATE));
    }
}
