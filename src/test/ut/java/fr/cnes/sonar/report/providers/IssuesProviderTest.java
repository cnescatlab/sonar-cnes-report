package fr.cnes.sonar.report.providers;

import fr.cnes.sonar.report.CommonTest;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Facet;
import fr.cnes.sonar.report.model.Value;
import fr.cnes.sonar.report.utils.StringManager;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IssuesProviderTest extends CommonTest {

    private static final String TOKEN = "token";
    private int rankFacet;
    private int rankValue;
    private List<Facet> facets;
    private static final String TYPES = "types";

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetIssues() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getIssues();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetFacets() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getFacets();
    }

    @Test(expected = SonarQubeException.class)
    public void executeFaultyGetRawIssues() throws SonarQubeException, BadSonarQubeRequestException {
        IssuesProvider issuesProvider = new IssuesProvider(sonarQubeServer, TOKEN, PROJECT_KEY, BRANCH);
        issuesProvider.getRawIssues();
    }

    /**
     * Assert that security hotspots are well considered as a value in a facet
     */
    @Test
    public void getFacetsTest(){
        this.searchSecurityHotspotValue();
        Assert.assertEquals(5, this.facets.get(this.rankFacet).getValues().get(this.rankValue).getCount());
    }

    /**
     * Search for = the indexes of each facet and value for the facet's and value's lists
     */
    private void searchSecurityHotspotValue(){
        this.facets = this.report.getFacets();
        for(int i = 0; i < facets.size(); i ++){
            if(facets.get(i).getProperty().equals(TYPES)){
                this.rankFacet = i;
                List<Value> values = facets.get(i).getValues();
                for(int j = 0; j < values.size(); j ++){
                    if(values.get(j).getVal().equals(StringManager.HOTSPOT_TYPE)){
                        this.rankValue = j;
                        return;
                    }
                }
            }
        }
    }
}