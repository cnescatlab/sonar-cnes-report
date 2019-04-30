package fr.cnes.sonar.plugin;

import fr.cnes.sonar.plugin.web.ReportPluginPageDefinition;
import fr.cnes.sonar.plugin.ws.ReportWs;
import org.sonar.api.Plugin;

public class ReportSonarPlugin implements Plugin {
    @Override
    public void define(Context context) {
        context.addExtension(ReportWs.class);
        context.addExtension(ReportPluginPageDefinition.class);
    }
}
