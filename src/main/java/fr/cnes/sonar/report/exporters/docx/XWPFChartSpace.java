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

import com.google.common.collect.Lists;
import fr.cnes.sonar.report.model.Value;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.ChartSpaceDocument;
import org.openxmlformats.schemas.drawingml.x2006.chart.impl.ChartSpaceDocumentImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom class to handle chart spaces
 * Complete the Apache POI framework
 */
public class XWPFChartSpace {

    /**
     * Encapsulated POI version of chart space
     */
    private ChartSpaceDocument chartSpace;

    /**
     * {@link PackagePart} to save the current ChartSpaceDocument
     */
    private PackagePart packagePart;

    private static final Logger LOGGER = Logger.getLogger(XWPFChartSpace.class.getName());

    /**
     * Basic constructor based on ChartSpaceDocumentImpl
     * @param ctChartSpace a prebuilt chart space
     * @param chartPackagePart the package part corresponding to the chart
     */
    public XWPFChartSpace(final ChartSpaceDocument ctChartSpace, final PackagePart chartPackagePart) {
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
     */
    public static List<XWPFChartSpace> getChartSpaces(final XWPFDocument document)
            throws IOException, XmlException {
        // gather charts documents
        final List<POIXMLDocumentPart> charts = Lists.newArrayList();
        // results list to return at the end
        final List<XWPFChartSpace> result = Lists.newArrayList();

        // get parts related to charts
        for(final POIXMLDocumentPart p : document.getRelations()) {
            if(p.toString().contains("/word/charts/")) {
                charts.add(p);
            }
        }

        // get chart spaces inside previous parts
        for(final POIXMLDocumentPart p : charts) {
            try {
                final InputStream inputStream = p.getPackagePart().getInputStream();
                final ChartSpaceDocument c = ChartSpaceDocument.Factory.parse(inputStream);
                result.add(new XWPFChartSpace(c, p.getPackagePart()));
            } catch(final ClassCastException e){
                LOGGER.log(Level.WARNING, "Error while getting charts, can't convert XMLObject into ChartSpaceDocument", e);
            }
        }

        return result;
    }

    /**
     * Getter for chartSpace
     * @return a CTChartSpace
     */
    public ChartSpaceDocument getChartSpace() {
        return this.chartSpace;
    }

    /**
     * Setter for chartSpace
     * @param ctChartSpace new chartSpace value
     */
    public void setChartSpace(final ChartSpaceDocumentImpl ctChartSpace) {
        this.chartSpace = ctChartSpace;
    }

    /**
     * Retrieve the chart's title value
     * @return a string containing the raw title
     */
    public String getTitle() {
        return chartSpace.getChartSpace().getChart().getTitle().getTx()
                .getRich().getPList().get(0).getRList().get(0).getT();
    }

    /**
     * Set the value of chart's title
     * @param newTitle the new value
     * @throws IOException error when writing the chart's file
     */
    public void setTitle(final String newTitle) throws IOException {
        chartSpace.getChartSpace().getChart().getTitle().getTx().getRich()
                .getPList().get(0).getRList().get(0).setT(newTitle);
        this.save();
    }

    /**
     * Save modifications on Chart, must be called for each Chart
     * @throws IOException When saving chart in the output stream
     */
    public void save() throws IOException {
        final OutputStream outputStream = packagePart.getOutputStream();
        chartSpace.save(outputStream);
        outputStream.close();
    }

    /**
     * Set values contained in the chart 
     * @param values values to set as a list of label/value
     * @throws IOException when writing the file
     */
    public void setValues(final List<Value> values) throws IOException {
        final CTPlotArea ctPlotArea = chartSpace.getChartSpace().getChart().getPlotArea();

        // if the chart is a pie chart we continue
        if(!ctPlotArea.getPieChartList().isEmpty()) {
            // get lists of values and categories (labels) of the pie chart
            final List<CTNumVal> ptListVal = ctPlotArea.getPieChartList().get(0)
                    .getSerList().get(0).getVal().getNumRef().getNumCache().getPtList();
            final List<CTStrVal> ptListCat = ctPlotArea.getPieChartList().get(0)
                    .getSerList().get(0).getCat().getStrRef().getStrCache().getPtList();

            // clear what could be present before
            ptListCat.clear();
            ptListVal.clear();

            // write resources in the pie chart
            for (int i = 0 ; i < values.size() ; i++) {
                // instantiate new label and value
                final CTStrVal cat = CTStrVal.Factory.newInstance();
                final CTNumVal val = CTNumVal.Factory.newInstance();

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
