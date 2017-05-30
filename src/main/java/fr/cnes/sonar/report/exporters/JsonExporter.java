package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.params.Params;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exports the report in .json format
 * @author begarco
 */
public class JsonExporter implements IExporter {
    @Override
    public void export(Object data, Params params, String filename) throws BadExportationDataTypeException, UnknownParameterException, IOException {

        // check if the data format is correct
        if(!(data instanceof String)) {
            throw new BadExportationDataTypeException();
        }
        String string = (String) data;

        // set relevant variables
        String filePath = params.get("report.path") + "/" + filename + ".json";

        File jsonFile = new File(filePath);
        FileWriter fileWriter = new FileWriter(jsonFile, false); // true to append
        // false to overwrite.
        fileWriter.write(string);
        fileWriter.close();
    }
}
