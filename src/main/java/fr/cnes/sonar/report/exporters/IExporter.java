package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.params.Params;

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
     * @throws Exception all exceptions
     */
    void export(Object data, Params params, String filename) throws Exception;
}
