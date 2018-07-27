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

package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;

/**
 * Generic interface for results' exporters
 */
@FunctionalInterface
public interface IExporter {
    /**
     * Export resources in a file
     * @param data Data to export
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @return Generated file.
     * @throws BadExportationDataTypeException resources has not the good type
     * @throws IOException ...
     * @throws OpenXML4JException When dealing with low level xml
     * @throws XmlException When dealing with low level xml
     */
    File export(final Object data, final String path, final String filename)
            throws BadExportationDataTypeException, IOException, OpenXML4JException, XmlException;
}
