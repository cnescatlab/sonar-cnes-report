package fr.cnes.tests;

import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.providers.*;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contains common code for tests
 * @author begarco
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
        params.put("sonar.project.quality.gate", "CNES");
        params.put("project.name", "Code Analysis Tool");
        params.put("report.author", "Benoît Garçon");
        params.put("report.date", new Date().toString());
        params.put("report.path", "./target");
        params.put("report.locale", "fr_FR");
        params.put("report.template", "src/main/resources/template/cnes-code-analysis-template.docx");
        params.put("issues.template", "src/main/resources/template/issues-template.xlsx");

    }
}
