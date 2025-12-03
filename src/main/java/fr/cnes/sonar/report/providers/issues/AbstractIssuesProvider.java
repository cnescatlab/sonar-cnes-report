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

import fr.cnes.sonar.report.providers.AbstractDataProvider;
import fr.cnes.sonar.report.utils.ListUtils;
import fr.cnes.sonar.report.utils.StringManager;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnsupportedSonarqubeResponseException;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Rule;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.sonarqube.ws.client.WsClient;

/**
 * Contains common code for issues providers
 */
public abstract class AbstractIssuesProvider extends AbstractDataProvider {

    /**
     * Correspond to the maximum number of issues that SonarQube allow
     * web api's users to collect.
     */
    private static final int MAXIMUM_ISSUES_LIMIT = 10000;
    /**
     * Value of the field to get confirmed issues
     */
    protected static final String CONFIRMED = "false";
    /**
     * Value of the field to get unconfirmed issues
     */
    protected static final String UNCONFIRMED = "true";
    /**
     * Parameter "issues" of the JSON response
     */
    private static final String ISSUES = "issues";
    /**
     * Parameter corresponding to Files in the JSON response
     */
    private static final String COMPONENTS = "components";
    /**
     * Name of the SonarQube facets to retrieve from issues
     */
    protected static final String ISSUES_FACETS = "ISSUES_FACETS";
    /**
     * Name of the SonarQube additional fields to retrieve from issues
     */
    protected static final String ISSUES_ADDITIONAL_FIELDS = "ISSUES_ADDITIONAL_FIELDS";

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
    protected AbstractIssuesProvider(final String pServer, final String pToken,
    		final String pProject, final String pBranch,
            final boolean pEnableIssuesMultiRequests, final int pMaxUrlSize) {
        super(pServer, pToken, pProject, pBranch, pEnableIssuesMultiRequests, pMaxUrlSize);
    }

    /**
     * Complete constructor.
     * 
     * @param wsClient The web client.
     * @param project  The id of the project to report.
     * @param branch   The branch of the project to report.
     */
    protected AbstractIssuesProvider(final WsClient wsClient, final String project, final String branch) {
        super(wsClient, project, branch);
    }

    /**
     * Generic getter for issues depending on their resolved status
     * 
     * @param confirmed equals "true" if Unconfirmed and "false" if confirmed
     * @return List containing all the issues
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    protected List<Issue> getIssuesByStatusAbstract(final String confirmed)
            throws BadSonarQubeRequestException, SonarQubeException {
        // results variable
        final List<Issue> res = new ArrayList<>();

        // stop condition
        boolean goOn = true;
        // flag when there are too many violation (> MAXIMUM_ISSUES_LIMIT)
        boolean overflow = false;
        // current page
        int page = 1;

        // temporary declared variable to contain data from ws
        Issue[] issuesTemp;
        Rule[] rulesTemp;

        // search all issues of the project
        while (goOn) {
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
            final JsonObject jo = getIssuesAsJsonObject(page, maxPerPage, confirmed, "");
            // transform json to Issue and Rule objects
            issuesTemp = (getGson().fromJson(jo.get(ISSUES), Issue[].class));
            rulesTemp = (getGson().fromJson(jo.get(RULES), Rule[].class));
            // association of issues and languages
            setIssuesLanguage(issuesTemp, rulesTemp);
            // add them to the final result
            res.addAll(Arrays.asList(issuesTemp));
            // check next results' pages
            int number = (jo.get(TOTAL).getAsInt());

            // check overflow
            if (number > MAXIMUM_ISSUES_LIMIT) {
                number = MAXIMUM_ISSUES_LIMIT;
                overflow = true;
            }
            goOn = page * maxPerPage < number;
            page++;
        }

        // in case of overflow we log the problem
        if (overflow) {
            String message = StringManager.string(StringManager.ISSUES_OVERFLOW_MSG);
            LOGGER.warning(message);
        }

        // return the issues
        return res;
    }

    /**
     * Generic getter for issues depending on their resolved status
     * 
     * @param confirmed               equals "true" if Unconfirmed and "false" if confirmed
     * @param listOfConfirmedIssues   List to fill with confirmed Issues
     * @param listOfUnconfirmedIssues List to fill with unconfirmed Issues
     * @param listOfMapOfIssues       List to fill with Issues, each Issue is represented as Map containing Issue parameters
     * @param listOfComponentKeys     List of Components's keys from which to retrieve associated Issues
     * @param recOnSubComps           Workaround SonarQube 10'000 issues limitation, using multiple requests.
     * 
     * @return void
     * 
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server
     * @throws SonarQubeException           When SonarQube server is not callable.
     * @throws UnsupportedSonarqubeResponseException
     */
    protected void getIssuesByStatusAbstract_strategy(
    		final String confirmed, 
    		final List<Issue> listOfConfirmedIssues, 
    		final List<Issue> listOfUnconfirmedIssues, 
    		final List<Map<String, String>> listOfMapOfIssues, 
    		final List<String> listOfComponentKeys, 
    		final boolean recOnSubComps
    		) throws Exception, BadSonarQubeRequestException, SonarQubeException, UnsupportedSonarqubeResponseException {
		final List<List<String>> subsetsOfListsOfComponentKeys = ListUtils.splitListOfStrings(listOfComponentKeys, maxUrlSize, 1);
		
		for (final List<String> subListOfComponentKeys : subsetsOfListsOfComponentKeys) {	
	    	// flag when there are too many violation (> MAXIMUM_ISSUES_LIMIT)
	        final MutableBoolean overflow = new MutableBoolean(false);
	        
	        getIssuesByStatusAbstract_oneRequest(confirmed, listOfConfirmedIssues, listOfUnconfirmedIssues, listOfMapOfIssues, subListOfComponentKeys, overflow, !recOnSubComps);
	        
	        // in case of overflow we log the problem
	        if (overflow.isTrue()) {
	        	if (!recOnSubComps) {
	        		String message = StringManager.string(StringManager.ISSUES_OVERFLOW_MSG);
	                LOGGER.warning(message);
	        	} else {
	        		final List<List<String>> listOfCompsSubsets = new ArrayList<List<String>>();
	        		
	        		if (subListOfComponentKeys.size() < 1) {
	        			// do nothing
	        		} else if (subListOfComponentKeys.size() == 1) {
	        			// get sub-components
	        			final Map<String, String> subComps = getSubComponents(subListOfComponentKeys.get(0), "children", "FIL,DIR");
	        			// check 
	        			if (subComps.isEmpty()) {
		        			String errMessage = StringManager.string(StringManager.FILES_OVERFLOW_MSG) +" : for ComponentKey=\""+ subListOfComponentKeys.get(0) +"\"";
		        			throw new UnsupportedSonarqubeResponseException(errMessage);
		        		}
	        			// separate Components's keys according to their qualifier
	        			final List<String> subComps_FIL = new ArrayList<String>();
	        			final List<String> subComps_DIR = new ArrayList<String>();
	        			for (final Map.Entry<String, String> currCompEntry : subComps.entrySet()) {
	        				final String currCompQualifier = currCompEntry.getValue();
	        				if (currCompQualifier.equals("FIL")) {
	        					subComps_FIL.add(currCompEntry.getKey());
	        				} else if (currCompQualifier.equals("DIR")) {
	        					subComps_DIR.add(currCompEntry.getKey());
	        				} else {
	        					LOGGER.warning("Component qualifier \""+ currCompQualifier +"\", not supported.");
	        				}
	        			}
	        			// split list of sub-compskeys in half
	        			// the goal here is to split but not too much
	        			if (subComps_FIL.isEmpty() && subComps_DIR.isEmpty()) {
	        				// do nothing
	        			} else if (subComps_FIL.isEmpty()) {
	        				// split the only list we got
	        				listOfCompsSubsets.addAll(ListUtils.splitListOfStrings(subComps_DIR));
	        			} else if (subComps_DIR.isEmpty()) {
	        				// split the only list we got
	        				listOfCompsSubsets.addAll(ListUtils.splitListOfStrings(subComps_FIL));
	        			} else {
	        				// split is the separation of FIL & DIR Components
	        				listOfCompsSubsets.add(subComps_FIL);
	        				listOfCompsSubsets.add(subComps_DIR);
	        			}
	        		} else {
	        			// split list of sub-compskeys in half
	        			listOfCompsSubsets.addAll(ListUtils.splitListOfStrings(subListOfComponentKeys));
	        		}
	        		
	        		for (final List<String> compsSubset : listOfCompsSubsets) {
		        			getIssuesByStatusAbstract_strategy(
		        					confirmed, 
		        					listOfConfirmedIssues, 
				        	    	listOfUnconfirmedIssues, 
				        	    	listOfMapOfIssues, 
				        	    	compsSubset, 
				        	    	recOnSubComps);
	        		}
	        	}
	        }
    	}
    }
    
    /**
     * Generic getter for issues depending on their resolved status
     * 
     * @param confirmed               equals "true" if Unconfirmed and "false" if confirmed
     * @param listOfConfirmedIssues   List to fill with confirmed Issues
     * @param listOfUnconfirmedIssues List to fill with unconfirmed Issues
     * @param listOfMapOfIssues       List to fill with Issues, each Issue is represented as Map containing Issue parameters
     * @param listOfComponentKeys     List of Components's keys from which to retrieve associated Issues
     * @param overflow                This flag will be set True when there are too many violation (> MAXIMUM_ISSUES_LIMIT)
     * @param continueOnError         To continue requesting next pages, even when exceeding MAXIMUM_ISSUES_LIMIT
     * 
     * @return List containing all the issues
     * 
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    protected void getIssuesByStatusAbstract_oneRequest(
    		final String confirmed, 
    		final List<Issue> listOfConfirmedIssues, 
    		final List<Issue> listOfUnconfirmedIssues, 
    		final List<Map<String, String>> listOfMapOfIssues, 
    		final List<String> listOfComponentKeys,
    		final MutableBoolean overflow,
    		final boolean continueOnError
    		) throws BadSonarQubeRequestException, SonarQubeException {	
    	
    	// get maximum number of results per page
        final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
        
        // multi-pages stop condition
        boolean goOn = true;
        
        //
        final String segmentationParam = componentKeys(listOfComponentKeys);
        
        // search all issues of the project
        for (int page = 1; goOn; page++) {
        	final JsonObject jo = getIssuesAsJsonObject(page, maxPerPage, confirmed, segmentationParam);
            
        	// transform json to Issue and Rule objects
            final Issue[] issuesTemp = (getGson().fromJson(jo.get(ISSUES), Issue[].class));
            final Rule[]  rulesTemp  = (getGson().fromJson(jo.get(RULES), Rule[].class));
            // association of issues and languages
            setIssuesLanguage(issuesTemp, rulesTemp);
            
            if (null != listOfMapOfIssues) {
            	// transform json to Issue objects
            	final Map<String, String>[] tmp = (getGson().fromJson(jo.get(ISSUES), Map[].class));
            	// add them to the final result
            	if (null != tmp) {
            		listOfMapOfIssues.addAll(Arrays.asList(tmp));
            	}
            }
            
            // check next results' pages
            int number = jo.get(TOTAL).getAsInt();

            // check overflow
            if (number > MAXIMUM_ISSUES_LIMIT) {
            	// when WebAPI-pages Overflow
                number = MAXIMUM_ISSUES_LIMIT;
                overflow.setValue(true);
                
                if (!continueOnError) {
                	// no need to continue iterating on other pages for this Component
                	// ignore issues found in current page
                	break;
                } else {
                	// store issues found in current page
                	// and continue iterating on other pages for this Component
                	if (Boolean.parseBoolean(confirmed)) {
                		if (null != listOfUnconfirmedIssues) {
                			listOfUnconfirmedIssues.addAll(Arrays.asList(issuesTemp));
                		} else {
                			// do nothing
                		}
                	} else {
                		if (null != listOfConfirmedIssues) {
                			listOfConfirmedIssues.addAll(Arrays.asList(issuesTemp));
                		} else {
                			// do nothing
                		}
                	}
                }
            } else {
            	// when all results fit in the WebAPI-pages
            	if (Boolean.parseBoolean(confirmed)) {
            		if (null != listOfUnconfirmedIssues) {
            			listOfUnconfirmedIssues.addAll(Arrays.asList(issuesTemp));
            		} else {
            			// do nothing
            		}
            	} else {
            		if (null != listOfConfirmedIssues) {
            			listOfConfirmedIssues.addAll(Arrays.asList(issuesTemp));
            		} else {
            			// do nothing
            		}
            	}
            }
            
            goOn = (page * maxPerPage) < number;
        }
    }
    
    /**
     * Converts a List of ComponentKeys
     * into a String for SonarQube WebAPI ComponentKey's parameter query-section
     * 
     * @param listOfComponentKeys
     * @return String
     */
    private String componentKeys(final List<String> listOfComponentKeys) {
    	final String strComponentKeys = String.join(",", listOfComponentKeys);
    	return "&componentKeys="+ strComponentKeys;
    }
    
    /**
     * Generic getter for sub-Components's key & qualifier
     * 
     * @param componentID  Compliant with SonarQube WebAPI specs
     * @param strategy     Compliant with SonarQube WebAPI specs
     * @param qualifiers   Compliant with SonarQube WebAPI specs
     * 
     * @return Map of found sub-components's keys and their Qualifier
     * 
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server
     * @throws SonarQubeException           When SonarQube server is not callable.
     * @throws UnsupportedSonarqubeResponseException
     */
    protected Map<String, String> getSubComponents(final String componentID, final String strategy, final String qualifiers)
            throws BadSonarQubeRequestException, SonarQubeException, UnsupportedSonarqubeResponseException {
    	final Map<String, String> mapOfSubComponents = new HashMap<String, String>();
    	
    	// stop condition
        boolean goOn = true;
        // flag when there are too many results (> MAXIMUM_ISSUES_LIMIT)
        boolean overflow = false;
        
        // get maximum number of results per page
        final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
        
        // search sub-components
        for (int page = 1; goOn; page++) {
        	
        	final JsonObject jo = getComponentsAsJsonObject(componentID, strategy, qualifiers, page, maxPerPage);
            // transform json to String FileIDs
        	final JsonElement jsonElemFILES = jo.get(COMPONENTS);
        	for (final JsonElement jsonElemFILE : jsonElemFILES.getAsJsonArray().asList()) {
        		if (jsonElemFILE instanceof JsonObject) {
        			final Map<String, JsonElement> file = ((JsonObject) jsonElemFILE).asMap();
        			final JsonElement fileKey = file.get("key");
        			final JsonElement fileQualifier = file.get("qualifier");
        			
        			mapOfSubComponents.put(fileKey.getAsString(), fileQualifier.getAsString());
        		} else {
        			String message = "SonarQube WebAPI result not supported : \""+ jsonElemFILE.getClass().getName() +"\"";
        			throw new UnsupportedSonarqubeResponseException(message);
        		}
        	}

            // check next results' pages
            int number = (jo.get("paging").getAsJsonObject().get(TOTAL).getAsInt());

            // check overflow
            if (number > MAXIMUM_ISSUES_LIMIT) {
                number = MAXIMUM_ISSUES_LIMIT;
                overflow = true;
                // from now, pages iteration continues, but we won't get all files
            }
            
            goOn = page * maxPerPage < number;
        }

        // in case of overflow we log the problem
        if (overflow) {
            String message = StringManager.string(StringManager.FILES_OVERFLOW_MSG);
            LOGGER.warning(message);
            throw new UnsupportedSonarqubeResponseException(message);
        }

        // return the Files
        return mapOfSubComponents;
    }

    /**
     * Generic getter for all the issues of a project in a raw format (map)
     * 
     * @return Array containing all the issues as maps
     * 
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    protected List<Map<String, String>> getRawIssuesAbstract() throws BadSonarQubeRequestException, SonarQubeException {
        // results variable
        final List<Map<String, String>> res = new ArrayList<>();

        // stop condition
        boolean goon = true;
        // flag when there are too many violation (> MAXIMUM_ISSUES_LIMIT)
        boolean overflow = false;
        // current page
        int page = 1;

        // search all issues of the project
        while (goon) {
            // get maximum number of results per page
            final int maxPerPage = Integer.parseInt(getRequest(MAX_PER_PAGE_SONARQUBE));
            final JsonObject jo = getIssuesAsJsonObject(page, maxPerPage, CONFIRMED, ALLFILES);
            // transform json to Issue objects
            final Map<String, String>[] tmp = (getGson().fromJson(jo.get(ISSUES), Map[].class));
            // add them to the final result
            res.addAll(Arrays.asList(tmp));
            // check next results' pages
            int number = (jo.get(TOTAL).getAsInt());

            // check overflow
            if (number > MAXIMUM_ISSUES_LIMIT) {
                number = MAXIMUM_ISSUES_LIMIT;
                overflow = true;
            }

            goon = page * maxPerPage < number;
            page++;
        }

        // in case of overflow we log the problem
        if (overflow) {
            String message = StringManager.string(StringManager.ISSUES_OVERFLOW_MSG);
            LOGGER.warning(message);
        }

        // return the issues
        return res;
    }

    /**
     * Find the display name of the programming language corresponding
     * to a rule with its key
     * 
     * @param ruleKey key of the rule to find
     * @param rules   array of the rules to browse
     * 
     * @return a String containing the display name of the programming language
     */
    private String findLanguageOf(String ruleKey, Rule[] rules) {
        // stop condition for the main loop
        boolean again = true;
        // increment for browsing the array
        int inc = 0;

        // result to return
        String language = "";

        // we iterate on the array until we find the good key
        while (again && inc < rules.length) {
            if (ruleKey.equals(rules[inc].getKey())) {
                again = false;
                language = rules[inc].getLangName();
            }
            inc++;
        }

        return language;
    }

    /**
     * Set the language of each issues
     * 
     * @param issues an array of issues to set
     * @param rules  an array of rules containing language information
     */
    private void setIssuesLanguage(Issue[] issues, Rule[] rules) {
        // rule's key of an issue
        String rulesKey;
        // language of the previous rule's key
        String rulesLanguage;

        // for each issue we associate the corresponding programming language
        // by browsing the rules array
        for (Issue issue : issues) {
            rulesKey = issue.getRule();
            rulesLanguage = findLanguageOf(rulesKey, rules);
            issue.setLanguage(rulesLanguage);
        }
    }

    /**
     * Get a JsonObject from the response of a search issues request.
     * 
     * @param page       The current page.
     * @param maxPerPage The maximum page size.
     * @param confirmed  Equals "true" if Unconfirmed and "false" if confirmed.
     * @param files      Scope focus on a list of source file. According to SonarWebAPI, Separator is char ','.
     * 
     * @return The response as a JsonObject.
     * 
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server.
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    protected abstract JsonObject getIssuesAsJsonObject(final int page, final int maxPerPage, final String confirmed, final String additionalParams)
            throws BadSonarQubeRequestException, SonarQubeException;

    /**
     * Get a JsonObject response from the request for Components (FILE, DIR, ...).
     * 
     * @param componentID 
     * @param strategy    Strategy to search for base component descendants. (For details, please report to SonarQube's Web-API documentation)
     * @param qualifiers  Comma-separated list of the requested qualifiers. (For details, please report to SonarQube's Web-API documentation)
     * @param page        The current page.
     * @param maxPerPage  The maximum page size.
     * 
     * @return The response as a JsonObject.
     * 
     * @throws BadSonarQubeRequestException A request is not recognized by the
     *                                      server.
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    protected abstract JsonObject getComponentsAsJsonObject(final String componentID, final String strategy, final String qualifiers, final int page, final int maxPerPage)
            throws BadSonarQubeRequestException, SonarQubeException;
    
}