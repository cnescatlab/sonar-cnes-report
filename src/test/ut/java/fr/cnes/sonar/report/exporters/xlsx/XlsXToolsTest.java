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

package fr.cnes.sonar.report.exporters.xlsx;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * Test the readability helpers of the xlsx export
 */
public class XlsXToolsTest extends CommonTest {

    private static final String TARGET = "./target/test";

    @Test
    public void displayWidthCountsWideCharactersTwiceTest() {
        Assert.assertEquals(5, XlsXTools.displayWidth("abcde"));
        Assert.assertEquals(8, XlsXTools.displayWidth("SQL 注入"));
        Assert.assertEquals(3, XlsXTools.displayWidth("ab\nabc"));
        Assert.assertEquals(0, XlsXTools.displayWidth(""));
    }

    @Test
    public void improveReadabilityTest() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            final XSSFSheet sheet = workbook.createSheet("Issues");
            XlsXTools.createRow(sheet, 0, Arrays.asList("Rule", "Message", "Severity"));
            final StringBuilder longMessage = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                longMessage.append("long message ");
            }
            XlsXTools.createRow(sheet, 1, Arrays.asList("squid:1", longMessage.toString(), "MAJOR"));
            XlsXTools.createRow(sheet, 2, Arrays.asList("squid:2", "short", "BLOCKER"));

            XlsXTools.improveReadability(workbook, Arrays.asList(sheet, null));

            // header row frozen
            Assert.assertNotNull(sheet.getPaneInformation());
            Assert.assertEquals(1, sheet.getPaneInformation().getHorizontalSplitPosition());
            // long texts wrapped in a capped column, short ones not wrapped
            Assert.assertEquals(60 * 256, sheet.getColumnWidth(1));
            Assert.assertTrue(sheet.getRow(1).getCell(1).getCellStyle().getWrapText());
            Assert.assertFalse(sheet.getRow(1).getCell(0).getCellStyle().getWrapText());
            Assert.assertEquals(9 * 256, sheet.getColumnWidth(0));
            // one rule per severity on the severity column
            Assert.assertEquals(5, sheet.getSheetConditionalFormatting().getNumConditionalFormattings());
            Assert.assertEquals("C2:C3", sheet.getSheetConditionalFormatting().getConditionalFormattingAt(0)
                    .getFormattingRanges()[0].formatAsString());
        }
    }

    @Test
    public void columnDefinitionsStaySortedTest() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            final XSSFSheet sheet = workbook.createSheet("Issues");
            // a template may already define the width of some columns
            sheet.setColumnWidth(1, 20 * 256);
            XlsXTools.createRow(sheet, 0, Arrays.asList("Rule", "Message", "Severity"));
            XlsXTools.createRow(sheet, 1, Arrays.asList("squid:1", "message", "MAJOR"));
            XlsXTools.improveReadability(workbook, Collections.singletonList(sheet));

            long previous = 0;
            for (org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol col
                    : sheet.getCTWorksheet().getColsArray(0).getColArray()) {
                Assert.assertTrue(col.getMin() > previous);
                previous = col.getMax();
            }
        }
    }

    @Test
    public void improveReadabilityWithoutDataTest() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            final XSSFSheet sheet = workbook.createSheet("Issues");
            XlsXTools.createRow(sheet, 0, Arrays.asList("Rule", "Severity"));
            XlsXTools.improveReadability(workbook, Collections.singletonList(sheet));
            Assert.assertEquals(0, sheet.getSheetConditionalFormatting().getNumConditionalFormattings());
        }
    }

    @Test
    public void traditionalChineseExportUsesLocalizedTemplateTest() throws Exception {
        new File(TARGET).mkdirs();
        final String output = TARGET + "/test_zh_TW.xlsx";
        try {
            StringManager.changeLocale("zh_TW");
            new XlsXExporter().export(report, output, "");
        } finally {
            StringManager.changeLocale(StringManager.getProperty(StringManager.DEFAULT_LANGUAGE));
        }
        try (Workbook workbook = WorkbookFactory.create(new File(output))) {
            Assert.assertEquals("Microsoft JhengHei", workbook.getFontAt(0).getFontName());
            // sheets are still found by their English names
            Assert.assertNotNull(workbook.getSheet("Issues"));
            Assert.assertTrue(workbook.getSheet("Issues").getLastRowNum() > 0);
        }
    }
}
