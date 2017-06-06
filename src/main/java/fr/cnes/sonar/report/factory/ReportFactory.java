package fr.cnes.sonar.report.factory;

import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import fr.cnes.sonar.report.providers.IssuesProvider;
import fr.cnes.sonar.report.providers.MeasureProvider;
import fr.cnes.sonar.report.providers.QualityGateProvider;
import fr.cnes.sonar.report.providers.QualityProfileProvider;

import java.io.IOException;

/**
 * Construct  the report from data providers
 * @author begarco
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
     * Create a report from program data
     * @return A complete report data model
     * @throws UnknownParameterException a specified parameter does not exist
     * @throws IOException on json problem
     * @throws UnknownQualityGateException a quality gate is not correct
     */
    public Report create() throws UnknownParameterException, IOException, BadSonarQubeRequestException, UnknownQualityGateException {
        // the new report to return
        Report report = new Report();

        // instantiation of providers
        IssuesProvider ip = new IssuesProvider(params);
        MeasureProvider mp = new MeasureProvider(params);
        QualityProfileProvider pp = new QualityProfileProvider(params);
        QualityGateProvider gp = new QualityGateProvider(params);

        // project's name's setting
        report.setProjectName(params.get("project.name"));
        // author's setting
        report.setProjectAuthor(params.get("report.author"));
        // date setting
        report.setProjectDate(params.get("report.date"));
        // measures's setting
        report.setMeasures(mp.getMeasures());
        // formatted issues and raw issues' setting
        report.setIssues(ip.getIssues());
        report.setRawIssues(ip.getRawIssues());
        // facets's setting
        report.setFacets(ip.getFacets());
        // quality profile's setting
        report.setQualityProfiles(pp.getQualityProfiles());
        // quality gate's setting
        report.setQualityGate(gp.getProjectQualityGate());

        return report;
    }

}
