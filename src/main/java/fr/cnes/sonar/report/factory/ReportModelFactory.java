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
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.providers.*;
import fr.cnes.sonar.report.utils.ReportConfiguration;

/**
 * Construct the report from resources providers.
 */
public class ReportModelFactory {

    /**
     * SonarQube server.
     */
    private SonarQubeServer server;
    /**
     * Token of the SonarQube user.
     */
    private String token;
    /**
     * Id of the project to report.
     */
    private String project;
    /**
     * Author of the project to report.
     */
    private String author;
    /**
     * Date of the reporting.
     */
    private String date;

    /**
     * Complete constructor
     * @param pServer Value for SQ server.
     * @param pConfiguration Contains report configuration.
     */
    public ReportModelFactory(final SonarQubeServer pServer, final ReportConfiguration pConfiguration) {
        this.server = pServer;
        this.token = pConfiguration.getToken();
        this.project = pConfiguration.getProject();
        this.author = pConfiguration.getAuthor();
        this.date = pConfiguration.getDate();
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

        // instantiation of providers
        final ProviderFactory providerFactory = new ProviderFactory(this.server, this.token, this.project);
        final IssuesProvider issuesProvider = providerFactory.create(IssuesProvider.class);
        final MeasureProvider measureProvider = providerFactory.create(MeasureProvider.class);
        final ProjectProvider projectProvider = providerFactory.create(ProjectProvider.class);
        final QualityProfileProvider qualityProfileProvider = providerFactory.create(QualityProfileProvider.class);
        final QualityGateProvider qualityGateProvider = providerFactory.create(QualityGateProvider.class);
        final LanguageProvider languageProvider = providerFactory.create(LanguageProvider.class);
        final ComponentProvider componentProvider = providerFactory.create(ComponentProvider.class);

        // author's setting
        report.setProjectAuthor(this.author);
        // date setting
        report.setProjectDate(this.date);

        if(!projectProvider.hasProject(this.project)) {
            throw new SonarQubeException(String.format("Unknown project '%s' on SonarQube instance.", this.project));
        }

        // measures's setting
        report.setMeasures(measureProvider.getMeasures());
        // metrics' by component setting
        report.setComponents(componentProvider.getComponents());
        // set report basic data
        report.setProject(projectProvider.getProject(projectProvider.getProjectKey()));
        // project's name's setting
        report.setProjectName(report.getProject().getName());
        // formatted issues, unconfirmed issues and raw issues' setting
        report.setIssues(issuesProvider.getIssues());
        report.setUnconfirmed(issuesProvider.getUnconfirmedIssues());
        report.setRawIssues(issuesProvider.getRawIssues());
        // facets's setting
        report.setFacets(issuesProvider.getFacets());
        // quality profile's setting
        report.setQualityProfiles(qualityProfileProvider.getQualityProfiles(report.getProject().getOrganization()));
        // quality gate's setting
        report.setQualityGate(qualityGateProvider.getProjectQualityGate());
        // languages' settings
        report.getProject().setLanguages(languageProvider.getLanguages());

        return report;
    }

}
