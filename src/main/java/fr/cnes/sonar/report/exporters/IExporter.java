package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.xlsx4j.exceptions.Xlsx4jException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Generic interface for results' exporters
 * @author begarco
 */
public interface IExporter {
    /**
     * Export data in a file
     * @param data Data to export
     * @param params Program's parameters
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException data has not the good type
     * @throws UnknownParameterException report.path is not set
     * @throws IOException ...
     * @throws Docx4JException when an error occurred in docx4j
     * @throws JAXBException when there is a problem with a jaxb element
     */
    void export(Object data, Params params, String filename)
            throws BadExportationDataTypeException, UnknownParameterException,
            IOException, Docx4JException, JAXBException, Xlsx4jException;
}
