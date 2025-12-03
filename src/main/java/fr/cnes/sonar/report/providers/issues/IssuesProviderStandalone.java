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

package fr.cnes.sonar.report.providers.issues;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnsupportedSonarqubeResponseException;
import fr.cnes.sonar.report.model.Issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Provides issue items in standalone mode
 */
public class IssuesProviderStandalone extends AbstractIssuesProvider implements IssuesProvider {

    /**
     * Name of the request
     */
    private static final String GET_ISSUES_REQUEST = "GET_ISSUES_REQUEST";
    private static final String GET_COMPONENTS_LIST_REQUEST	= "GET_COMPONENTS_LIST_REQUEST";


    /**
     * Complete constructor.
     * 
     * @param pServer  SonarQube server.
     * @param pToken   String representing the user token.
     * @param pProject The id of the project to report.
     * @param pBranch  The branch of the project to report.
     * @param pEnableIssuesMultiRequests Workaround SonarQube 10'000 issues limitation, by multiple requests.
     * @param pMaxUrlSize                SonarQube WebAPI max URL text-size.
     */
    public IssuesProviderStandalone(final String pServer, final String pToken, final String pProject,
            final String pBranch,
            final boolean pEnableIssuesMultiRequests, final int pMaxUrlSize) {
        super(pServer, pToken, pProject, pBranch, pEnableIssuesMultiRequests, pMaxUrlSize);
    }

    @Override
    public List<Issue> getIssues()
            throws Exception, BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(CONFIRMED);
    }

    @Override
    public List<Issue> getUnconfirmedIssues()
            throws Exception, BadSonarQubeRequestException, SonarQubeException {
        return getIssuesByStatus(UNCONFIRMED);
    }

    /**
     * Get issues depending on their resolved status
     * 
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws Exception 
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server
     * @throws SonarQubeException           When SonarQube server is not callable.
     * @throws UnsupportedSonarqubeResponseException 
     */
    private List<Issue> getIssuesByStatus(String confirmed)
            throws Exception, BadSonarQubeRequestException, SonarQubeException, UnsupportedSonarqubeResponseException {
//        return getIssuesByStatusAbstract(confirmed);
    	final List<String> listOfComponentKeys = new ArrayList<String>(1);
    	listOfComponentKeys.add(getProjectKey());
       
    	final List<Issue> listOfUnconfirmedIssues = new ArrayList<Issue>();
    	
    	getIssuesByStatusAbstract_strategy(
        		confirmed, 
        		new ArrayList<Issue>(), 
        		listOfUnconfirmedIssues, 
        		new ArrayList<Map<String, String>>(), 
        		listOfComponentKeys, 
        		enableIssuesMultiRequests);
        return listOfUnconfirmedIssues;
    }

    @Override
    public List<Map<String, String>> getRawIssues() throws BadSonarQubeRequestException, SonarQubeException {
        return getRawIssuesAbstract();
    }
    
    @Override
	public void getIssuesStructures(
    		final String confirmed, 
    		final List<Issue> listOfConfirmedIssues, 
    		final List<Issue> listOfUnconfirmedIssues, 
    		final List<Map<String, String>> listOfMapOfIssues) 
    	throws Exception, BadSonarQubeRequestException, SonarQubeException, UnsupportedSonarqubeResponseException {
    	
    	final List<String> listOfComponentKeys = new ArrayList<String>(1);
    	listOfComponentKeys.add(getProjectKey());
    	
    	getIssuesByStatusAbstract_strategy(
        		confirmed, 
    			listOfConfirmedIssues, 
        		listOfUnconfirmedIssues, 
        		listOfMapOfIssues, 
        		listOfComponentKeys, 
        		enableIssuesMultiRequests
        		);
    }

    @Override
    protected JsonObject getIssuesAsJsonObject(final int page, final int maxPerPage, final String confirmed, 
    		final String additionalParams)
            throws BadSonarQubeRequestException, SonarQubeException {
        // prepare the server to get all the issues
        final String request = String.format(getRequest(GET_ISSUES_REQUEST), getServer(), getProjectKey(),
                getMetrics(ISSUES_FACETS), maxPerPage, page, getMetrics(ISSUES_ADDITIONAL_FIELDS), confirmed,
                getBranch(), additionalParams) + additionalParams;
        // perform the request to the server
        return request(request);
    }

    @Override
    protected JsonObject getComponentsAsJsonObject(final String componentID, final String strategy, final String qualifiers,
    		final int page, final int maxPerPage)
            throws BadSonarQubeRequestException, SonarQubeException {
        // prepare the server to get all the issues
        final String request = String.format(getRequest(GET_COMPONENTS_LIST_REQUEST), getServer(),
        		componentID, qualifiers, strategy,
        		maxPerPage, page,
        		getBranch());
        // perform the request to the server
        return request(request);
    }
    
}
