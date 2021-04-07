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

import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

/**
 * Manage the command line by parsing it and providing preprocessed data.
 */
public class CommandLineManager {

    /** Default logger. */
    private static final Logger LOGGER = Logger.getLogger(CommandLineManager.class.getName());

    /** Possible options definition. */
    private Options options;
    /** Parser used by the manager. */
    private CommandLineParser parser;
    /** Formatter for automatic help. */
    private HelpFormatter helpFormatter;
    /** Contain the formatted cli. */
    private CommandLine commandLine;

    /** Option details for help. */
    private static final String[][] OPTIONS_DEFINITION = {
            {"h", "help", Boolean.FALSE.toString(), "Display this message."},
            {"v", "version", Boolean.FALSE.toString(), "Display current version."},
            {"s", "server", Boolean.TRUE.toString(), "Complete URL of the targeted SonarQube server."},
            {"t", "token", Boolean.TRUE.toString(), "SonarQube token of the SonarQube user who has permissions on the project."},
            {"p", "project", Boolean.TRUE.toString(), "SonarQube key of the targeted project."},
            {"b", "branch", Boolean.TRUE.toString(), "Branch of the targeted project. Requires Developer Edition or sonarqube-community-branch-plugin. Default: usage of main branch."},
            {"o", "output", Boolean.TRUE.toString(), "Output path for exported resources."},
            {"l", "language", Boolean.TRUE.toString(), "Language of the report. Values: en_US, fr_FR. Default: en_US."},
            {"a", "author", Boolean.TRUE.toString(), "Name of the report writer."},
            {"d", "date", Boolean.TRUE.toString(), "Date for the report. Format: " + StringManager.DATE_PATTERN + ". Default: current date."},
            {"c", "disable-conf", Boolean.FALSE.toString(), "Disable export of quality configuration used during analysis."},
            {"w", "disable-report", Boolean.FALSE.toString(), "Disable report generation."},
            {"e", "disable-spreadsheet", Boolean.FALSE.toString(), "Disable spreadsheet generation."},
            {"f", "disable-csv", Boolean.FALSE.toString(), "Disable CSV generation"},
            {"m", "disable-markdown", Boolean.FALSE.toString(), "Disable Markdown generation"},
            {"n", "template-markdown", Boolean.TRUE.toString(), "Path to the report template in markdown. Default: usage of internal template."},
            {"r", "template-report", Boolean.TRUE.toString(), "Path to the report template. Default: usage of internal template."},
            {"x", "template-spreadsheet", Boolean.TRUE.toString(), "Path to the spreadsheet template. Default: usage of internal template."}
    };

    /**
     * Default construct which initialize and set options.
     */
    public CommandLineManager() {
        configure();
    }

    /**
     * Set all members and prepare list of possible options
     */
    private void configure() {
        // Initialize values for members.
        options = new Options();
        parser = new DefaultParser();
        helpFormatter = new HelpFormatter();
        commandLine = null;

        // Add options
        for(final String[] option : OPTIONS_DEFINITION) {
            options.addOption(option[0], option[1], Boolean.valueOf(option[2]), option[3]);
        }

    }

    /**
     * Parse the provided command line.
     *
     * @param pArgs Arguments to parse.
     */
    public void parse(final String[] pArgs) {

        // Contains true if options are reliable
        boolean areOptionsCorrect = false;

        try {
            // Parse the command line.
            commandLine = parser.parse(options, pArgs);
            areOptionsCorrect = checkOptionsUse(commandLine);
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
	    areOptionsCorrect = false;
        }

        // If help option is present we print it.
        if (!areOptionsCorrect) {
            printHelp();
            throw new IllegalArgumentException("Illegal command line arguments");
        } else if (commandLine.hasOption("h")) {
            printHelp();
            System.exit(0);
        } else if (commandLine.hasOption("v")) {
            // Display version information and exit.
            try(InputStream input = this.getClass().getClassLoader().getResourceAsStream("version.properties")) {
                if(input!=null) {
                    final Properties properties = new Properties();
                    properties.load(input);
                    String version = properties.getProperty("version");
                    String message = String.format("Current version: %s", version);                    
                    LOGGER.info(message);
                    System.exit(0);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void printHelp() {
        helpFormatter.printHelp(128, "java -jar cnesreport.jar",
                "Generate editable reports for SonarQube projects.\n\n", options,
                "\n\nPlease report issues at https://github.com/cnescatlab/sonar-cnes-report/issues", true);
    }

    /**
     * Check options compatibility:
     * + Options HELP cannot be mixed with other options.
     *
     * @param commandLine Parsed command line.
     * @return True if options respect requirements.
     */
    private boolean checkOptionsUse(final CommandLine commandLine) {
        // number of options which should be called alone
        int standaloneOptions = 0;
        // number of options without restriction
        int analysisOptions = 0;

        for (final Option option : commandLine.getOptions()) {
            if (option.getOpt().equals("h") || option.getOpt().equals("v")) {
                standaloneOptions++;
            } else {
                analysisOptions++;
            }
        }

        return (analysisOptions == 0 || standaloneOptions == 0) && standaloneOptions < 2;
    }

    /**
     * Provides arguments as a list.
     *
     * @return A List<String> with args.
     */
    public List<String> getArgs() {
        List<String> result;

        if (null != commandLine) {
            result = commandLine.getArgList();
        } else {
            result = new ArrayList<>();
        }

        return result;
    }

    /**
     * Determine if an option is contained in the cli.
     *
     * @param pOption Name of the option to retrieve.
     * @return True if the cli contains the option.
     */
    public boolean hasOption(final String pOption) {
        return commandLine != null && commandLine.hasOption(pOption);
    }

    /**
     * Return the value of the corresponding option.
     *
     * @param pOption Name of the option.
     * @return A string containing the value or an empty string.
     */
    public String getOptionValue(final String pOption) {
        return this.getOptionValue(pOption, "");
    }

    /**
     * Return the value of the corresponding option.
     *
     * @param pOption Name of the option.
     * @param pDefault Default value of the option.
     * @return A string containing the value or a default string.
     */
    public String getOptionValue(final String pOption, final String pDefault) {
        String result = pDefault;

        if (null != commandLine) {
            result = commandLine.getOptionValue(pOption, pDefault);
        }

        return result;
    }
}
