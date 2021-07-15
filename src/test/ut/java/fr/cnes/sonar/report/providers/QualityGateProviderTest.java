package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.providers.qualitygate.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

public class QualityGateProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetQualityGates() throws SonarQubeException, BadSonarQubeRequestException {
        QualityGateProvider qualityGateProvider = new QualityGateProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        qualityGateProvider.getQualityGates();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetQualityGateStatus() throws SonarQubeException, BadSonarQubeRequestException {
        QualityGateProvider qualityGateProvider = new QualityGateProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        qualityGateProvider.getQualityGateStatus();
    }

    @Test
    public void getErrorExplanationTest() {
        QualityGateProviderWrapper qualityGateProvider = new QualityGateProviderWrapper(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);

        List<String> actual = new ArrayList<>();
        actual.add(qualityGateProvider.getErrorExplanationPublic("5", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("4", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("3", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("2", "1", "GT", "RATING"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("30", "0", "GT", "WORK_DUR"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("50.314", "80", "LT", "PERCENT"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("10000", "5000", "GT", "MILLISEC"));
        actual.add(qualityGateProvider.getErrorExplanationPublic("3", "0", "GT", "INT"));

        List<String> expected = new ArrayList<>();
        expected.add(" (E is worse than A)");
        expected.add(" (D is worse than A)");
        expected.add(" (C is worse than A)");
        expected.add(" (B is worse than A)");
        expected.add(" (0d 0h 30min is greater than 0d 0h 0min)");
        expected.add(" (50.3% is less than 80%)");
        expected.add(" (10000ms is greater than 5000ms)");
        expected.add(" (3 is greater than 0)");

        assertEquals(expected, actual);
    }

     /**
     * Wrapper on QualityGateProvider for testing purposes
     */
    private class QualityGateProviderWrapper extends QualityGateProviderStandalone {

        public QualityGateProviderWrapper(final String server, final String token, final String project,
            final String branch) {
            super(server, token, project, branch);
        }

        public String getErrorExplanationPublic(String actualValue, String errorThreshold, String comparator, String type) {
            return getErrorExplanation(actualValue, errorThreshold, comparator, type);
        }
    }
}