package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.SecurityHotspot;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.stream.*;

/**
 * Exports the report in .docx format
 */
public class XlsXExporter implements IExporter {

    /** Logger for XlsXExporter. */
    private static final Logger LOGGER = Logger.getLogger(StringManager.class.getCanonicalName());
    /**
     * Name of the tab containing formatted issues
     */
    private static final String ISSUES_SHEET_NAME = "Issues";
    /**
     * Name of the tab containing unconfirmed issues
     */
    private static final String UNCONFIRMED_SHEET_NAME = "Unconfirmed";
    /**
     *  Name of the tab containing all detailed issues
     */
    private static final String ALL_DETAILS_SHEET_NAME = "All";
    /**
     * Name for the table containing selected resources
     */
    private static final String SELECTED_TABLE_NAME = "selected";
    /**
     * Name for the table containing unconfirmed resources
     */
    private static final String UNCONFIRMED_TABLE_NAME = "unconfirmed";
    /**
     * Name for the table containing all raw resources
     */
    private static final String ALL_TABLE_NAME = "all";
    /**
     * Name for the tab containing formatted security hotspots
     */
    private static final String SECURITY_HOTSPOTS_SHEET_NAME = "Security Hotspots";
    /**
     * Name for the table containing security hotspots
     */
    private static final String SECURITY_HOTSPOTS_TABLE_NAME = "hotspots";
    /**
     * Name for the tab containing metrics
     */
    private static final String METRICS_SHEET_NAME = "Metrics";
    /**
     * Name for the table containing metrics
     */
    private static final String METRICS_TABLE_NAME = "metrics";
    /**
     * Name of the property giving the default xlsx template
     */
    private static final String DEFAULT_TEMPLATE = "xlsx.template";

    /**
     * Overridden export for XlsX
     * @param data Data to export as Report
     * @param path Path where to export the file
     * @param filename Name of the template file
     * @return Generated file.
     * @throws BadExportationDataTypeException ...
     * @throws IOException when reading a file
     */
    @Override
    public File export(Object data, String path, String filename)
            throws BadExportationDataTypeException, IOException {
        // check resources type
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // resources casting
        final Report report = (Report) data;

        // set output filename
        final String outputFilePath = path;

        // open excel file from the path given in the parameters
        final File file = new File(filename);

        // Check if template file exists
        if(!file.exists() && !filename.isEmpty()) {
            LOGGER.log(Level.WARNING, () -> "Unable to find provided XLSX template file (using default one instead) : " + file.getAbsolutePath());
        }

        // open the template
        try(
            InputStream excelFile = file.exists() ?
                    new FileInputStream(file) : getClass().getResourceAsStream(StringManager.getProperty(DEFAULT_TEMPLATE));
            Workbook workbook = new XSSFWorkbook(excelFile);
            FileOutputStream fileOut = new FileOutputStream(outputFilePath)
        ) {

            // retrieve the sheet aiming to contain selected resources
            final XSSFSheet selectedSheet = (XSSFSheet) workbook.getSheet(ISSUES_SHEET_NAME);

            // retrieve the sheet aiming to contain selected resources
            final XSSFSheet unconfirmedSheet = (XSSFSheet) workbook.getSheet(UNCONFIRMED_SHEET_NAME);

            // retrieve the sheet aiming to contain selected resources
            final XSSFSheet allDataSheet = (XSSFSheet) workbook.getSheet(ALL_DETAILS_SHEET_NAME);

            // retrieve the sheet aiming to contain selected resources
            final XSSFSheet securityHotspotsSheet = (XSSFSheet) workbook.getSheet(SECURITY_HOTSPOTS_SHEET_NAME);

            // retrieve the sheet with metrics
            final XSSFSheet metricsSheet = (XSSFSheet) workbook.getSheet(METRICS_SHEET_NAME);

            // write selected resources in the file
            XlsXTools.addSelectedData(report.getIssues().getIssuesList(), selectedSheet, SELECTED_TABLE_NAME);

            // write selected resources in the file
            XlsXTools.addSelectedData(report.getUnconfirmed(), unconfirmedSheet, UNCONFIRMED_TABLE_NAME);

            // write all raw resources in the third sheet
            XlsXTools.addListOfMap(allDataSheet, report.getRawIssues(), ALL_TABLE_NAME);

            // write all security hotspots in the security hotspots sheet
            List<SecurityHotspot> allSecurityHotspots = Stream.concat(report.getToReviewSecurityHotspots().stream(),
                    report.getReviewedSecurityHotspots().stream()).collect(Collectors.toList());
            XlsXTools.addSecurityHotspots(allSecurityHotspots, securityHotspotsSheet, SECURITY_HOTSPOTS_TABLE_NAME);

            // write all metrics in the metric sheet
            XlsXTools.addListOfMap(metricsSheet, report.getComponents(), METRICS_TABLE_NAME);

            // write output as file
            workbook.write(fileOut);
        }

        return new File(outputFilePath);
    }

}
