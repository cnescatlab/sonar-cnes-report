package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.params.ParamsFactory;

import java.util.List;
import java.util.logging.Logger;

/**
 * Exports the report in the console
 * @author begarco
 */
public class ConsoleExporter implements IExporter {

    private static final Logger LOGGER = Logger.getLogger(ParamsFactory.class.getCanonicalName());

    @Override
    public void export(Object data, Params params, String filename) throws BadExportationDataTypeException {
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        Report report = (Report) data;


        List<Issue> issues = report.getIssues();
        LOGGER.info("key\t" + "project\t" + "component\t" + "type\t" + "severity\t" +
                "message\t" + "line\t" + "status\t" + "\t");
        for(Issue issue : issues)
            LOGGER.info(issue.toString());
        LOGGER.info("Nombre total de violations : " + String.valueOf(issues.size()));
    }
}
