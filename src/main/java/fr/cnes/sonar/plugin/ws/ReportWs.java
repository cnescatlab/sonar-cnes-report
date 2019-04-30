package fr.cnes.sonar.plugin.ws;

import org.sonar.api.server.ws.WebService;

public class ReportWs implements WebService {
    @Override
    public void define(Context context) {
        final NewController controller = context.createController("api/cnesreport");
        controller.setSince("6.7");
        controller.setDescription("Blablabla");
        reportAction(controller);
        controller.done();
    }

    public void reportAction(final WebService.NewController controller){
        final WebService.NewAction report = controller.createAction("report");
        report.setDescription("Blablabla");
        report.setSince("7.4");
        report.setHandler(new exportTask());

        WebService.NewParam keyParam = report.createParam("key");
        keyParam.setDescription("Key of the project");
        keyParam.setRequired(true);

        WebService.NewParam authorParam = report.createParam("author");
        authorParam.setDescription("Author of the report");
        authorParam.setRequired(true);

    }
}

