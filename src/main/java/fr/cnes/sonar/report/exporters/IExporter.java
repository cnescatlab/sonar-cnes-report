package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import org.docx4j.openpackaging.exceptions.Docx4JException;

/**
 * Generic interface for results' exporters
 * @author begarco
 */
public interface IExporter {
    void export(Object data, Params params, String filename) throws Exception, BadExportationDataTypeException;
}
