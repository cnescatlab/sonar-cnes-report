package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.params.Params;

/**
 * Generic interface for results' exporters
 * @author begarco
 */
public interface IExporter {
    void export(Object data, Params params, String filename) throws Exception;
}
