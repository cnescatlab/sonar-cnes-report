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
     * mockable resources providers
     */
    protected IssuesProvider issuesProvider;
    protected MeasureProvider measureProvider;
    protected QualityProfileProvider qualityProfileProvider;
    protected QualityGateProvider qualityGateProvider;

    /**
     * Setting of all stubbed resources before launching a test
     */
    @Before
    public void before() throws IOException, UnknownParameterException {
        // create stubbed params
        params = new Params();
        params.put("sonar.url", "http://sonarqube:9000");
        params.put("sonar.project.id", "www2");
        params.put("sonar.project.quality.gate", "CNES");
        params.put("project.name", "CNES.FR");
        params.put("report.author", "Benoît Garçon");
        params.put("report.date", new Date().toString());
        params.put("report.path", "./target");
        params.put("report.locale", "fr_FR");
        params.put("report.template", "src/main/resources/template/code-analysis-template.docx");
        params.put("issues.template", "src/main/resources/template/issues-template.xlsx");

        // create stubbed ws
        try (
                FileReader fileReader = new FileReader("src/test/it/fr/cnes/tests/ws.txt");
                BufferedReader br = new BufferedReader(fileReader)) {

            // read a couple of request/reponse
            String request = "", response = "";
            // to put it in a hashmap
            Map<String,String> stubbedResponses = new HashMap<>();

            while (response != null && (request = br.readLine()) != null) {
                response = br.readLine();
                stubbedResponses.put(request,response);
            }

            // create a mock
            RequestManager mockedRequestManager = mock(RequestManager.class);

            for(Map.Entry<String, String> entity : stubbedResponses.entrySet()) {
                when(mockedRequestManager.get(entity.getKey())).thenReturn(entity.getValue());
            }

            Report report = new Report();

            // instantiation of providers
            issuesProvider = new IssuesProvider(params, mockedRequestManager);
            measureProvider = new MeasureProvider(params, mockedRequestManager);
            qualityProfileProvider = new QualityProfileProvider(params, mockedRequestManager);
            qualityGateProvider = new QualityGateProvider(params, mockedRequestManager);

        }
    }
}
