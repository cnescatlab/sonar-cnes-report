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
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.factory.ReportModelFactory;
import fr.cnes.sonar.report.factory.ServerFactory;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.ReportConfiguration;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private static final Logger LOGGER = Logger.getLogger(ReportFactory.class.getName());

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
            // Log message.
            String message;

            // Parse command line arguments.
            final ReportConfiguration conf = ReportConfiguration.create(args);

            // Set the language of the report.
            // assumes the language is set with language_country
            StringManager.changeLocale(conf.getLanguage());

            // Display version information and exit.
            if(conf.isVersion()) {
                final String name = ReportCommandLine.class.getPackage().getImplementationTitle();
                final String version = ReportCommandLine.class.getPackage().getImplementationVersion();
                final String vendor = ReportCommandLine.class.getPackage().getImplementationVendor();
                message = String.format("%s %s by %s", name, version, vendor);
                LOGGER.info(message);
                System.exit(0);
            }

            // Print information about SonarQube.
            message = String.format("SonarQube URL: %s", conf.getServer());
            LOGGER.info(message);

            // Initialize connexion with SonarQube and retrieve primitive information
            final SonarQubeServer server = new ServerFactory(conf.getServer(), conf.getToken()).create();

            message = String.format("SonarQube online: %s", server.isUp());
            LOGGER.info(message);

            if(!server.isUp()) {
                throw new SonarQubeException("Impossible to reach SonarQube instance.");
            }

            message = String.format("Detected SonarQube version: %s", server.getNormalizedVersion());
            LOGGER.info(message);

            if(!server.isSupported()) {
                throw new SonarQubeException("SonarQube instance is not supported by cnesreport.");
            }

            // Generate the model of the report.
            final Report model = new ReportModelFactory(server, conf).create();
            // Generate results files.
            ReportFactory.report(conf, model);

            message = "Report generation: SUCCESS";
            LOGGER.info(message);

        } catch (BadExportationDataTypeException | BadSonarQubeRequestException | IOException |
                UnknownQualityGateException | OpenXML4JException | XmlException | SonarQubeException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(-1);
        }
    }

}
