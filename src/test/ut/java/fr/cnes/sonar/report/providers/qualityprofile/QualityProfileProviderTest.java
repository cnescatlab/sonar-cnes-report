package fr.cnes.sonar.report.providers.qualityprofile;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;

import org.junit.Test;

public class QualityProfileProviderTest extends CommonTest {

    private static final String TOKEN = "token";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetQualityProfilesStandalone() throws SonarQubeException, BadSonarQubeRequestException {
        QualityProfileProvider qualityProfileProvider = new QualityProfileProviderStandalone(sonarQubeServer, TOKEN, PROJECT_KEY);
        qualityProfileProvider.getQualityProfiles();
    }

    @Test(expected = IllegalStateException.class)
    public void executeFaultyGetQualityProfilesPlugin() throws SonarQubeException, BadSonarQubeRequestException {
        QualityProfileProvider qualityProfileProvider = new QualityProfileProviderPlugin(wsClient, PROJECT_KEY);
        qualityProfileProvider.getQualityProfiles();
    }
}