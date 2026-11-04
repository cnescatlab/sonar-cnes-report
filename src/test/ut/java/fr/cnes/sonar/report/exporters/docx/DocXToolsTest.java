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

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exporters.ReportPalette;
import fr.cnes.sonar.report.model.TimeValue;
import fr.cnes.sonar.report.model.Value;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test the readability helpers of the docx export
 */
public class DocXToolsTest extends CommonTest {

    @Test
    public void sortByLabelFollowsReferenceOrderTest() {
        final List<Value> values = Arrays.asList(new Value("INFO", 1), new Value("UNKNOWN", 2),
                new Value("BLOCKER", 3), new Value("MAJOR", 4));
        final List<String> labels = DocXTools.sortByLabel(values, "BLOCKER,CRITICAL,MAJOR,MINOR,INFO")
                .stream().map(Value::getVal).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("BLOCKER", "MAJOR", "INFO", "UNKNOWN"), labels);
    }

    @Test
    public void isNumericColumnTest() {
        final List<List<String>> data = Arrays.asList(
                Arrays.asList("squid:1", "12", "90.3 %", "7d 1h", ""),
                Arrays.asList("squid:2", "1,024", "-66.7 %", "2h", ""));
        Assert.assertFalse(DocXTools.isNumericColumn(data, 0));
        Assert.assertTrue(DocXTools.isNumericColumn(data, 1));
        Assert.assertTrue(DocXTools.isNumericColumn(data, 2));
        Assert.assertFalse(DocXTools.isNumericColumn(data, 3));
        // only empty values: not numeric
        Assert.assertFalse(DocXTools.isNumericColumn(data, 4));
    }

    @Test
    public void fillTableFormatsRowsTest() throws Exception {
        try (InputStream template = getClass().getResourceAsStream(StringManager.getProperty("docx.template"));
             XWPFDocument document = new XWPFDocument(template)) {
            final List<List<String>> data = Arrays.asList(
                    Arrays.asList("Java", "1200"),
                    Arrays.asList("XML", "35"));
            // XWPFTable caches its text when built: find the table before filling it
            XWPFTable table = null;
            for (XWPFTable current : document.getTables()) {
                if (current.getText().contains("$VOLUME")) {
                    table = current;
                }
            }
            DocXTools.fillTable(document, Arrays.asList("Language", "Number"), data, "$VOLUME");

            Assert.assertNotNull(table);
            Assert.assertEquals(3, table.getNumberOfRows());
            // POI 4.1.2 writes w:val="true" but only reads back "on": check the XML itself
            Assert.assertTrue(table.getRow(0).getCtRow().xmlText().contains("tblHeader"));
            Assert.assertTrue(table.getRow(1).getCtRow().xmlText().contains("cantSplit"));
            Assert.assertFalse(table.getRow(1).getCtRow().xmlText().contains("tblHeader"));
            Assert.assertEquals(ParagraphAlignment.RIGHT,
                    table.getRow(1).getCell(1).getParagraphs().get(0).getAlignment());
            Assert.assertEquals(ParagraphAlignment.RIGHT,
                    table.getRow(0).getCell(1).getParagraphs().get(0).getAlignment());
            Assert.assertNotEquals(ParagraphAlignment.RIGHT,
                    table.getRow(1).getCell(0).getParagraphs().get(0).getAlignment());
        }
    }

    @Test
    public void chartsHaveConsistentPointsAndColorsTest() throws Exception {
        try (InputStream template = getClass().getResourceAsStream(StringManager.getProperty("docx.template"));
             XWPFDocument document = new XWPFDocument(template)) {
            for (XWPFChartSpace chart : XWPFChartSpace.getChartSpaces(document)) {
                final String title = chart.getTitle();
                if (title.contains("$SEVERITY")) {
                    chart.setValues(Arrays.asList(new Value("MAJOR", 4), new Value("UNKNOWN", 1),
                            new Value("BLOCKER", 2)), ReportPalette.severityColors());
                    final String xml = chart.getChart().getCTChart().xmlText();
                    Assert.assertTrue(xml.contains("<c:ptCount val=\"3\"/>"));
                    Assert.assertEquals(2, countOccurrences(xml, "<c:dPt>"));
                    Assert.assertTrue(xml.contains(ReportPalette.severityColors().get("MAJOR")));
                    Assert.assertTrue(xml.contains(ReportPalette.severityColors().get("BLOCKER")));
                } else if (title.contains("$VIOLATIONS")) {
                    final List<TimeValue> values = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        values.add(new TimeValue(i, String.valueOf(i)));
                    }
                    chart.setTimeValues(values);
                    final String xml = chart.getChart().getCTChart().xmlText();
                    Assert.assertEquals(2, countOccurrences(xml, "<c:ptCount val=\"5\"/>"));
                } else if (title.contains("$TYPE")) {
                    chart.setValues(Collections.singletonList(new Value("BUG", 1)), ReportPalette.typeColors());
                    final String xml = chart.getChart().getCTChart().xmlText();
                    Assert.assertEquals(1, countOccurrences(xml, "<c:dPt>"));
                }
            }
        }
    }

    private static int countOccurrences(final String text, final String part) {
        int count = 0;
        int index = text.indexOf(part);
        while (index >= 0) {
            count++;
            index = text.indexOf(part, index + part.length());
        }
        return count;
    }
}
