package fr.cnes.sonar.plugin.web;

import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.PageDefinition;

public class ReportPluginPageDefinition implements PageDefinition {

    @Override
    public void define(Context context) {
        Page.Builder page = Page.builder("cnesreport/report");
        page.setName("Report ?");
        page.setScope(Page.Scope.GLOBAL);
        context.addPage(page.build());
    }
}
