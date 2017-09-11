import fr.cnes.sonar.report.model.Report;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Check Report class
 * @author lequal
 */
public class ReportTest {

    /**
     *  Simple text for testing messages
     */
    private static final String TEST_STRING = "This is a test string.";

    private Report report;

    /**
     * Executed each time before running a single test
     */
    @Before
    public void prepare() {
        report = new Report();
    }

    /**
     * Check the default values
     */
    @Test
    public void defaultReportValuesTest() {
        assertEquals("", report.getProjectAuthor());
        assertEquals("", report.getProjectDate());
        assertEquals("", report.getProjectName());
        assertEquals("", report.getQualityProfilesFilename());
        assert(report.getRawIssues().isEmpty());
        assert(report.getQualityProfiles().isEmpty());
        assert(report.getFacets().isEmpty());
        assert(report.getIssues().isEmpty());
        assert(report.getMeasures().isEmpty());
    }

}
