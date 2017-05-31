package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.params.Params;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exports the report in .xml format
 * @author begarco
 */
public class XmlExporter implements IExporter {
    /**
     * Overridden export for xml
     * @param data Data to export as String
     * @param params Program's parameters
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException ...
     * @throws UnknownParameterException report.path is not set
     * @throws IOException ...
     */
    @Override
    public void export(Object data, Params params, String filename) throws BadExportationDataTypeException, UnknownParameterException, IOException {

        // check if the data format is correct
        if(!(data instanceof String)) {
            throw new BadExportationDataTypeException();
        }
        // data casting
        String string = (String) data;

        // set relevant variables
        String filePath = params.get("report.path") + "/" + filename + ".xml";

        // writer used
        FileWriter fileWriter = null;

        // prevent file's allocation leaks
        try {
            File xmlFile = new File(filePath);
            fileWriter = new FileWriter(xmlFile, false); // true to append
            // false to overwrite.
            fileWriter.write(string);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }

    }
}
