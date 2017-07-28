package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Value;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static fr.cnes.sonar.report.exporters.docx.DataAdapter.getFacetValues;

/**
 * Different tools to manipulate docx
 * @author begarco
 */
public final class DocXTools {

    /**
     * folder for image resources
     */
    private static final String IMG_FOLDER = "img/";
    /**
     * extension for png
     */
    private static final String PNG_EXTENSION = ".png";
    /**
     * title for chart displaying number of issues by severity
     */
    private static final String CHART_SEVERITY_TITLE = "chart.severity.title";
    /**
     * title for chart displaying number of issues by type
     */
    private static final String CHART_TYPE_TITLE = "chart.type.title";
    /**
     * placeholder for chart displaying number of issues by severity
     */
    private static final String TYPE_TABLE_PLACEHOLDER = "$TYPE";
    /**
     * placeholder for chart displaying number of issues by severity
     */
    private static final String SEVERITY_TABLE_PLACEHOLDER = "$SEVERITY";
    /**
     * facet's name for number of issues by severity
     */
    private static final String SEVERITIES = "severities";
    /**
     * facet's name for number of issues by type
     */
    private static final String TYPES = "types";

    /**
     * Private constructor to hide the public one
     */
    private DocXTools() {}

    /**
     * Fill the chart "camembert"
     * @param opcPackage word document
     * @param facets resources as facets
     * @throws InvalidFormatException ...
     */
    public static void fillCharts(OPCPackage opcPackage, XWPFDocument document, List<Facet> facets)
            throws OpenXML4JException, IOException, XmlException {

        List<XWPFChartSpace> chartSpaces = XWPFChartSpace.getChartSpaces(document);
        List<Value> dataPerType = getFacetValues(facets, TYPES);
        List<Value> dataPerSeverity = getFacetValues(facets, SEVERITIES);

        // browse chart list to find placeholders (based on locale) in title
        // and provide them adapted resources
        for (XWPFChartSpace chartSpace : chartSpaces) {
            String currentChartTitle = chartSpace.getTitle();
            // fill the pie chart with issues count by severity
            if(currentChartTitle.contains(SEVERITY_TABLE_PLACEHOLDER)) {
                chartSpace.setValues(dataPerSeverity);
                chartSpace.setTitle(StringManager.string(CHART_SEVERITY_TITLE));
            }
            // fill the pie chart with issues count by type
            else if(currentChartTitle.contains(TYPE_TABLE_PLACEHOLDER)) {
                chartSpace.setValues(dataPerType);
                chartSpace.setTitle(StringManager.string(CHART_TYPE_TITLE));
            }
        }
    }

    /**
     * Replace all placeholder in a word
     * A word file is composed of paragraph which are composed of runs
     * which contains string.
     * @param document word file
     * @param values a map containing pairs of placeholder/value
     * @throws IOException when a problem occurred on the pictures loading
     */
    static void replacePlaceholder(XWPFDocument document, Map<String,String> values)
            throws OpenXML4JException, IOException {

        // Gather all elements in header, footer and body
        // in order to extract for each parts all paragraphs
        List<IBodyElement> elements = getAllElements(document);

        // gather all paragraphs from the previous collected elements
        List<XWPFParagraph> paragraphs = getAllParagraphs(elements);

        // replace all placeholders in all gathered paragraphs
        for (XWPFParagraph p : paragraphs) {
            replaceInParagraph(p, values);
        }

    }

    /**
     * Extract all paragraphs from a list of IBodyElement
     * @param elements list of IBodyElement to browse
     * @return a list of XWPFParagraph
     */
    private static List<XWPFParagraph> getAllParagraphs(List<IBodyElement> elements) {
        // final list to return
        List<XWPFParagraph> paragraphs = new ArrayList<>();
        // browse elements
        Iterator<IBodyElement> elementIterator = elements.iterator();
        IBodyElement elt;

        while (elementIterator.hasNext()) {
            elt = elementIterator.next();

            // if we find a paragraph we just add it to the list
            if (elt.getElementType().compareTo(BodyElementType.PARAGRAPH) == 0) {
                paragraphs.add((XWPFParagraph) elt);
            }
            // otherwise if it is a table we browse its lines
            // in order to collect all paragraphs of all cells
            else if(elt.getElementType().compareTo(BodyElementType.TABLE)==0) {
                XWPFTable table = (XWPFTable) elt;
                // browse lines
                Iterator<XWPFTableRow> rowIterator = table.getRows().iterator();
                XWPFTableRow row;
                while(rowIterator.hasNext()) {
                    row = rowIterator.next();
                    // browse cells
                    Iterator<XWPFTableCell> cellIterator = row.getTableCells().iterator();
                    XWPFTableCell cell;
                    while(cellIterator.hasNext()) {
                        cell = cellIterator.next();
                        // final paragraphs add from a cell
                        paragraphs.addAll(cell.getParagraphs());
                    }
                }
            }
        }

        return paragraphs;
    }

    /**
     * Gather all elements from a document
     * @param document the complete xwpf document
     * @return a list of IBodyElement
     */
    private static List<IBodyElement> getAllElements(XWPFDocument document) {
        // gather the final list
        // add directly body content
        List<IBodyElement> elements = new ArrayList<>(document.getBodyElements());
        // iterator for headers
        Iterator<XWPFHeader> headerIterator = document.getHeaderList().iterator();
        // iterator for footers
        Iterator<XWPFFooter> footerIterator = document.getFooterList().iterator();

        // collect header elements
        while (headerIterator.hasNext()) {
            XWPFHeaderFooter header = headerIterator.next();
            elements.addAll(header.getBodyElements());
        }
        // collect footer elements
        while (footerIterator.hasNext()) {
            XWPFHeaderFooter header = footerIterator.next();
            elements.addAll(header.getBodyElements());
        }
        return elements;
    }

    /**
     * Replace placeholders inside a paragraph by values given in a map
     * @param paragraph paragraph to modify, style will be the default paragraph style
     * @param values a map indexed by placeholders
     * @throws IOException When opening pictures
     * @throws InvalidFormatException When dealing with open files
     */
    private static void replaceInParagraph(XWPFParagraph paragraph, Map<String,String> values) throws IOException, InvalidFormatException {
        // Concatenate all runs' content in a single string
        StringBuilder sb = new StringBuilder();
        List<XWPFRun> runs = paragraph.getRuns();
        int pos;
        for (XWPFRun r : runs){
            pos = r.getTextPosition();
            if(r.getText(pos) != null) {
                sb.append(r.getText(pos));
            }
        }

        // if there are matter to work on
        if(sb.length()!=0) {
            // delete all runs to replace it by one single run
            for(int i = runs.size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // construct here the new string by replacing each placeholder by its value
            String text = sb.toString();
            List<String> pictures = new ArrayList<>();
            String key, value;
            for (Map.Entry<String, String> nextValue : values.entrySet()) {
                key = nextValue.getKey();
                value = nextValue.getValue();

                // if we try to replace by a png picture
                if(value.endsWith(PNG_EXTENSION) && text.contains(key)) {
                    // we save the filename
                    pictures.add(value);
                    value = StringManager.EMPTY;
                }
                // finally we concatenate
                text = text.replaceAll(key, value);
            }

            // Add new run with updated text
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            // add images if we have something to add
            if(!pictures.isEmpty()) {
                // browse picture list previously filled out
                ClassLoader classloader;
                InputStream is;
                Dimension dim;
                for(String filename : pictures) {
                    classloader = Thread.currentThread().getContextClassLoader();
                    is = classloader.getResourceAsStream(IMG_FOLDER +filename);
                    // height and width are retrieve from here
                    dim = ImageUtils.getImageDimension(is, XWPFDocument.PICTURE_TYPE_PNG);
                    run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, filename, dim.width, dim.height);
                    // close picture
                    is.close();
                }
            }
            paragraph.addRun(run);
        }
    }

    /**
     * Fill a table with resources sorted by lines in a list of strings
     * You can select the table to fill with th field "name", it must be
     * a placeholder in your table, if it is not found, a new table is
     * added at the end of the document.
     * @param document Document containing the table
     * @param data Issues to add in the table
     * @param name Table's name
     */
    public static void fillTable(XWPFDocument document, List<String> header, List<List<String>> data, String name) {

        // if there are no resources, there a
        if(data!=null && !data.isEmpty()) {
            // table to fill out
            XWPFTable table = null;

            // search for a table with the corresponding placeholder
            Iterator<XWPFTable> iterator = document.getTablesIterator();
            XWPFTable current;
            boolean found = false;
            while (iterator.hasNext() && !found) {
                current = iterator.next();
                if(current.getText().contains(name)) {
                    table = current;
                    found = true;
                }
            }

            // if the table does not exist, we create one at the bottom of the document
            if (!found) {
                table = document.createTable();
            }
            // otherwise we clear the table
            else {
                if(table!=null) {
                    for (int i = table.getNumberOfRows() - 1; i >= 0; --i) {
                        table.removeRow(i);
                    }
                }
            }

            // create the top line (header) and fill it
            XWPFTableRow row = table.createRow();
            for(String field : header) {
                row.createCell().setText(field);
            }

            // create resources rows
            for(int i = 0 ; i < data.size() ; i++) {
                table.createRow();
            }

            // fill resources rows
            for(int iLine = 0 ; iLine < data.size() ; iLine++) {
                row = table.getRow(iLine+1);
                List<String> line = data.get(iLine);

                for (int iCell = 0; iCell < line.size(); iCell++) {
                    if(iCell>=row.getTableCells().size()) {
                        row.createCell();
                    }
                    row.getCell(iCell).setText(line.get(iCell));
                }
            }
        }
    }
}
