package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportFactoryTest extends CommonTest {

    @Test
    public void createTest() throws XmlException, BadExportationDataTypeException, OpenXML4JException, IOException, ParseException {
        ReportFactory.report(conf, report);
        Assert.assertTrue(true);
    }

    @Test
    public void formatFilenameWithValidDate() throws ParseException {
        String actual = ReportFactory.formatFilename("report.output", "./target", "2021-03-31", "CNES Report");
        String expected = "./target/2021-03-31-CNES Report-analysis-report.docx";
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void formatFilenameWithEmptyDate() throws ParseException {
        String actual = ReportFactory.formatFilename("report.output", "./target", "", "CNES Report");
        String expected = String.format("./target/%s-CNES Report-analysis-report.docx", new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()));
        Assert.assertEquals(actual, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatFilenameWithBadDateFormat() throws ParseException {
        ReportFactory.formatFilename("report.output", "./target", "30-03-2021", "CNES Report");
    }

    @Test(expected = ParseException.class)
    public void formatFilenameWithInconsistentDate() throws ParseException {
        ReportFactory.formatFilename("report.output", "./target", "2021-12-32", "CNES Report");
    }
}
