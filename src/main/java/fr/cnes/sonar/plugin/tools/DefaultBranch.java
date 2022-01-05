package fr.cnes.sonar.plugin.tools;

import java.util.List;

import org.sonarqube.ws.ProjectBranches.Branch;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.projectbranches.ListRequest;

/**
 * utility class used to determine the MAIN branch of a project 
 */
public class DefaultBranch {

    private DefaultBranch(){}

    /**
     * Method used to retrieve the MAIN branch from a specified project
     * @param wsClient Class needed to interact with SonarQube classes
     * @param project Project from which we want the MAIN branch
     * @return The MAIN branch of the given project
     */
    public static String getDefaultBranchFromProject(WsClient wsClient, String project) {

        ListRequest request = new ListRequest().setProject(project);
        List<Branch> branchesList = wsClient.projectBranches().list(request).getBranchesList();        

        String defaultBranch = "";

        /** In theory, no need to check if the following condition is matched at least once
         * because SonarQube always defines a MAIN branch inside a project
         * even if the project is empty
         */
        for(Branch branch: branchesList) {
            if(branch.getIsMain()) {
                defaultBranch = branch.getName();
            }
        }

        return defaultBranch;
    }
    
}
