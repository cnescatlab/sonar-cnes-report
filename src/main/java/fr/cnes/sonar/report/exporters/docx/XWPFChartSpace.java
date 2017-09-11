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

import fr.cnes.sonar.report.model.Value;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.impl.ChartSpaceDocumentImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Custom class to handle chart spaces
 * Complete the Apache POI framework
 * @author lequal
 */
public class XWPFChartSpace {
    /**
     * Logger of this class
     */
    private static final Logger LOGGER = Logger.getLogger(XWPFChartSpace.class.getName());

    /**
     * Encapsulated POI version of chart space
     */
    private ChartSpaceDocumentImpl chartSpace;

    /**
     * {@link PackagePart} to save the current ChartSpaceDocument
     */
    private PackagePart packagePart;

    /**
     * Basic constructor based on ChartSpaceDocumentImpl
     * @param ctChartSpace a prebuilt chart space
     * @param chartPackagePart the package part corresponding to the chart
     */
    public XWPFChartSpace(ChartSpaceDocumentImpl ctChartSpace, PackagePart chartPackagePart) {
        this.chartSpace = ctChartSpace;
        this.packagePart = chartPackagePart;
    }

    /**
     * Collect all the ChartSpace containing Charts and return it as a list of
     * high level XWPFChartSpace
     * @param document Document from which extract chart spaces
     * @return list of chart spaces
     * @throws IOException When reading files
     * @throws XmlException When reading files
     * @throws OpenXML4JException When handling nodes
     */
    public static List<XWPFChartSpace> getChartSpaces(XWPFDocument document)
            throws IOException, XmlException, OpenXML4JException {
        // gather charts documents
        List<POIXMLDocumentPart> charts = new ArrayList<>();
        // results list to return at the end
        List<XWPFChartSpace> result = new ArrayList<>();

        // get parts related to charts
        for(POIXMLDocumentPart p : document.getRelations()) {
            if(p.toString().contains("/word/charts/")) {
                charts.add(p);
            }
        }

        // get chart spaces inside previous parts
        for(POIXMLDocumentPart p : charts) {
            ChartSpaceDocumentImpl c = (ChartSpaceDocumentImpl) XmlObject.Factory.parse(p.getPackagePart().getInputStream(),
                    POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            result.add(new XWPFChartSpace(c,p.getPackagePart()));
        }

        return result;
    }

    /**
     * Getter for chartSpace
     * @return a CTChartSpace
     */
    public ChartSpaceDocumentImpl getChartSpace() {
        return this.chartSpace;
    }

    /**
     * Setter for chartSpace
     * @param ctChartSpace new chartSpace value
     */
    public void setChartSpace(ChartSpaceDocumentImpl ctChartSpace) {
        this.chartSpace = ctChartSpace;
    }

    /**
     * Retrieve the chart's title value
     * @return a string containing the raw title
     */
    public String getTitle() {
        String title;
        title = chartSpace.getChartSpace().getChart().getTitle().getTx().getRich().getPList().get(0).getRList().get(0).getT();
        return title;
    }

    /**
     * Set the value of chart's title
     * @param newTitle the new value
     */
    public void setTitle(String newTitle) throws IOException {
        chartSpace.getChartSpace().getChart().getTitle().getTx().getRich().getPList().get(0).getRList().get(0).setT(newTitle);
        this.save();
    }

    /**
     * Save modifications on Chart, must be called for each Chart
     * @throws IOException When saving chart in the output stream
     */
    public void save() throws IOException {
        OutputStream outputStream = packagePart.getOutputStream();
        chartSpace.save(outputStream);
        outputStream.close();
    }

    public void setValues(List<Value> values) throws IOException {
        CTPlotArea ctPlotArea = chartSpace.getChartSpace().getChart().getPlotArea();

        // if the chart is a pie chart we continue
        if(!ctPlotArea.getPieChartList().isEmpty()) {
            // get lists of values and categories (labels) of the pie chart
            List<CTNumVal> ptListVal = ctPlotArea.getPieChartList().get(0).getSerList().get(0).getVal().getNumRef().getNumCache().getPtList();
            List<CTStrVal> ptListCat = ctPlotArea.getPieChartList().get(0).getSerList().get(0).getCat().getStrRef().getStrCache().getPtList();

            // clear what could be present before
            ptListCat.clear();
            ptListVal.clear();

            // write resources in the pie chart
            for (int i = 0 ; i < values.size() ; i++) {
                // instantiate new label and value
                CTStrVal cat = CTStrVal.Factory.newInstance();
                CTNumVal val = CTNumVal.Factory.newInstance();

                // set ids
                cat.setIdx(i);
                val.setIdx(i);

                // set values
                cat.setV(String.valueOf(values.get(i).getVal()));
                val.setV(String.valueOf(values.get(i).getCount()));

                // add new resources to lists
                ptListCat.add(cat);
                ptListVal.add(val);
            }

        }
        // otherwise we do not support other type for now

        // finally we save modifications
        this.save();
    }

}
