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

package fr.cnes.sonar.plugin.settings;

import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.Arrays;
import java.util.List;

/**
 * Define all SonarQube properties provided by this plugin.
 */
public class ReportSonarPluginProperties {

    /**
	 * Prefix used by all properties of this plugin.
	 **/
	private static final String PROPERTIES_PREFIX = "sonar.cnesreport.";

    /**
     * CNES report name.
     **/
    public static final String CNES_REPORT_NAME = "CNES report";

	/**
	 * Suffix used in the description of every path property
	 */
	public static final String PATH_DESC_SUFFIX = "This must be an absolute path in the machine on which the SonarQube server is running. "
			+ "The template must have at least read access by the user used to start the SonarQube server.";

    /**
	 * Key for the docx template path property
	 **/
	public static final String DOCX_PATH_KEY = PROPERTIES_PREFIX + "docx.path";
    /**
	 * Name for the docx template path property
	 **/
	public static final String DOCX_PATH_NAME = "DOCX template path";
    /**
	 * Description for the docx template path property
	 **/
	public static final String DOCX_PATH_DESC = "Path to the report template. " + PATH_DESC_SUFFIX;

    /**
	 * Key for the md template path property
	 **/
	public static final String MD_PATH_KEY = PROPERTIES_PREFIX + "md.path";
    /**
	 * Name for the md template path property
	 **/
	public static final String MD_PATH_NAME = "MD template path";
    /**
	 * Description for the md template path property
	 **/
	public static final String MD_PATH_DESC = "Path to the markdown template. " + PATH_DESC_SUFFIX;

    /**
	 * Key for the xlsx template path property
	 **/
	public static final String XLSX_PATH_KEY = PROPERTIES_PREFIX + "xlsx.path";
    /**
	 * Name for the xlsx template path property
	 **/
	public static final String XLSX_PATH_NAME = "XLSX template path";
    /**
	 * Description for the xlsx template path property
	 **/
	public static final String XLSX_PATH_DESC = "Path to the spreadsheet template. " + PATH_DESC_SUFFIX;

    /**
	 * Private constructor because it is a utility class.
	 */
    private ReportSonarPluginProperties() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Plugin properties extensions.
     *
     * @return The list of built properties.
     */
    public static List<PropertyDefinition> getProperties() {
        return Arrays.asList(
            PropertyDefinition.builder(DOCX_PATH_KEY)
					.category(CNES_REPORT_NAME)
					.name(DOCX_PATH_NAME)
					.description(DOCX_PATH_DESC)
					.onQualifiers(Qualifiers.APP)
					.build()
            ,
            PropertyDefinition.builder(MD_PATH_KEY)
					.category(CNES_REPORT_NAME)
					.name(MD_PATH_NAME)
					.description(MD_PATH_DESC)
					.onQualifiers(Qualifiers.APP)
					.build()
            ,
            PropertyDefinition.builder(XLSX_PATH_KEY)
					.category(CNES_REPORT_NAME)
					.name(XLSX_PATH_NAME)
					.description(XLSX_PATH_DESC)
					.onQualifiers(Qualifiers.APP)
					.build());
    }
}