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

package fr.cnes.sonar.report;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.factory.ProviderFactory;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.factory.ReportModelFactory;
import fr.cnes.sonar.report.factory.ServerFactory;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.ReportConfiguration;
import fr.cnes.sonar.report.utils.StringManager;
import fr.cnes.sonar.report.factory.StandaloneProviderFactory;
import fr.cnes.sonar.report.factory.PluginProviderFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.sonarqube.ws.client.WsClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main entry point
 */
public final class ReportCommandLine {

    // Static initialization to set logging configuration.
    static {
        // Configure logging system
        try (InputStream fis = ReportCommandLine.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        // Configure temp files: creation of folder ~/.cnesreport/log to contain log files
        (new File(org.apache.commons.io.FileUtils.getUserDirectory().getPath().concat("/.cnesreport/log"))).mkdirs();
    }

    /** Logger of this class */
    private static final Logger LOGGER = Logger.getLogger(ReportCommandLine.class.getName());

    /**
     * Private constructor to not be able to instantiate it.
     */
    private ReportCommandLine(){}

    /**
     * Main method.
     * See help message for more information about using this program.
     * Entry point of the program.
     * @param args Arguments that will be preprocessed.
     */
    public static void main(final String[] args)  {
        // main catches all exceptions
        try {
            // We use different method because it can be called outside main (for example, in from ReportSonarPlugin)
            execute(args, null);

        } catch (BadExportationDataTypeException | BadSonarQubeRequestException | IOException |
                UnknownQualityGateException | OpenXML4JException | XmlException | SonarQubeException |
                IllegalStateException | IllegalArgumentException | ParseException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(-1);
        }
    }

    public static void execute(final String[] args, final WsClient wsClient) throws BadExportationDataTypeException , BadSonarQubeRequestException , IOException,
    UnknownQualityGateException, OpenXML4JException, XmlException, SonarQubeException, ParseException {
        // Log message.
        String message;

        // Parse command line arguments.
        final ReportConfiguration conf = ReportConfiguration.create(args);
        if(conf.getProject().isEmpty()){
            throw new IllegalStateException("Please provide a project with the -p argument, you can also use -h argument to display help.");
        }

        // Set the language of the report.
        // assumes the language is set with language_country
        StringManager.changeLocale(conf.getLanguage());

        // format server URL
        String url = conf.getServer();
        if(url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // Print information about SonarQube.
        message = String.format("SonarQube URL: %s", url);
        LOGGER.info(message);

        // Instantiate a ProviderFactory depending on the execution mode of the application
        ProviderFactory providerFactory;
        if (wsClient == null) {
            providerFactory = new StandaloneProviderFactory(url, conf.getToken(), conf.getProject(), conf.getBranch());
        } else {
            providerFactory = new PluginProviderFactory(conf.getProject(), conf.getBranch(), wsClient);
        }

        // Initialize connexion with SonarQube and retrieve primitive information
        final SonarQubeServer server = new ServerFactory(url, providerFactory).create();

        message = String.format("SonarQube online: %s", server.isUp());
        LOGGER.info(message);

        if(!server.isUp()) {
            throw new SonarQubeException("Impossible to reach SonarQube instance.");
        }

        message = String.format("Detected SonarQube version: %s", server.getVersion());
        LOGGER.info(message);

        if(!server.isSupported()) {
            LOGGER.warning("This SonarQube version is not supported by this cnesreport version.");
            LOGGER.warning("For further information, please refer to the compatibility matrix on the project GitHub page.");
        }

        // Generate the model of the report.
        final Report model = new ReportModelFactory(conf.getProject(), conf.getBranch(), conf.getAuthor(), conf.getDate(), providerFactory).create();
        // Generate results files.
        ReportFactory.report(conf, model);

        message = "Report generation: SUCCESS";
        LOGGER.info(message);
    }

}
