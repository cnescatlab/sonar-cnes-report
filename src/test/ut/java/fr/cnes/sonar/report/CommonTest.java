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

package fr.cnes.sonar.report;

import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.factory.ProviderFactory;
import fr.cnes.sonar.report.factory.StandaloneProviderFactory;
import fr.cnes.sonar.report.factory.PluginProviderFactory;
import fr.cnes.sonar.report.utils.ReportConfiguration;
import fr.cnes.sonar.report.utils.StringManager;
import org.junit.Before;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Contains common code for report
 */
public abstract class CommonTest {

    /**
     * Severity for stubbed violations.
     */
    private static final String MAJOR = "MAJOR";
    /**
     * Project key.
     */
    protected static final String PROJECT_KEY = "cnesreport";
    /**
     * Branch name.
     */
    protected static final String BRANCH = "main";
    /**
     * Quality Gate name.
     */
    protected static final String QUALITY_GATE_NAME = "CNES";
    /**
     * Stubbed report for report.
     */
    protected Report report;
    /**
     * Stubbed sonarqube server for report.
     */
    protected SonarQubeServer sonarQubeServerInstance;
    /**
     * stubbed parameters for testing.
     */
    protected ReportConfiguration conf;
    /**
     * Stubbed WsClient for testing plugin mode.
     */
    protected WsClient wsClient;
    /**
     * Stubbed ProviderFactory for testing standalone mode.
     */
    protected ProviderFactory standaloneProviderFactory;
    /**
     * Stubbed ProviderFactory for testing plugin mode.
     */
    protected ProviderFactory pluginProviderFactory;
    /**
     * Stubbed sonarqube server URL for report.
     */
    protected String sonarQubeServer;

    /**
     * Setting of all stubbed resources before launching a test.
     */
    @Before
    public void before() {
        report = new StubReport();
        conf = ReportConfiguration.create(new String[]{
                "-s", "http://biiiiiiiiiiiiim",
                "-p", PROJECT_KEY,
                "-a", "Lequal",
                "-b", BRANCH,
                "-d", new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date()),
                "-o", "./target",
                "-l", "en_US",
                "-r", "src/main/resources/template/code-analysis-template.docx",
                "-x", "src/main/resources/template/issues-template.xlsx"
        });

        report.setProjectName("CNES Report");
        report.setProjectDate(new Date().toString().substring(0,16));
        report.setProjectAuthor("Lequal");

        sonarQubeServerInstance = new SonarQubeServer();
        sonarQubeServerInstance.setStatus("UP");
        sonarQubeServerInstance.setUrl("http://biiiiiiiiiiiiim");
        sonarQubeServerInstance.setVersion("8.9.6");
        sonarQubeServerInstance.setSupported(true);

        sonarQubeServer = conf.getServer();

        HttpConnector httpConnector = HttpConnector.newBuilder()
            .url(conf.getServer())
            .credentials("admin", "admin")
            .build();
        wsClient = WsClientFactories.getDefault().newClient(httpConnector);

        standaloneProviderFactory = new StandaloneProviderFactory(conf.getServer(), conf.getToken(), conf.getProject(), conf.getBranch());
        pluginProviderFactory = new PluginProviderFactory(conf.getProject(), conf.getBranch(), wsClient);

        final List<Issue> issues = new ArrayList<>();
        final Issue i1 = new Issue();
        final Issue i2 = new Issue();
        final Issue i3 = new Issue();
        final Issue i4 = new Issue();
        i1.setComponent("a");
        i1.setKey("z");
        i1.setLine("15");
        i1.setMessage("azerty");
        i1.setProject("genius");
        i1.setSeverity(MAJOR);
        i1.setStatus("OPEN");
        i1.setType("BUG");
        i1.setRule("squid4321");
        i3.setComponent("a");
        i3.setKey("z");
        i3.setLine("15");
        i3.setMessage("azerty");
        i3.setProject("genius");
        i3.setSeverity(MAJOR);
        i3.setStatus("OPEN");
        i3.setType("BUG");
        i2.setComponent("b");
        i2.setKey("x");
        i2.setLine("23");
        i2.setMessage("qwertz");
        i2.setProject("genius");
        i2.setSeverity(MAJOR);
        i2.setStatus("OPEN");
        i2.setType("BUG");
        i2.setRule("abcd:dcba");
        i4.setSeverity(MAJOR);
        i4.setType("BUG");
        // Adding multiple time to test comparator (DataAdapter.RuleComparator)
        Issue issue;
        for(int i=1;i<10;i++){
            issue = new Issue();
            issue.setRule("squid:1234");
            issue.setMessage("ISSUES");
            issue.setSeverity(MAJOR);
            issue.setKey("key");
        }
        issues.add(i1);
        issues.add(i3);
        issues.add(i2);
        issues.add(i4);
        report.setIssues(issues);

        List<Map<String,String>> rawIssues = new ArrayList<>();
        Map<String, String> issue1 = new HashMap<>();
        issue1.put("Comments", new ArrayList<String>().toString());
        issue1.put("ToReview", "true");
        issue1.put("someNumber", "1.0");
        rawIssues.add(issue1);

        Map<String,String> issue2 = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("Element 1");
        list.add("Element 2");
        issue2.put("Comments", list.toString());
        issue2.put("someNumber", "2.0");
        rawIssues.add(issue2);

        report.setRawIssues(rawIssues);

        final List<Facet> facetsList = new ArrayList<>();
        final Facet rules = new Facet();
        final Facet severities = new Facet();
        final Facet types = new Facet();
        rules.setProperty("rules");
        severities.setProperty("severities");
        types.setProperty("types");
        final List<Value> values = new ArrayList<>();
        values.add(new Value("squid:S1258", 3));
        rules.setValues(values);
        final List<Value> valuesSeverity = new ArrayList<>();
        valuesSeverity.add(new Value("CRITICAL", 0));
        valuesSeverity.add(new Value("INFO", 10));
        final List<Value> valuesType = new ArrayList<>();
        valuesType.add(new Value("BUG", 5));
        valuesType.add(new Value("CODE_SMELL", 15));
        severities.setValues(valuesSeverity);
        types.setValues(valuesType);
        facetsList.add(rules);
        facetsList.add(severities);
        facetsList.add(types);
        final Facets facets = new Facets();
        facets.setFacets(facetsList);
        report.setFacets(facets);

        // Adding TimeFacets
        List<TimeValue> values1 = new ArrayList<>();
        values1.add(new TimeValue(1.0, "1.0"));
        values1.add(new TimeValue(2.3, "4.5"));

        List<TimeValue> values2 = new ArrayList<>();
        values2.add(new TimeValue(1.0, "1"));
        values2.add(new TimeValue(2.3, "4"));
        values2.add(new TimeValue(5.6, "7"));

        List<TimeFacet> facetList = new ArrayList<>();
        facetList.add(new TimeFacet("sqale_debt_ratio", values1));
        facetList.add(new TimeFacet("violations", values2));
        
        TimeFacets timeFacets = new TimeFacets();
        timeFacets.setTimeFacets(facetList);
        report.setTimeFacets(timeFacets);


        final List<SecurityHotspot> securityHotspots = new ArrayList<>();
        final SecurityHotspot sh1 = new SecurityHotspot();
        final SecurityHotspot sh2 = new SecurityHotspot();
        final SecurityHotspot sh3 = new SecurityHotspot();
        final Comment[] comments = new Comment[0];
        sh1.setComponent("c");
        sh1.setLanguage("Java");
        sh1.setKey("y");
        sh1.setLine("58");
        sh1.setMessage("qsdf");
        sh1.setSeverity(MAJOR);
        sh1.setStatus("TO_REVIEW");
        sh1.setRule("java:S1234");
        sh1.setSecurityCategory("buffer-overflow");
        sh1.setVulnerabilityProbability("LOW");
        sh1.setComments(comments);
        sh2.setComponent("d");
        sh2.setLanguage("Java");
        sh2.setKey("w");
        sh2.setLine("45");
        sh2.setMessage("wxcv");
        sh2.setSeverity(MAJOR);
        sh2.setStatus("TO_REVIEW");
        sh2.setRule("java:S4321");
        sh2.setSecurityCategory("sql-injection");
        sh2.setVulnerabilityProbability("HIGH");
        sh2.setComments(comments);
        sh3.setComponent("d");
        sh3.setLanguage("Java");
        sh3.setKey("k");
        sh3.setLine("45");
        sh3.setMessage("wxcv");
        sh3.setSeverity(MAJOR);
        sh3.setStatus("TO_REVIEW");
        sh3.setRule("java:S4321");
        sh3.setSecurityCategory("sql-injection");
        sh3.setVulnerabilityProbability("HIGH");
        sh3.setComments(comments);
        securityHotspots.add(sh1);
        securityHotspots.add(sh2);
        securityHotspots.add(sh3);
        report.setToReviewSecurityHotspots(securityHotspots);

        final List<SecurityHotspot> reviewedSecurityHotspots = new ArrayList<>();
        final SecurityHotspot rsh1 = new SecurityHotspot();
        final SecurityHotspot rsh2 = new SecurityHotspot();
        rsh1.setComponent("c");
        rsh1.setLanguage("Java");
        rsh1.setKey("u");
        rsh1.setLine("58");
        rsh1.setMessage("qsdf");
        rsh1.setSeverity(MAJOR);
        rsh1.setStatus("REVIEWED");
        rsh1.setResolution("FIXED");
        rsh1.setRule("java:S1234");
        rsh1.setSecurityCategory("buffer-overflow");
        rsh1.setVulnerabilityProbability("LOW");
        rsh1.setComments(comments);
        rsh2.setComponent("d");
        rsh2.setLanguage("Java");
        rsh2.setKey("v");
        rsh2.setLine("45");
        rsh2.setMessage("wxcv");
        rsh2.setSeverity(MAJOR);
        rsh2.setStatus("REVIEWED");
        rsh2.setResolution("SAFE");
        rsh2.setRule("java:S4321");
        rsh2.setSecurityCategory("sql-injection");
        rsh2.setVulnerabilityProbability("HIGH");
        rsh2.setComments(comments);
        reviewedSecurityHotspots.add(rsh1);
        reviewedSecurityHotspots.add(rsh2);
        report.setReviewedSecurityHotspots(reviewedSecurityHotspots);

        final ProfileData profileData = new ProfileData();
        profileData.setConf("coucou");
        final List<Rule> rulesOfProfile = new ArrayList<>();
        final Rule rule1 = new Rule();
        final Rule rule2 = new Rule();
        final Rule rule3 = new Rule();
        rule1.setKey("squid:S1258");
        rule1.setName("Nom swag");
        rule1.setHtmlDesc("Cette description est pas trop longue donc ça va en fait, faut pas s'inquiéter.");
        rule1.setSeverity(MAJOR);
        rule1.setType("BUG");
        rule2.setKey("java:S1234");
        rule2.setName("Nom swag");
        rule2.setHtmlDesc("Cette description est pas trop longue donc ça va en fait, faut pas s'inquiéter.");
        rule2.setSeverity(MAJOR);
        rule2.setType("SECURITY_HOTSPOT");
        rule3.setKey("java:S4321");
        rule3.setName("Nom swag");
        rule3.setHtmlDesc("Cette description est pas trop longue donc ça va en fait, faut pas s'inquiéter.");
        rule3.setSeverity(MAJOR);
        rule3.setType("SECURITY_HOTSPOT");
        rulesOfProfile.add(rule1);
        rulesOfProfile.add(rule2);
        rulesOfProfile.add(rule3);
        profileData.setRules(rulesOfProfile);
        final ProfileMetaData profileMetaData = new ProfileMetaData();
        profileMetaData.setName("BG");
        profileMetaData.setKey("BG");
        final QualityProfile qualityProfile = new QualityProfile(profileData, profileMetaData);
        qualityProfile.setProjects((new Project[]{new Project("sonar-cnes-plugin", "sonar-cnes-plugin", "none", "", "", "")}));
        report.setQualityProfiles(Collections.singletonList(qualityProfile));
        final QualityGate qualityGate = new QualityGate();
        qualityGate.setName(QUALITY_GATE_NAME);
        report.setQualityGate(qualityGate);

        final Language language = new Language();
        language.setKey("java");
        language.setName("Java");

        final Map<String, Language> languages = new HashMap<>();
        languages.put(language.getKey(), language);

        final Project project = new Project("key", "Name", "none","Version", "Short description", "");
        project.setQualityProfiles(new ProfileMetaData[0]);
        project.setLanguages(languages);
        report.setProject(project);

        final List<Measure> measures = new ArrayList<>();
        measures.add(new Measure("reliability_rating", "1.0"));
        measures.add(new Measure("duplicated_lines_density", "1.0"));
        measures.add(new Measure("comment_lines_density", "1.0"));
        measures.add(new Measure("sqale_rating", "2.0"));
        measures.add(new Measure("coverage", "1.0"));
        measures.add(new Measure("ncloc", "1.0"));
        measures.add(new Measure("alert_status", "OK"));
        measures.add(new Measure("security_rating", "3.0"));
        measures.add(new Measure("security_review_rating", "4.0"));
        measures.add(new Measure("reliability_remediation_effort", "3474"));
        measures.add(new Measure("security_remediation_effort", "4097"));
        measures.add(new Measure("sqale_index", "3224"));
        measures.add(new Measure("tests", "42"));
        measures.add(new Measure("test_success_density", "90.3"));
        measures.add(new Measure("skipped_tests", "0"));
        measures.add(new Measure("test_failures", "2"));
        measures.add(new Measure("test_errors", "8"));
        report.setMeasures(measures);


        Map<String, Double> metricStats = new HashMap<>();
        // Use first component to gets all metrics names

        // for each metric
        metricStats.put("mincomplexity", 0.0);
        metricStats.put("maxcomplexity", 1.0);
        metricStats.put("minncloc", 0.0);
        metricStats.put("maxncloc", 1.0);
        metricStats.put("mincomment_lines_density", 0.0);
        metricStats.put("maxcomment_lines_density", 1.0);
        metricStats.put("minduplicated_lines_density", 0.0);
        metricStats.put("maxduplicated_lines_density", 1.0);
        metricStats.put("mincognitive_complexity", 0.0);
        metricStats.put("maxcognitive_complexity", 1.0);
        metricStats.put("mincoverage", 0.0);
        metricStats.put("medianncloc", 1.0);
        // Max coverage is not included to raise a NullPointerException


        report.setMetricsStats(metricStats);

        final Map<String, String> qualityGateStatus = new HashMap<>();
        qualityGateStatus.put("Quality Gate Status", "OK");
        qualityGateStatus.put("Maintainability Rating on New Code", "OK");
        qualityGateStatus.put("Coverage on New Code", "OK");
        report.setQualityGateStatus(qualityGateStatus);
    }
}
