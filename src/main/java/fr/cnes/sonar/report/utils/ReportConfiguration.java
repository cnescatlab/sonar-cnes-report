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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains all configuration about the report to generate.
 */
public class ReportConfiguration {

    /** Options for h. */
    private boolean help;
    /** Options for v. */
    private boolean version;
    /** Options for s. */
    private String server;
    /** Options for t. */
    private String token;
    /** Options for p. */
    private String project;
    /** Options for b. */
    private String branch;
    /** Options for o. */
    private String output;
    /** Options for l. */
    private String language;
    /** Options for a. */
    private String author;
    /** Options for d. */
    private String date;
    /** Options for c. */
    private boolean enableConf;
    /** Options for w. */
    private boolean enableReport;
    /** Options for e. */
    private boolean enableSpreadsheet;
    /** Options for f. */
    private boolean enableCSV;
    /** Options for m. */
    private boolean enableMarkdown;
    /** Options for r. */
    private String templateReport;
    /** Options for x. */
    private String templateSpreadsheet;
    /** Options for n. */
    private String templateMarkdown;

    /**
     * Private constructor, use create method instead.
     * @param help Value for h option.
     * @param version Value for v option.
     * @param server Value for s option.
     * @param token Value for t option.
     * @param project Value for p option.
     * @param output Value for o option.
     * @param language Value for l option.
     * @param author Value for a option.
     * @param date Value for d option.
     * @param enableConf Value for c option.
     * @param enableReport Value for w option.
     * @param enableSpreadsheet Value for e option.
     * @param templateReport Value for r option.
     * @param templateSpreadsheet Value for x option.
     * @param branch Value for b option.
     */
    private ReportConfiguration(final boolean help, final boolean version, final String server,
                                final String token, final String project, final String output,
                                final String language, final String author, final String date,
                                final boolean enableConf, final boolean enableReport,
                                final boolean enableSpreadsheet, final boolean enableCSV,
                                final boolean enableMarkdown, String templateReport,
                                final String templateSpreadsheet, final String templateMarkdown, final String branch) {
        this.help = help;
        this.version = version;
        this.server = server;
        this.token = token;
        this.project = project;
        this.output = output;
        this.language = language;
        this.author = author;
        this.date = date;
        this.enableConf = enableConf;
        this.enableReport = enableReport;
        this.enableSpreadsheet = enableSpreadsheet;
        this.enableCSV = enableCSV;
        this.enableMarkdown = enableMarkdown;
        this.templateReport = templateReport;
        this.templateSpreadsheet = templateSpreadsheet;
        this.templateMarkdown = templateMarkdown;
        this.branch = branch;
    }

    /**
     * Create a configuration object from raw string array.
     *
     * @param pArgs Raw java string array.
     * @return Configuration object.
     */
    public static ReportConfiguration create(final String[] pArgs) {

        // Parse arguments.
        final CommandLineManager commandLineManager = new CommandLineManager();
        commandLineManager.parse(pArgs);

        // Final result to return.
        final String branch = commandLineManager.getOptionValue("b", StringManager.NO_BRANCH);
        return new ReportConfiguration(
                commandLineManager.hasOption("h"),
                commandLineManager.hasOption("v"),
                commandLineManager.getOptionValue("s", StringManager.getProperty(StringManager.SONAR_URL)),
                commandLineManager.getOptionValue("t", StringManager.getProperty(StringManager.SONAR_TOKEN)),
                commandLineManager.getOptionValue("p", StringManager.EMPTY),
                commandLineManager.getOptionValue("o", StringManager.getProperty(StringManager.DEFAULT_OUTPUT)),
                commandLineManager.getOptionValue("l", StringManager.getProperty(StringManager.DEFAULT_LANGUAGE)),
                commandLineManager.getOptionValue("a", StringManager.getProperty(StringManager.DEFAULT_AUTHOR)),
                commandLineManager.getOptionValue("d", new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date())),
                !commandLineManager.hasOption("c"),
                !commandLineManager.hasOption("w"),
                !commandLineManager.hasOption("e"),
                !commandLineManager.hasOption("f"), // Why f? Because every "logic" options like "c" are already used
                !commandLineManager.hasOption("m"),
                commandLineManager.getOptionValue("r", StringManager.EMPTY),
                commandLineManager.getOptionValue("x", StringManager.EMPTY),
                commandLineManager.getOptionValue("n", StringManager.EMPTY),
                branch.isEmpty()?StringManager.NO_BRANCH:branch
        );
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isVersion() {
        return version;
    }

    public String getServer() {
        return server;
    }

    public String getToken() {
        return token;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getOutput() {
        return output;
    }

    public String getLanguage() {
        return language;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public boolean isEnableConf() {
        return enableConf;
    }

    public boolean isEnableCSV(){ return enableCSV; }

    public boolean isEnableMarkdown(){ return enableMarkdown; }

    public boolean isEnableReport() {
        return enableReport;
    }

    public boolean isEnableSpreadsheet() {
        return enableSpreadsheet;
    }

    public String getTemplateReport() {
        return templateReport;
    }

    public String getTemplateSpreadsheet() {
        return templateSpreadsheet;
    }

    public String getTemplateMarkdown(){
        return templateMarkdown;
    }
}
