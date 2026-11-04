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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
     * Namespace of chart elements
     */
    private static final String CHART_NS = "http://schemas.openxmlformats.org/drawingml/2006/chart";
    /**
     * Namespace of drawing elements
     */
    private static final String DRAWING_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    /**
     * Elements following the data points in a pie chart serie (CT_PieSer sequence)
     */
    private static final List<String> AFTER_DATA_POINTS = Arrays.asList("dLbls", "cat", "val", "extLst");

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
        setValues(values, Collections.emptyMap());
    }

    /**
     * Set values contained in the chart and color each slice according to its label
     * @param values values to set as a list of label/value
     * @param colors RGB hexadecimal colors indexed by label; labels without color keep the theme color
     * @throws IOException when writing the file
     */
    public void setValues(List<Value> values, Map<String, String> colors) throws IOException {
        final CTPlotArea ctPlotArea = chart.getCTChart().getPlotArea();

        // if the chart is a pie chart we continue
        if(!ctPlotArea.getPieChartList().isEmpty()) {
            final CTPieSer ser = ctPlotArea.getPieChartList().get(0).getSerList().get(0);
            final CTNumData numCache = ser.getVal().getNumRef().getNumCache();
            final CTStrData strCache = ser.getCat().getStrRef().getStrCache();
            // get lists of values and categories (labels) of the pie chart
            final List<CTNumVal> ptListVal = numCache.getPtList();
            final List<CTStrVal> ptListCat = strCache.getPtList();

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

            // keep the declared number of points consistent with the actual points
            setPointCount(numCache.isSetPtCount() ? numCache.getPtCount() : numCache.addNewPtCount(), values.size());
            setPointCount(strCache.isSetPtCount() ? strCache.getPtCount() : strCache.addNewPtCount(), values.size());

            setSliceColors(ser, values, colors);
        }
        // otherwise we do not support other type for now
    }

    /**
     * Replace the colors of the slices of a pie chart serie
     * @param ser the pie chart serie
     * @param values values of the serie, in the same order as the points
     * @param colors RGB hexadecimal colors indexed by label
     */
    private static void setSliceColors(final CTPieSer ser, final List<Value> values, final Map<String, String> colors) {
        // the light ooxml schemas shipped with POI do not contain CT_DPt: edit the DOM directly
        final Node serNode = ser.getDomNode();
        final Document dom = serNode.getOwnerDocument();
        final String prefix = serNode.getPrefix();

        // remove colors defined for the template's placeholder points, find where new points go
        Node next = null;
        Node child = serNode.getFirstChild();
        while (child != null) {
            final Node sibling = child.getNextSibling();
            if (CHART_NS.equals(child.getNamespaceURI())) {
                if ("dPt".equals(child.getLocalName())) {
                    serNode.removeChild(child);
                } else if (next == null && AFTER_DATA_POINTS.contains(child.getLocalName())) {
                    next = child;
                }
            }
            child = sibling;
        }

        for (int i = 0 ; i < values.size() ; i++) {
            final String color = colors.get(String.valueOf(values.get(i).getVal()));
            if (color != null) {
                final Element point = chartElement(dom, prefix, "dPt");
                final Element index = chartElement(dom, prefix, "idx");
                index.setAttribute("val", String.valueOf(i));
                point.appendChild(index);
                final Element bubble = chartElement(dom, prefix, "bubble3D");
                bubble.setAttribute("val", "0");
                point.appendChild(bubble);
                final Element shape = (Element) point.appendChild(chartElement(dom, prefix, "spPr"));
                shape.appendChild(solidFill(dom, color));
                // white separator between slices
                final Element line = (Element) shape.appendChild(dom.createElementNS(DRAWING_NS, "a:ln"));
                line.setAttribute("w", "19050");
                line.appendChild(solidFill(dom, "FFFFFF"));
                serNode.insertBefore(point, next);
            }
        }
    }

    /**
     * Create an element in the chart namespace
     * @param dom owner document
     * @param prefix prefix used for the chart namespace, may be null
     * @param name local name
     * @return the new element
     */
    private static Element chartElement(final Document dom, final String prefix, final String name) {
        return dom.createElementNS(CHART_NS, prefix == null ? name : prefix + ":" + name);
    }

    /**
     * Create a drawing solid fill element
     * @param dom owner document
     * @param color RGB hexadecimal color
     * @return the a:solidFill element
     */
    private static Element solidFill(final Document dom, final String color) {
        final Element fill = dom.createElementNS(DRAWING_NS, "a:solidFill");
        final Element rgb = (Element) fill.appendChild(dom.createElementNS(DRAWING_NS, "a:srgbClr"));
        rgb.setAttribute("val", color);
        return fill;
    }

    /**
     * Set the number of points declared in a chart cache
     * @param ptCount the ptCount element
     * @param count number of points
     */
    private static void setPointCount(final CTUnsignedInt ptCount, final int count) {
        ptCount.setVal(count);
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

            // keep the declared number of points consistent with the actual points
            final CTNumData yNumCache = ctPlotArea.getScatterChartList().get(0)
                    .getSerList().get(0).getYVal().getNumRef().getNumCache();
            setPointCount(xNumCache.isSetPtCount() ? xNumCache.getPtCount() : xNumCache.addNewPtCount(), values.size());
            setPointCount(yNumCache.isSetPtCount() ? yNumCache.getPtCount() : yNumCache.addNewPtCount(), values.size());
        }
        // otherwise we do not support other type for now
    }

}
