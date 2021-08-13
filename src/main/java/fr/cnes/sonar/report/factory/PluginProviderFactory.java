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

package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.providers.component.ComponentProvider;
import fr.cnes.sonar.report.providers.component.ComponentProviderPlugin;
import fr.cnes.sonar.report.providers.facets.FacetsProvider;
import fr.cnes.sonar.report.providers.facets.FacetsProviderPlugin;
import fr.cnes.sonar.report.providers.issues.IssuesProvider;
import fr.cnes.sonar.report.providers.issues.IssuesProviderPlugin;
import fr.cnes.sonar.report.providers.language.LanguageProvider;
import fr.cnes.sonar.report.providers.language.LanguageProviderPlugin;
import fr.cnes.sonar.report.providers.measure.MeasureProvider;
import fr.cnes.sonar.report.providers.measure.MeasureProviderPlugin;
import fr.cnes.sonar.report.providers.project.ProjectProvider;
import fr.cnes.sonar.report.providers.project.ProjectProviderPlugin;
import fr.cnes.sonar.report.providers.qualitygate.QualityGateProvider;
import fr.cnes.sonar.report.providers.qualitygate.QualityGateProviderPlugin;
import fr.cnes.sonar.report.providers.qualityprofile.QualityProfileProvider;
import fr.cnes.sonar.report.providers.qualityprofile.QualityProfileProviderPlugin;
import fr.cnes.sonar.report.providers.securityhotspots.SecurityHotspotsProvider;
import fr.cnes.sonar.report.providers.securityhotspots.SecurityHotspotsProviderPlugin;
import fr.cnes.sonar.report.providers.sonarqubeinfo.SonarQubeInfoProvider;
import fr.cnes.sonar.report.providers.sonarqubeinfo.SonarQubeInfoProviderPlugin;
import org.sonarqube.ws.client.WsClient;

/**
 * Factory of providers for plugin mode
 */
public class PluginProviderFactory implements ProviderFactory {

    /**
     * Key of the project
     */
    private String project;
    /**
     * Branch of the project
     */
    private String branch;
    /**
     * Client to talk with sonarqube's services
     */
	private WsClient wsClient;

    /**
     * 
     * @param project Project's id.
     * @param branch Project's branch.
     * @param wsClient SonarQube web client.
     */
	public PluginProviderFactory(String project, String branch, WsClient wsClient){
		this.project = project;
        this.branch = branch;
        this.wsClient = wsClient;
	}

    @Override
    public ComponentProvider createComponentProvider() {
        return new ComponentProviderPlugin(this.wsClient, this.project, this.branch);
    }

    @Override
    public FacetsProvider createFacetsProvider() {
        return new FacetsProviderPlugin(this.wsClient, this.project, this.branch);
    }

    @Override
    public IssuesProvider createIssuesProvider() {
        return new IssuesProviderPlugin(this.wsClient, this.project, this.branch);
    }

    @Override
    public LanguageProvider createLanguageProvider() {
        return new LanguageProviderPlugin(this.wsClient, this.project);
    }

    @Override
    public  MeasureProvider createMeasureProvider() {
        return new MeasureProviderPlugin(this.wsClient, this.project, this.branch);
    }

    @Override
    public ProjectProvider createProjectProvider() {
        return new ProjectProviderPlugin(this.wsClient, this.project, this.branch, createLanguageProvider());
    }

    @Override
    public QualityGateProvider createQualityGateProvider() {
        return new QualityGateProviderPlugin(this.wsClient, this.project, this.branch);
    }

    @Override
    public QualityProfileProvider createQualityProfileProvider() {
        return new QualityProfileProviderPlugin(this.wsClient, this.project);
    }

    @Override
    public SecurityHotspotsProvider createSecurityHotspotsProvider() {
        return new SecurityHotspotsProviderPlugin(this.wsClient, this.project, this.branch);
    }

    @Override
    public SonarQubeInfoProvider createSonarQubeInfoProvider() {
        return new SonarQubeInfoProviderPlugin(this.wsClient);
    }
}