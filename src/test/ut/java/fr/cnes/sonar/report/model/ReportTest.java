package fr.cnes.sonar.report.model;

import fr.cnes.sonar.report.CommonTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ReportTest extends CommonTest {

    @Test
    public void ProjectBranchTest() {
        Report report = new Report();
        report.setProjectBranch("branch");
        Assert.assertEquals("branch", report.getProjectBranch());
    }

}
