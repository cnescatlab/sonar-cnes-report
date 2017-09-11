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
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.input.StringManager;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.providers.*;

import java.io.IOException;

/**
 * Construct  the report from resources providers
 * @author lequal
 */
public class ReportFactory {

    /**
     * Program's parameters
     */
    private Params params;

    /**
     * Complete constructor
     * @param params Program's parameters
     */
    public ReportFactory(Params params) {
        this.params = params;
    }

    /**
     * Create a report from program resources
     * @return A complete report resources model
     * @throws UnknownParameterException a specified parameter does not exist
     * @throws IOException on json problem
     * @throws BadSonarQubeRequestException when a request to the server is not well-formed
     * @throws UnknownQualityGateException a quality gate is not correct
     */
    public Report create()
            throws UnknownParameterException, IOException, BadSonarQubeRequestException, UnknownQualityGateException {
        // the new report to return
        Report report = new Report();

        // instantiation of providers
        IssuesProvider issuesProvider = new IssuesProvider(params, RequestManager.getInstance());
        MeasureProvider measureProvider = new MeasureProvider(params, RequestManager.getInstance());
        ProjectProvider projectProvider = new ProjectProvider(params, RequestManager.getInstance());
        QualityProfileProvider qualityProfileProvider = new QualityProfileProvider(params, RequestManager.getInstance());
        QualityGateProvider qualityGateProvider = new QualityGateProvider(params, RequestManager.getInstance());
        LanguageProvider languageProvider = new LanguageProvider(params, RequestManager.getInstance());

        // author's setting
        report.setProjectAuthor(params.get(StringManager.REPORT_AUTHOR));
        // date setting
        report.setProjectDate(params.get(StringManager.REPORT_DATE));
        // measures's setting
        report.setMeasures(measureProvider.getMeasures());
        // set report basic data
        report.setProject(projectProvider.getProject(projectProvider.getProjectKey()));
        // project's name's setting
        report.setProjectName(report.getProject().getName());
        // formatted issues and raw issues' setting
        report.setIssues(issuesProvider.getIssues());
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
