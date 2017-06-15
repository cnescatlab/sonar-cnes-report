package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exports the report in .json format
 * @author begarco
 */
public class JsonExporter implements IExporter {
    /**
     * Overridden export for json
     * @param data Data to export as String
     * @param params Program's parameters
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException data is not a String
     * @throws UnknownParameterException When report path is not set
     * @throws IOException ...
     */
    @Override
    public void export(Object data, Params params, String filename)
            throws BadExportationDataTypeException, UnknownParameterException, IOException {

        // check if the data format is correct
        if(!(data instanceof String)) {
            throw new BadExportationDataTypeException();
        }
        // data casting
        String string = (String) data;

        // set relevant variables
        String filePath = params.get("report.path") + "/" + filename + ".json";

        // writer used to write the file
        FileWriter fileWriter = null;

        // preventing leaks
        try {
            File jsonFile = new File(filePath);
            fileWriter = new FileWriter(jsonFile, false); // true to append
            // false to overwrite.
            fileWriter.write(string);
        } finally {
            // close the writer if it is still open
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
}
