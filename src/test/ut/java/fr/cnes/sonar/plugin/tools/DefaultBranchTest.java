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

package fr.cnes.sonar.plugin.tools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;
import org.sonarqube.ws.ProjectBranches.Branch;
import org.sonarqube.ws.ProjectBranches.Branch.Builder;
import org.sonarqube.ws.ProjectBranches.ListWsResponse;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.projectbranches.ListRequest;
import org.sonarqube.ws.client.projectbranches.ProjectBranchesService;

public class DefaultBranchTest {

    @Test
    public void testGetDefaultBranchFromProject() {

        // Create the request object
        String project = "whatever";

        // Create the fake list of Branch returned by Sonarqube WS
        Builder builder = Branch.newBuilder();
        Branch notMain = builder.setName("notMain").setIsMain(false).build();
        Branch main = builder.setName("main").setIsMain(true).build();
        Branch otherNotMain = builder.setName("otherNotMain").setIsMain(false).build();

        // Create the fake WsClient
        WsClient wsClient = Mockito.mock(WsClient.class);
        ProjectBranchesService projectBranchesService = Mockito.mock(ProjectBranchesService.class);
        ListWsResponse listWsResponse = ListWsResponse.newBuilder()
                .addBranches(0, notMain)
                .addBranches(1, main)
                .addBranches(2, otherNotMain)
                .build();
        Mockito.when(wsClient.projectBranches()).thenReturn(projectBranchesService);
        Mockito.when(projectBranchesService.list(Mockito.isA(ListRequest.class))).thenReturn(listWsResponse);

        // Check we get the correct branch
        assertEquals("main", DefaultBranch.getDefaultBranchFromProject(wsClient, project));
    }

}