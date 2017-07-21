package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;

/**
 * Generic interface for results' exporters
 * @author begarco
 */
@FunctionalInterface
public interface IExporter {
    /**
     * Export resources in a file
     * @param data Data to export
     * @param params Program's parameters
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException resources has not the good type
     * @throws UnknownParameterException report.path is not set
     * @throws IOException ...
     * @throws OpenXML4JException When dealing with low level xml
     * @throws XmlException When dealing with low level xml
     */
    void export(Object data, Params params, String path, String filename)
            throws BadExportationDataTypeException, UnknownParameterException,
            IOException, OpenXML4JException, XmlException;
}
