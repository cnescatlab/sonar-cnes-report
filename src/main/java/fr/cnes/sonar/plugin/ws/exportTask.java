package fr.cnes.sonar.plugin.ws;

import fr.cnes.sonar.plugin.tools.FileTools;
import fr.cnes.sonar.plugin.tools.ZipFolder;
import fr.cnes.sonar.report.ReportCommandLine;
import org.apache.commons.io.FileUtils;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.RequestHandler;
import org.sonar.api.server.ws.Response;

import java.io.File;

public class exportTask implements RequestHandler {
    @Override
    public void handle(Request request, Response response){
        try {
            Response.Stream stream = response.stream();
            stream.setMediaType("application/zip");

            // Get a temp folder
            final File outputDirectory = File.createTempFile("cnesreport", Long.toString(System.nanoTime()));
            // Create file instead of folder, we delete file to put folder later
            outputDirectory.delete();
            ReportCommandLine.execute(new String[]{
                    "report",
                    "-o", outputDirectory.getAbsolutePath(),
                    "-s", "http://localhost:9000",
                    "-p", request.getParam("key").getValue(),
                    "-a", request.getParam("author").getValue()
            });

            // generate zip output and send it
            ZipFolder.pack(outputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath() + ".zip");
            File zip = new File(outputDirectory.getAbsolutePath() + ".zip");
            FileUtils.copyFile(zip, stream.output());


            // Some cleaning
            zip.delete();
            FileTools.deleteFolder(outputDirectory);
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }
}
