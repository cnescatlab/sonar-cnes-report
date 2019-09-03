package fr.cnes.sonar.report;

import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.Rule;

/**
 * Stub class to return non-null rules without web call
 */
public class StubReport extends Report {
    @Override
    public Rule getRule(String key){
        Rule rule = new Rule();
        rule.setSeverity("MAJOR");
        rule.setHtmlDesc("<p>description</p>");
        rule.setKey("c");
        rule.setName("squid:1234");
        rule.setType("BUG");
        rule.setDebt("2h");
        rule.setLang("FR");
        rule.setLangName("Francais");
        return rule;
    }
}