/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.model.Facets;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.TimeFacets;
import fr.cnes.sonar.report.model.Value;
import fr.cnes.sonar.report.model.TimeValue;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Different tools to manipulate docx
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
     * title for chart displaying the evolution of the number of issues
     */
    private static final String CHART_VIOLATIONS_TITLE = "chart.violations.title";
    /**
     * title for chart displaying the evolution of the technical debt ratio
     */
    private static final String CHART_TECHNICAL_DEBT_RATIO_TITLE = "chart.technicalDebtRatio.title";
    /**
     * placeholder for chart displaying number of issues by severity
     */
    private static final String TYPE_TABLE_PLACEHOLDER = "$TYPE";
    /**
     * placeholder for chart displaying number of issues by severity
     */
    private static final String SEVERITY_TABLE_PLACEHOLDER = "$SEVERITY";
    /**
     * placeholder for chart displaying issues history
     */
    private static final String VIOLATIONS_CHART_PLACEHOLDER = "$VIOLATIONS";
    /**
     * placeholder for chart displaying technical debt ratio history
     */
    private static final String TECHNICAL_DEBT_RATIO_CHART_PLACEHOLDER = "$TECHNICAL_DEBT_RATIO";
    /**
     * facet's name for number of issues by severity
     */
    private static final String SEVERITIES = "severities";
    /**
     * facet's name for number of issues by type
     */
    private static final String TYPES = "types";
    /**
     * facet's name for issues history
     */
    private static final String VIOLATIONS = "violations";
    /**
     * facet's name for technical debt ratio history
     */
    private static final String TECHNICAL_DEBT_RATIO = "sqale_debt_ratio";

    /**
     * Private constructor to hide the public one
     */
    private DocXTools() {}

    /**
     * Fill the chart "camembert"
     * @param document word document
     * @param facets resources as facets
     * @throws IOException ...
     */
    public static void fillCharts(XWPFDocument document, Report report)
            throws IOException {

        final Facets facets = report.getFacets();
        final TimeFacets timeFacets = report.getTimeFacets();
        final List<XWPFChartSpace> chartSpaces = XWPFChartSpace.getChartSpaces(document);
        final List<Value> dataPerType = facets.getFacetValues(TYPES);
        final List<Value> dataPerSeverity = facets.getFacetValues(SEVERITIES);
        final List<TimeValue> issuesHistory = timeFacets.getFacetValues(VIOLATIONS);
        final List<TimeValue> technicalDebtRatioHistory = timeFacets.getFacetValues(TECHNICAL_DEBT_RATIO);

        // browse chart list to find placeholders (based on locale) in title
        // and provide them adapted resources
        
        for (XWPFChartSpace chartSpace : chartSpaces) {
        	
            final String currentChartTitle = chartSpace.getTitle();
            // fill the pie chart with issues count by severity
            if(currentChartTitle.contains(SEVERITY_TABLE_PLACEHOLDER)) {
                chartSpace.setValues(dataPerSeverity);
                chartSpace.setTitle(StringManager.string(CHART_SEVERITY_TITLE));
            // fill the pie chart with issues count by type
            } else if(currentChartTitle.contains(TYPE_TABLE_PLACEHOLDER)) {
                chartSpace.setValues(dataPerType);
                chartSpace.setTitle(StringManager.string(CHART_TYPE_TITLE));
            // fill the scatter chart with the evolution of the number of issues
            } else if(currentChartTitle.contains(VIOLATIONS_CHART_PLACEHOLDER)) {
                chartSpace.setTimeValues(issuesHistory);
                chartSpace.setTitle(StringManager.string(CHART_VIOLATIONS_TITLE));
            // fill the scatter chart with the evolution of the technical debt ratio
            } else if(currentChartTitle.contains(TECHNICAL_DEBT_RATIO_CHART_PLACEHOLDER)) {
                chartSpace.setTimeValues(technicalDebtRatioHistory);
                chartSpace.setTitle(StringManager.string(CHART_TECHNICAL_DEBT_RATIO_TITLE));
            }

        }
        
    }

    /**
     * Replace all placeholder in a word
     * A word file is composed of paragraph which are composed of runs
     * which contains string.
     * @param document word file
     * @param values a map containing pairs of placeholder/value
     * @throws OpenXML4JException when a problem occurred on the file writting
     * @throws IOException when a problem occurred on the pictures loading
     */
    static void replacePlaceholder(XWPFDocument document, Map<String,String> values)
            throws OpenXML4JException, IOException {

        // Gather all elements in header, footer and body
        // in order to extract for each parts all paragraphs
        final List<IBodyElement> elements = getAllElements(document);

        // gather all paragraphs from the previous collected elements
        final List<XWPFParagraph> paragraphs = getAllParagraphs(elements);

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
        final List<XWPFParagraph> paragraphs = new ArrayList<>();
        // browse elements
        final Iterator<IBodyElement> elementIterator = elements.iterator();
        IBodyElement elt;

        while (elementIterator.hasNext()) {
            elt = elementIterator.next();

            // if we find a paragraph we just add it to the list
            if (elt.getElementType().compareTo(BodyElementType.PARAGRAPH) == 0) {
                paragraphs.add((XWPFParagraph) elt);
            // otherwise if it is a table we browse its lines
            // in order to collect all paragraphs of all cells
            } else if(elt.getElementType().compareTo(BodyElementType.TABLE)==0) {
                final XWPFTable table = (XWPFTable) elt;
                // browse lines
                final Iterator<XWPFTableRow> rowIterator = table.getRows().iterator();
                XWPFTableRow row;
                while(rowIterator.hasNext()) {
                    row = rowIterator.next();
                    // browse cells
                    final Iterator<XWPFTableCell> cellIterator = row.getTableCells().iterator();
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
        final List<IBodyElement> elements = new ArrayList<>(document.getBodyElements());
        // iterator for headers
        final Iterator<XWPFHeader> headerIterator = document.getHeaderList().iterator();
        // iterator for footers
        final Iterator<XWPFFooter> footerIterator = document.getFooterList().iterator();

        // collect header elements
        while (headerIterator.hasNext()) {
            final XWPFHeaderFooter header = headerIterator.next();
            elements.addAll(header.getBodyElements());
        }
        // collect footer elements
        while (footerIterator.hasNext()) {
            final XWPFHeaderFooter header = footerIterator.next();
            elements.addAll(header.getBodyElements());
        }
        return elements;
    }

 

    /**
     * Replace placeholders inside a paragraph by values given in a map.
     * Conserve word style.
     * @param paragraph paragraph to modify, style will be the default paragraph style
     * @param values a map indexed by placeholders
     * @throws IOException When opening pictures
     * @throws InvalidFormatException When dealing with open files
     */
    private static void replaceInParagraph(XWPFParagraph paragraph, Map<String,String> values)
            throws IOException, InvalidFormatException {
        
        
        final List<XWPFRun> runs = paragraph.getRuns();
         String text;
        final List<String> pictures = new ArrayList<>();
        String key;
        String value;
        ClassLoader classloader;
        InputStream is;
        BufferedImage image;
        ByteArrayOutputStream baos;
        ByteArrayInputStream bais;
        // For all Run
        for (XWPFRun currentRun : runs){
            // if there are matter to work on
            if(currentRun.getText(0) != null) {            

                // construct here the new string by replacing each placeholder by its value
                text = currentRun.getText(0);
                
                pictures.clear();
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
                // Replace le text
                currentRun.setText(text,0);

                // add images if we have something to add
                if(!pictures.isEmpty()) {
                    // browse picture list previously filled out
                    for(String filename : pictures) {
                        // get the image from resources as an input stream
                        classloader = DocXTools.class.getClassLoader();
                        is = classloader.getResourceAsStream(IMG_FOLDER + filename);
                        // convert the input stream to a buffered image
                        image = ImageIO.read(is);
                        // close the input stream
                        is.close();
                        // retrieve image dimensions
                        int width = image.getWidth();
                        int height = image.getHeight();
                        // ratio for dimensions shrinking
                        double ratio = 0.25;
                        // write the buffered image on a byte array output stream
                        baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        // create a byte array input stream from the previous stream
                        bais = new ByteArrayInputStream(baos.toByteArray());
                        // add the image to the run
                        currentRun.addPicture(bais, Document.PICTURE_TYPE_PNG,
                                filename, Units.toEMU(width*ratio), Units.toEMU(height*ratio));
                    }
                }
            }
        }


    }

    /**
     * Fill a table with resources sorted by lines in a list of strings
     * You can select the table to fill with th field "name", it must be
     * a placeholder in your table, if it is not found, a new table is
     * added at the end of the document.
     * @param document Document containing the table
     * @param header Header to add in the table
     * @param data Issues to add in the table
     * @param name Table's name
     */
    public static void fillTable(XWPFDocument document, List<String> header,
                                 List<List<String>> data, String name) {

        // if there are no resources, there a
        if(data!=null) {
            // table to fill out
            XWPFTable table = null;

            // search for a table with the corresponding placeholder
            final Iterator<XWPFTable> iterator = document.getTablesIterator();
            XWPFTable current;
            boolean found = false;
            while (iterator.hasNext() && !found) {
                current = iterator.next();
                if(current.getText().contains(name)) {
                    table = current;
                    found = true;
                }
            }

            // if the table does not exist don't fill
            if (found) {
                // we clear the table                
            	for (int i = table.getNumberOfRows() - 1; i >= 0; --i) {
                        table.removeRow(i);
                
                }
	            
                if (!data.isEmpty()) {
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
                        final List<String> line = data.get(iLine);
        
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
    }
}
