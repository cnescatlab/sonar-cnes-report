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
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.providers.*;

import java.io.IOException;

/**
 * Construct  the report from resources providers
 * @author lequal
 */
public class ReportFactory {

    /**
     * Url of the SonarQube server.
     */
    private String url;
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
     * @param pUrl Value for url.
     * @param pToken Value for token.
     * @param pProject Value for project id.
     * @param pAuthor Name of the author.
     * @param pDate Date of the reporting.
     */
    public ReportFactory(final String pUrl, final String pToken, final String pProject, final String pAuthor, final String pDate) {
        this.url = pUrl;
        this.token = pToken;
        this.project = pProject;
        this.author = pAuthor;
        this.date = pDate;
    }

    /**
     * Create a report from program resources
     * @return A complete report resources model
     * @throws IOException on json problem
     * @throws BadSonarQubeRequestException when a request to the server is not well-formed
     * @throws UnknownQualityGateException a quality gate is not correct
     */
    public Report create() throws IOException, BadSonarQubeRequestException, UnknownQualityGateException {
        // the new report to return
        final Report report = new Report();

        // instantiation of providers
        final IssuesProvider issuesProvider = new IssuesProvider(this.url, this.token, this.project);
        final MeasureProvider measureProvider = new MeasureProvider(this.url, this.token, this.project);
        final ProjectProvider projectProvider = new ProjectProvider(this.url, this.token, this.project);
        final QualityProfileProvider qualityProfileProvider = new QualityProfileProvider(this.url, this.token, this.project);
        final QualityGateProvider qualityGateProvider = new QualityGateProvider(this.url, this.token, this.project);
        final LanguageProvider languageProvider = new LanguageProvider(this.url, this.token, this.project);

        // author's setting
        report.setProjectAuthor(author);
        // date setting
        report.setProjectDate(date);
        // measures's setting
        report.setMeasures(measureProvider.getMeasures());
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
        report.setQualityProfiles(qualityProfileProvider.getQualityProfiles());
        // quality gate's setting
        report.setQualityGate(qualityGateProvider.getProjectQualityGate());
        // languages' settings
        report.getProject().setLanguages(languageProvider.getLanguages());

        return report;
    }

}
