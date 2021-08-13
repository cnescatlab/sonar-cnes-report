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

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.Components;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.providers.component.ComponentProvider;
import fr.cnes.sonar.report.providers.facets.FacetsProvider;
import fr.cnes.sonar.report.providers.issues.IssuesProvider;
import fr.cnes.sonar.report.providers.measure.MeasureProvider;
import fr.cnes.sonar.report.providers.project.ProjectProvider;
import fr.cnes.sonar.report.providers.qualitygate.QualityGateProvider;
import fr.cnes.sonar.report.providers.qualityprofile.QualityProfileProvider;
import fr.cnes.sonar.report.providers.securityhotspots.SecurityHotspotsProvider;

/**
 * Construct the report from resources providers.
 */
public class ReportModelFactory {

    /**
     * Id of the project to report.
     */
    private String project;
    /**
     * Branch of the project to report.
     */
    private String branch;
    /**
     * Author of the project to report.
     */
    private String author;
    /**
     * Date of the reporting.
     */
    private String date;
    /**
     * Factory used to create providers.
     */
    private ProviderFactory providerFactory;

    /**
     * Complete constructor
     * @param project the project's id
     * @param branch the project's branch
     * @param author the project's author
     * @param date the project's date
     * @param providerFactory the provider factory
     */
    public ReportModelFactory(final String project, final String branch, final String author, final String date, final ProviderFactory providerFactory) {
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.date = date;
        this.providerFactory = providerFactory;
    }

    /**
     * Create a report from program resources
     * @return A complete report resources model
     * @throws BadSonarQubeRequestException when a request to the server is not well-formed
     * @throws UnknownQualityGateException a quality gate is not correct
     * @throws SonarQubeException When an error occurred from SonarQube server.
     */
    public Report create() throws BadSonarQubeRequestException, UnknownQualityGateException, SonarQubeException {
        // the new report to return
        final Report report = new Report();

        final IssuesProvider issuesProvider = this.providerFactory.createIssuesProvider();
        final FacetsProvider facetsProvider = this.providerFactory.createFacetsProvider();
        final SecurityHotspotsProvider securityHotspotsProvider = this.providerFactory.createSecurityHotspotsProvider();
        final MeasureProvider measureProvider = this.providerFactory.createMeasureProvider();
        final ProjectProvider projectProvider = this.providerFactory.createProjectProvider();
        final QualityProfileProvider qualityProfileProvider = this.providerFactory.createQualityProfileProvider();
        final QualityGateProvider qualityGateProvider = this.providerFactory.createQualityGateProvider();
        final ComponentProvider componentProvider = this.providerFactory.createComponentProvider();

        // author's setting
        report.setProjectAuthor(this.author);
        // date setting
        report.setProjectDate(this.date);

        if(!projectProvider.hasProject(this.project, this.branch)) {
            throw new SonarQubeException(String.format("Unknown project '%s' on SonarQube instance.", this.project));
        }

        // measures's setting
        report.setMeasures(measureProvider.getMeasures());
        // metrics' by component setting
        final Components components = componentProvider.getComponents();
        report.setComponents(components.getComponentsList());
        report.setMetricsStats(components.getMetricStats());
        // set report basic data
        report.setProject(projectProvider.getProject(this.project, this.branch));
        // project's name's setting
        report.setProjectName(report.getProject().getName());
        // formatted issues, unconfirmed issues and raw issues' setting
        report.setIssues(issuesProvider.getIssues());
        report.setUnconfirmed(issuesProvider.getUnconfirmedIssues());
        report.setRawIssues(issuesProvider.getRawIssues());
        // facets's setting
        report.setFacets(facetsProvider.getFacets());
        report.setTimeFacets(facetsProvider.getTimeFacets());
        // security hotspots to review
        report.setToReviewSecurityHotspots(securityHotspotsProvider.getToReviewSecurityHotspots());
        // reviewed security hotspots
        report.setReviewedSecurityHotspots(securityHotspotsProvider.getReviewedSecurityHotspots());
        // quality profile's setting
        report.setQualityProfiles(qualityProfileProvider.getQualityProfiles());
        // quality gate's setting
        report.setQualityGate(qualityGateProvider.getProjectQualityGate());
        // quality gate's status
        report.setQualityGateStatus(qualityGateProvider.getQualityGateStatus());

        return report;
    }

}
