package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.model.Report;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static fr.cnes.sonar.report.exporters.xlsx.XlsXTools.addListOfMap;
import static fr.cnes.sonar.report.exporters.xlsx.XlsXTools.addSelectedData;

/**
 * Exports the report in .docx format
 * @author begarco
 */
public class XlsXExporter implements IExporter {

    /**
     * Name of the tab containing formatted issues
     */
    private static final String ISSUES_SHEET_NAME = "Issues";
    /**
     *  Name of the tab containing all detailed issues
     */
    private static final String ALL_DETAILS_SHEET_NAME = "All";
    /**
     *  Name of the property for the path of the issues template
     */
    private static final String ISSUES_TEMPLATE = "issues.template";
    /**
     *  Name of the property for the path of the report output folder
     */
    private static final String REPORT_PATH = "report.path";
    /**
     * Name for the table containing selected data
     */
    private static final String SELECTED_TABLE_NAME = "selected";
    /**
     * Name for the table containing all raw data
     */
    private static final String ALL_TABLE_NAME = "all";

    /**
     * Overridden export for XlsX
     * @param data Data to export as Report
     * @param params Program's parameters
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException ...
     * @throws UnknownParameterException report.path is not set
     * @throws IOException when reading a file
     */
    @Override
    public void export(Object data, Params params, String filename)
            throws BadExportationDataTypeException, IOException, UnknownParameterException {
        // check data type
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // data casting
        Report report = (Report) data;

        // set output filename
        String outputFilePath = params.get(REPORT_PATH)+"/"+filename;

        // open the template
        FileInputStream excelFile = new FileInputStream(new java.io.File(params.get(ISSUES_TEMPLATE)));
        Workbook workbook = new XSSFWorkbook(excelFile);

        // retrieve the sheet aiming to contain selected data
        XSSFSheet selectedSheet = (XSSFSheet) workbook.getSheet(ISSUES_SHEET_NAME);

        // retrieve the sheet aiming to contain selected data
        XSSFSheet allDataSheet = (XSSFSheet) workbook.getSheet(ALL_DETAILS_SHEET_NAME);

        // write selected data in the file
        addSelectedData(report, selectedSheet, SELECTED_TABLE_NAME);

        // write all raw data in the third sheet
        addListOfMap(allDataSheet, report.getRawIssues(), ALL_TABLE_NAME);

        // write output as file
        FileOutputStream fileOut = new FileOutputStream(outputFilePath);
        workbook.write(fileOut);
        // close the file
        fileOut.close();
        excelFile.close();
    }

}
