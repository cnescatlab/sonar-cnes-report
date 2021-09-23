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
import fr.cnes.sonar.report.providers.component.ComponentProviderStandalone;
import fr.cnes.sonar.report.providers.facets.FacetsProvider;
import fr.cnes.sonar.report.providers.facets.FacetsProviderStandalone;
import fr.cnes.sonar.report.providers.issues.IssuesProvider;
import fr.cnes.sonar.report.providers.issues.IssuesProviderStandalone;
import fr.cnes.sonar.report.providers.language.LanguageProvider;
import fr.cnes.sonar.report.providers.language.LanguageProviderStandalone;
import fr.cnes.sonar.report.providers.measure.MeasureProvider;
import fr.cnes.sonar.report.providers.measure.MeasureProviderStandalone;
import fr.cnes.sonar.report.providers.project.ProjectProvider;
import fr.cnes.sonar.report.providers.project.ProjectProviderStandalone;
import fr.cnes.sonar.report.providers.qualitygate.QualityGateProvider;
import fr.cnes.sonar.report.providers.qualitygate.QualityGateProviderStandalone;
import fr.cnes.sonar.report.providers.qualityprofile.QualityProfileProvider;
import fr.cnes.sonar.report.providers.qualityprofile.QualityProfileProviderStandalone;
import fr.cnes.sonar.report.providers.securityhotspots.SecurityHotspotsProvider;
import fr.cnes.sonar.report.providers.securityhotspots.SecurityHotspotsProviderStandalone;
import fr.cnes.sonar.report.providers.sonarqubeinfo.SonarQubeInfoProvider;
import fr.cnes.sonar.report.providers.sonarqubeinfo.SonarQubeInfoProviderStandalone;

/**
 * Factory of providers for standalone mode
 */
public class StandaloneProviderFactory implements ProviderFactory {

    /**
     * URL of the SonarQube server
     */
    private String server;
    /**
     * Token to authenticate the user on the sonarqube server
     */
	private String token;
    /**
     * Key of the project
     */
	private String project;
    /**
     * Branch of the project
     */
    private String branch;    
	
    /**
     * Constructor.
     * @param server SonarQube server.
     * @param token User's token.
     * @param project Project's id.
     * @param branch Project's branch.
    */
	public StandaloneProviderFactory(String server, String token, String project, String branch){
		this.server = server;
		this.token = token;
		this.project = project;
		this.branch = branch;
	}

    @Override
    public ComponentProvider createComponentProvider() {
        return new ComponentProviderStandalone(this.server, this.token, this.project, this.branch);
    }

    @Override
    public FacetsProvider createFacetsProvider() {
        return new FacetsProviderStandalone(this.server, this.token, this.project, this.branch);
    }

    @Override
    public IssuesProvider createIssuesProvider() {
        return new IssuesProviderStandalone(this.server, this.token, this.project, this.branch);
    }

    @Override
    public LanguageProvider createLanguageProvider() {
        return new LanguageProviderStandalone(this.server, this.token, this.project);
    }
    
    @Override
    public MeasureProvider createMeasureProvider() {
        return new MeasureProviderStandalone(this.server, this.token, this.project, this.branch);
    }

    @Override
    public ProjectProvider createProjectProvider() {
        return new ProjectProviderStandalone(this.server, this.token, this.project, this.branch, createLanguageProvider());
    }

    @Override
    public QualityGateProvider createQualityGateProvider() {
        return new QualityGateProviderStandalone(this.server, this.token, this.project, this.branch);
    }

    @Override
    public QualityProfileProvider createQualityProfileProvider() {
        return new QualityProfileProviderStandalone(this.server, this.token, this.project);
    }

    @Override
    public SecurityHotspotsProvider createSecurityHotspotsProvider() {
        return new SecurityHotspotsProviderStandalone(this.server, this.token, this.project, this.branch);
    }

    @Override
    public SonarQubeInfoProvider createSonarQubeInfoProvider() {
        return new SonarQubeInfoProviderStandalone(this.server, this.token);
    }
}