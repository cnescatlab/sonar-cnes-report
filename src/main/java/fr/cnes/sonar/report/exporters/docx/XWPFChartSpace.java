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
import fr.cnes.sonar.report.model.TimeValue;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;

import java.io.IOException;
import java.util.List;

/**
 * Custom class to handle chart spaces
 * Complete the Apache POI framework
 */
public class XWPFChartSpace {

    /**
     * Encapsulated POI version of chart
     */
    private XWPFChart chart;

    /**
     * Basic constructor based on ChartSpaceDocumentImpl
     * @param ctChartSpace a prebuilt chart space
     * @param chartPackagePart the package part corresponding to the chart
     */
    public XWPFChartSpace(final XWPFChart chart) {
        this.chart = chart;
    }

    /**
     * Collect all the ChartSpace containing Charts and return it as a list of
     * high level XWPFChartSpace
     * @param document Document from which extract chart spaces
     * @return list of chart spaces
     * @throws IOException When reading files
     * @throws XmlException When reading files
     */
    public static List<XWPFChartSpace> getChartSpaces(final XWPFDocument document) {

        // gather charts
        final List<XWPFChartSpace> charts = Lists.newArrayList();

        for (POIXMLDocumentPart part : document.getRelations()) {
            if (part instanceof XWPFChart) {
                charts.add(new XWPFChartSpace((XWPFChart) part));
            }
        }

        return charts;
    }

    /**
     * Getter for chart
     * @return a XWPFChart
     */
    public XWPFChart getChart() {
        return this.chart;
    }

    /**
     * Setter for chart
     * @param XWPFChart new chart value
     */
    public void setChartSpace(final XWPFChart pChart) {
        this.chart = pChart;
    }

    /**
     * Retrieve the chart's title value
     * @return a string containing the raw title
     */
    public String getTitle() {
        return chart.getCTChart().getTitle().getTx().getRich().getPList().get(0).getRList().get(0).getT();
    }

    /**
     * Set the value of chart's title
     * @param newTitle the new value
     * @throws IOException error when writing the chart's file
     */
    public void setTitle(final String newTitle) throws IOException {
        chart.getCTChart().getTitle().getTx().getRich().getPList().get(0).getRList().get(0).setT(newTitle);
    }

    /**
     * Set values contained in the chart 
     * @param values values to set as a list of label/value
     * @throws IOException when writing the file
     */
    public void setValues(List<Value> values) throws IOException {
        final CTPlotArea ctPlotArea = chart.getCTChart().getPlotArea();

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
    }

    /**
     * Set time values inside the dedicated chart 
     * @param values values to set as a list of date/value
     * @throws IOException when writing the file
     */
    public void setTimeValues(List<TimeValue> values) throws IOException {
        final CTPlotArea ctPlotArea = chart.getCTChart().getPlotArea();

        // if the chart is a scatter chart we continue
        if(!ctPlotArea.getScatterChartList().isEmpty()) {
            //get lists of x values and y values
            final List<CTNumVal> ptListXVal = ctPlotArea.getScatterChartList().get(0)
                    .getSerList().get(0).getXVal().getNumRef().getNumCache().getPtList();
            final List<CTNumVal> ptListYVal = ctPlotArea.getScatterChartList().get(0)
                    .getSerList().get(0).getYVal().getNumRef().getNumCache().getPtList();

            // clear what could be present before
            ptListXVal.clear();
            ptListYVal.clear();

            // format date values
            final CTNumData xNumCache = ctPlotArea.getScatterChartList().get(0)
                    .getSerList().get(0).getXVal().getNumRef().getNumCache();
            xNumCache.setFormatCode("dd/mm/yyyy\\ hh:mm");

            // format date axis
            final CTNumFmt dateAxisFormat = ctPlotArea.getValAxList().get(0).getNumFmt();
            dateAxisFormat.setFormatCode("dd/mm/yyyy\\ hh:mm");

            // write resources in the scatter chart
            for (int i = 0 ; i < values.size() ; i++) {
                // instantiate new x and y values
                final CTNumVal xVal = CTNumVal.Factory.newInstance();
                final CTNumVal yVal = CTNumVal.Factory.newInstance();

                // set ids
                xVal.setIdx(i);
                yVal.setIdx(i);

                // set values
                xVal.setV(String.valueOf(values.get(i).getDate()));
                yVal.setV(String.valueOf(values.get(i).getValue()));

                // add new resources to lists
                ptListXVal.add(xVal);
                ptListYVal.add(yVal);
            }
        }
        // otherwise we do not support other type for now
    }

}
