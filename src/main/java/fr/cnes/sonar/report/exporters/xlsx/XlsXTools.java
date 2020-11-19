package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.utils.StringManager;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

import java.util.*;

/**
 * Different tools to manipulate docx
 */
public final class XlsXTools {

    /**
     * The maximum number of characters that can be contained in a cell
     */
    private static final int MAX_CELL_SIZE = 32767;
    /**
     * Column index for rule id
     */
    private static final int RULE_ID_INDEX = 0;
    /**
     * Column index for issue's message
     */
    private static final int MESSAGE_INDEX = 1;
    /**
     * Column index for issue's type
     */
    private static final int ISSUE_TYPE_INDEX = 2;
    /**
     * Column index for issue's severity
     */
    private static final int ISSUE_SEVERITY_INDEX = 3;
    /**
     * Column index for issue's language
     */
    private static final int ISSUE_LANGUAGE_INDEX = 4;
    /**
     * Column index for issue's file
     */
    private static final int ISSUE_FILE_INDEX = 5;
    /**
     * Column index for issue's line
     */
    private static final int ISSUE_LINE_INDEX = 6;
    /**
     * Column index for issue's effort
     */
    private static final int ISSUE_EFFORT_INDEX = 7;
    /**
     * Column index for issue's status
     */
    private static final int ISSUE_STATUS_INDEX = 8;
    /**
     * Column index for issue's status
     */
    private static final int ISSUE_COMMENTS_INDEX = 9;
    /**
     * Status for false positive / wont fix
     */
    private static final String RESOLVED = "RESOLVED";

    /**
     * Private constructor to hide the public one
     */
    private XlsXTools() {}

    /**
     * Add a list of Map in an excel sheet
     * @param sheet sheet to fill out
     * @param list list of map to put
     * @param tableName name of the table to fill out
     */
    public static void addListOfMap(XSSFSheet sheet, List<Map> list, String tableName) {

        // get the headers list
        final List<String> headers = extractHeader(list);

        // Create an object of type XSSFTable containing the template table for selected resources
        final XSSFTable table = findTableByName(sheet, tableName);

        // check that there are resources to print and the table exists
        if(!headers.isEmpty() && null!=table) {
            // get CTTable object
            final CTTable cttable = table.getCTTable();

            // Define the resources range including headers
            final AreaReference allDataRange = new AreaReference(
                    new CellReference(0, 0),
                    new CellReference(list.size(), headers.size() - 1),
                    SpreadsheetVersion.EXCEL2007);

            // Set Range to the Table
            cttable.setRef(allDataRange.formatAsString());

            // set number of columns in the table
            final CTTableColumns columns = cttable.getTableColumns();
            final long oldCount = columns.getCount();
            columns.setCount(headers.size());

            // define header information for the table
            for (long i = oldCount; i < headers.size(); i++) {
                final CTTableColumn column = columns.addNewTableColumn();
                column.setId(i + 1);
            }

            // row index: 0 is the header
            int rowIndex = 0;

            // create the headers' row and add it to the sheet
            createRow(sheet, rowIndex, headers);

            // go to the first resources line
            rowIndex++;
            String[] content;
            // we add a row for each map in the list
            for (Map<String, String> map : list) {
                // will contain all the values sorted as needed to comply to the header
                content = new String[headers.size()];
                int index;
                // adding each field of the map in a different column of the row
                for (Map.Entry issue : map.entrySet()) {
                    // index of the column to fill comparing key and headers
                    index = headers.indexOf(issue.getKey().toString());
                    // get the cell having the same key as the header
                    content[index] = issue.getValue().toString();
                }

                // create a row from resources as string's list
                createRow(sheet, rowIndex, Arrays.asList(content));
                // go to the next line
                rowIndex++;
            }
        }
    }

    /**
     * Extract a list of string which represents all the possible key of all maps
     * @param list List of map whose you want to extract keys
     * @return a list of strings
     */
    public static List<String> extractHeader(List<Map> list) {
        // list of header titles to be returned
        final List<String> result = new ArrayList<>();
        // gather all unique keys of all maps
        final Map<String, String> gatherer = new HashMap<>();

        // we gather all the key thanks to the map
        for(Map<String,String> map : list) {
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
     * @param sheet Sheet to fill out
     * @param index Index of the row to create
     * @param list resources to fill out the row  @return a filled out row
     * @return return the created row as a XSSFRow
     */
    public static XSSFRow createRow(XSSFSheet sheet, int index, List<String> list) {
        // create a new row from the context, it will be returned
        final XSSFRow row = sheet.createRow(index);

        // index on the columns of the row
        int colIndex = 0;

        // add each element of the list
        for(String s : list) {

            // a cell cannot contain a string bigger than 32,767 chars
            // so we check and cut it if it is too long
            if(s != null && s.length() > MAX_CELL_SIZE) {
                s = s.substring(0, MAX_CELL_SIZE);
            }
            row.createCell(colIndex).setCellValue(s);

            // go to the next column
            colIndex++;
        }

        // return the header row
        return row;
    }

    /**
     * Write the formatted resources as wanted in the corresponding sheet
     * @param issues Intern resources to format to excel
     * @param selectedSheet sheet where we want to write
     * @param selectedTableName Name of the table to fill
     */
    public static void addSelectedData(List<Issue> issues, XSSFSheet selectedSheet,
                                       String selectedTableName) {

        // Create an object of type XSSFTable containing the template table for selected resources
        final XSSFTable selectedTable = findTableByName(selectedSheet, selectedTableName);

        // check that the table exists
        if(null!=selectedTable && !issues.isEmpty()) {
            // get CTTable object
            final CTTable cttable = selectedTable.getCTTable();

            // Define the resources range including headers
            final AreaReference selectedDataRange = new AreaReference(
                    new CellReference(0, 0),
                    new CellReference(issues.size(), 9),
                    SpreadsheetVersion.EXCEL2007);

            // Set Range to the Table
            cttable.setRef(selectedDataRange.formatAsString());

            // number of the row to insert, begin to 1 because 0 is the header
            int numRow = 1;

            // add issues
            for (Issue issue : issues) {
                // initialization of a new row
                final XSSFRow row = selectedSheet.createRow(numRow);

                // adding resources
                row.createCell(RULE_ID_INDEX).setCellValue(issue.getRule());
                row.createCell(MESSAGE_INDEX).setCellValue(issue.getMessage());
                row.createCell(ISSUE_TYPE_INDEX).setCellValue(issue.getType());
                row.createCell(ISSUE_SEVERITY_INDEX).setCellValue(issue.getSeverity());
                row.createCell(ISSUE_LANGUAGE_INDEX).setCellValue(issue.getLanguage());
                row.createCell(ISSUE_FILE_INDEX).setCellValue(issue.getComponent());
                row.createCell(ISSUE_LINE_INDEX).setCellValue(issue.getLine());
                row.createCell(ISSUE_EFFORT_INDEX).setCellValue(issue.getEffort());
                // if the issue's status is RESOLVED we print the status resolution
                String status = issue.getStatus();
                if(status.equals(RESOLVED)) {
                    status = issue.getResolution();
                }
                row.createCell(ISSUE_STATUS_INDEX).setCellValue(status);
                row.createCell(ISSUE_COMMENTS_INDEX).setCellValue(issue.getComments());

                // go to the next line
                numRow++;
            }
        }
    }

    /**
     * Search a table by name
     * @param sheet Sheet to browse
     * @param tableName Name of the table to find
     * @return A Table or null
     */
    public static XSSFTable findTableByName(XSSFSheet sheet, Object tableName) {
        // result that can be null
        XSSFTable result = null;
        // retrieve all tables to browse
        final Iterator<XSSFTable> iterator = sheet.getTables().iterator();
        XSSFTable current;
        // search by name the table corresponding to tableName
        while (iterator.hasNext() && result==null) {
            current = iterator.next();
            if(current.getName().equals(tableName)) {
                result = current;
            }
        }
        return result;
    }

}
