package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;

/**
 * Exports the report in .docx format
 * @author begarco
 */
public class XlsXExporter implements IExporter {

    /**
     * Pattern for sheet's names
     */
    private static final String XL_WORKSHEETS_SHEET_S_XML = "/xl/worksheets/sheet%s.xml";
    /**
     * Name of the tab containing formatted issues
     */
    private static final String ISSUES = "Issues";
    /**
     * Name of the tab containing raw issues
     */
    private static final String RAW_ISSUES = "Raw issues";

    /**
     * Count the number of sheets
     */
    private int sheetsCount = 1;

    /**
     * Overridden export for XlsX
     * @param data Data to export as Report
     * @param params Program's parameters
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException ...
     * @throws UnknownParameterException report.path is not set
     * @throws Docx4JException when an error occurred in docx4j
     * @throws JAXBException when there is a problem with a jaxb element
     */
    @Override
    public void export(Object data, Params params, String filename)
            throws BadExportationDataTypeException, UnknownParameterException, Docx4JException, JAXBException {
        // check data type
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // data casting
        Report report = (Report) data;

        // reinitialize sheet counter
        this.sheetsCount = 1;

        // set output filename
        String outputFilePath = params.get("report.path")+"/"+filename;

        // create an xlsX document
        SpreadsheetMLPackage pkg = SpreadsheetMLPackage.createPackage();

        // create a worksheet
        WorksheetPart formattedSheet = createSheet(pkg, ISSUES);

        // create a worksheet
        WorksheetPart rawSheet = createSheet(pkg, RAW_ISSUES);

        // add content to the sheet
        addContent(formattedSheet, report);

        // add content to the sheet
        addListOfMap(rawSheet, report.getRawIssues());

        // save the file
        pkg.save(new File(outputFilePath));
    }

    /**
     * Create a new sheet in excel file
     * @param pkg represent the excel file
     * @param name name of the sheet to add
     * @return a sheet ready to be filled out
     */
    private WorksheetPart createSheet(SpreadsheetMLPackage pkg, String name)
            throws InvalidFormatException, JAXBException {
        // create a sheet in the file from a filename, a name and an id
        WorksheetPart result = pkg.createWorksheetPart(
                new PartName(String.format(XL_WORKSHEETS_SHEET_S_XML, this.sheetsCount)), name, sheetsCount);
        // increment page count
        sheetsCount++;
        // return a new sheet with incremented counter
        return result;
    }

    /**
     * Add a list of Map in an excel sheet
     * @param sheet sheet to fill out
     * @param list list of map to put
     */
    private void addListOfMap(WorksheetPart sheet, List<Map> list) {
        // Minimal content already present
        SheetData sheetData = sheet.getJaxbElement().getSheetData();

        // get the headers list
        List<String> headers = extractHeader(list);

        // create the headers' row and add it to the sheet
        sheetData.getRow().add(createRow(headers));

        // we add a row for each map in the list
        for(Map<Object,Object> map : list) {
            // will contain all the values sorted as needed to comply to the header
            String[] content = new String[headers.size()];

            // adding each field of the map in a different column of the row
            for(Map.Entry issue : map.entrySet()) {
                // index of the column to fill comparing key and headers
                int index = headers.indexOf(issue.getKey().toString());
                // get the cell having the same key as the header
                content[index] = issue.getValue().toString();
            }

            // create a row from data as string's list
            Row newRow = createRow(Arrays.asList(content));
            // adding a new row
            sheetData.getRow().add(newRow);
        }
    }

    /**
     * Create a row containing empty strings
     * @param size Number of cells in the row
     * @return a new 'empty' row
     */
    private Row createEmptyRow(int size) {
        // we create a stubbed list
        List<String> list = Collections.nCopies(size, "");
        // to reuse create row and get empty cells
        return createRow(list);
    }

    /**
     * Extract a list of string which represents all the possible key of all maps
     * @param list List of map whose you want to extract keys
     * @return a list of strings
     */
    private List<String> extractHeader(List<Map> list) {
        // list of header titles to be returned
        List<String> result = new ArrayList<>();
        // gather all unique keys of all maps
        Map<String, String> gatherer = new HashMap<>();

        // we gather all the key thanks to the map
        for(Map map : list) {
            gatherer.putAll(map);
        }

        // for each unique header
        for(Map.Entry header : gatherer.entrySet()) {
            // we had the key to the list
            result.add(header.getKey().toString());
        }

        return result;
    }

    /**
     * Create a row from a list of strings
     * @param list data to fill out the row
     * @return a filled out row
     */
    private Row createRow(List<String> list) {
        // create a new row from the context, it will be returned
        Row row = Context.getsmlObjectFactory().createRow();
        // gather all cells to be had to the row
        ArrayList<Cell> cells = new ArrayList<>();

        // add each element of the list
        for(String s : list) {
            cells.add(createCell(s));
        }

        // add the cells to the row
        row.getC().addAll(cells);
        // return the header row
        return row;
    }

    /**
     * Fill the sheet
     * @param sheet Sheet to fill
     * @param report Data to put inside
     */
    private void addContent(WorksheetPart sheet, Report report) {
        // Minimal content already present
        SheetData sheetData = sheet.getJaxbElement().getSheetData();

        // Issues
        List<Issue> issues = report.getIssues();

        // Add header
        Row row = Context.getsmlObjectFactory().createRow();
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(createCell("rule"));
        cells.add(createCell("message"));
        cells.add(createCell("type"));
        cells.add(createCell("severity"));
        cells.add(createCell("file"));
        cells.add(createCell("line"));
        row.getC().addAll(cells);
        sheetData.getRow().add(row);

        // add issues
        for(Issue issue : issues) {
            // initialization of a new row
            row = Context.getsmlObjectFactory().createRow();
            cells = new ArrayList<>();

            // adding data
            cells.add(createCell(issue.getRule()));
            cells.add(createCell(issue.getMessage()));
            cells.add(createCell(issue.getType()));
            cells.add(createCell(issue.getSeverity()));
            cells.add(createCell(issue.getComponent()));
            cells.add(createCell(issue.getLine()));

            // adding a new row
            row.getC().addAll(cells);
            sheetData.getRow().add(row);
        }

    }

    /**
     * Create a cell able to contain strings
     * @param content string to put in the cell
     * @return a new cell
     */
    private static Cell createCell(String content) {
        // create a new cell
        Cell cell = Context.getsmlObjectFactory().createCell();

        // set the value of the cell by adding the string to the shared string list
        // and set the value of the cell
        setCell(cell, content);

        return cell;

    }

    /**
     * Set the value of a cell
     *      1- add the string to the shared strings list
     *      2- set the cell's value
     * @param cell cell to set
     * @param content content to set in the cell
     */
    private static void setCell(Cell cell, String content) {
        // add a string
        CTXstringWhitespace ctx = Context.getsmlObjectFactory().createCTXstringWhitespace();
        // set string content
        ctx.setValue(content);

        CTRst ctrst = new CTRst();
        ctrst.setT(ctx);

        cell.setT(STCellType.INLINE_STR);
        // add ctrst as inline string
        cell.setIs(ctrst);
    }

}
