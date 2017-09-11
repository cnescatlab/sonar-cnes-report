package fr.cnes.tests;

import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import org.junit.Before;

import java.io.IOException;
import java.util.Date;

/**
 * Contains common code for tests
 * @author lequal
 */
public class MasterTest {

    /**
     * stubbed parameters for testing
     */
    protected Params params;

    /**
     * Setting of all stubbed resources before launching a test
     */
    @Before
    public void before() throws IOException, UnknownParameterException {
        // create stubbed params
        params = new Params();
        params.put("sonar.url", "http://sonarqube:9000");
        params.put("sonar.project.id", "cat");
        params.put("report.author", "Benoît Garçon");
        params.put("report.date", new Date().toString());
        params.put("report.path", "./target");
        params.put("report.locale", "fr_FR");
        params.put("report.template", "src/main/resources/template/cnes-code-analysis-template.docx");
        params.put("issues.template", "src/main/resources/template/issues-template.xlsx");

    }
}
