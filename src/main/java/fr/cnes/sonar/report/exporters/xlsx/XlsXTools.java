package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Report;
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
 * @author garconb
 */
public final class XlsXTools {

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
        List<String> headers = extractHeader(list);

        // Create an object of type XSSFTable containing the template table for selected data
        XSSFTable table = findTableByName(sheet, tableName);

        // check that there are data to print and the table exists
        if(!headers.isEmpty() && null!=table) {
            // get CTTable object
            CTTable cttable = table.getCTTable();

            // Define the data range including headers
            AreaReference allDataRange = new AreaReference(new CellReference(0, 0), new CellReference(list.size(), headers.size() - 1));

            // Set Range to the Table
            cttable.setRef(allDataRange.formatAsString());

            // set number of columns in the table
            CTTableColumns columns = cttable.getTableColumns();
            long oldCount = columns.getCount();
            columns.setCount(headers.size());

            // define header information for the table
            for (long i = oldCount; i < headers.size(); i++) {
                CTTableColumn column = columns.addNewTableColumn();
                column.setId(i + 1);
            }

            // row index: 0 is the header
            int rowIndex = 0;

            // create the headers' row and add it to the sheet
            createRow(sheet, rowIndex, headers);

            // go to the first data line
            rowIndex++;

            // we add a row for each map in the list
            for (Map<Object, Object> map : list) {
                // will contain all the values sorted as needed to comply to the header
                String[] content = new String[headers.size()];

                // adding each field of the map in a different column of the row
                for (Map.Entry issue : map.entrySet()) {
                    // index of the column to fill comparing key and headers
                    int index = headers.indexOf(issue.getKey().toString());
                    // get the cell having the same key as the header
                    content[index] = issue.getValue().toString();
                }

                // create a row from data as string's list
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
     * @param sheet Sheet to fill out
     * @param index Index of the row to create
     * @param list data to fill out the row  @return a filled out row
     */
    public static XSSFRow createRow(XSSFSheet sheet, int index, List<String> list) {
        // create a new row from the context, it will be returned
        XSSFRow row = sheet.createRow(index);

        // index on the columns of the row
        int colIndex = 0;

        // add each element of the list
        for(String s : list) {
            row.createCell(colIndex).setCellValue(s);
            // go to the next column
            colIndex++;
        }

        // return the header row
        return row;
    }

    /**
     * Write the formatted data as wanted in the corresponding sheet
     * @param report Intern data to format to excel
     * @param selectedSheet sheet where we want to write
     * @param selectedTableName Name of the table to fill
     */
    public static void addSelectedData(Report report, XSSFSheet selectedSheet, String selectedTableName) {

        // retrieve issues from report
        List<Issue> issues = report.getIssues();

        // Create an object of type XSSFTable containing the template table for selected data
        XSSFTable selectedTable = findTableByName(selectedSheet, selectedTableName);

        // check that the table exists
        if(null!=selectedTable) {
            // get CTTable object
            CTTable cttable = selectedTable.getCTTable();

            // Define the data range including headers
            AreaReference selectedDataRange = new AreaReference(new CellReference(0, 0), new CellReference(issues.size(), 6));

            // Set Range to the Table
            cttable.setRef(selectedDataRange.formatAsString());

            // number of the row to insert, begin to 1 because 0 is the header
            int numRow = 1;

            // add issues
            for (Issue issue : issues) {
                // initialization of a new row
                XSSFRow row = selectedSheet.createRow(numRow);

                // adding data
                row.createCell(0).setCellValue(issue.getRule());
                row.createCell(1).setCellValue(issue.getMessage());
                row.createCell(2).setCellValue(issue.getType());
                row.createCell(3).setCellValue(issue.getSeverity());
                row.createCell(4).setCellValue(issue.getComponent());
                row.createCell(5).setCellValue(issue.getLine());
                row.createCell(6).setCellValue(issue.getEffort());

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
        Iterator iterator = sheet.getTables().iterator();
        // search by name the table corresponding to tableName
        while (iterator.hasNext() && result==null) {
            XSSFTable current = (XSSFTable) iterator.next();
            result = current.getName().equals(tableName) ? current : null;
        }
        return result;
    }

}
