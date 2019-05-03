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

package fr.cnes.sonar.plugin.ws;

import fr.cnes.sonar.plugin.tools.PluginStringManager;
import fr.cnes.sonar.plugin.tools.FileTools;
import fr.cnes.sonar.plugin.tools.ZipFolder;
import fr.cnes.sonar.report.ReportCommandLine;
import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.sonar.api.config.Configuration;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.RequestHandler;
import org.sonar.api.server.ws.Response;

import java.io.File;
import java.io.IOException;

public class ExportTask implements RequestHandler {

    // Sonarqube configuration
    private final Configuration config;

    /**
     * public constructor
     * @param config sonarqube configuration
     */
    ExportTask(Configuration config){
        this.config = config;
    }

    /**
     * handle a request, write output in response stream.
     * @param request
     * @param response
     */
    @Override
    public void handle(Request request, Response response) throws BadExportationDataTypeException, BadSonarQubeRequestException, IOException,
            UnknownQualityGateException, OpenXML4JException, XmlException, SonarQubeException {

        // Getting stream and change headers
        Response.Stream stream = response.stream();
        stream.setMediaType("application/zip");


        // Get a temp folder
        final File outputDirectory = File.createTempFile("cnesreport", Long.toString(System.nanoTime()));

        // Last line create file instead of folder, we delete file to put folder at the same place later
        outputDirectory.delete();

        // Start generation, re-using standalone script
        ReportCommandLine.execute(new String[]{
                "report",
                "-o", outputDirectory.getAbsolutePath(),
                "-s", config.get("sonar.core.serverBaseURL").orElse(PluginStringManager.getProperty("plugin.defaultHost")),
                "-p", request.getParam(PluginStringManager.getProperty("api.report.args.key")).getValue(),
                "-a", request.getParam(PluginStringManager.getProperty("api.report.args.author")).getValue()
        });

        // generate zip output and send it
        ZipFolder.pack(outputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath() + ".zip");
        File zip = new File(outputDirectory.getAbsolutePath() + ".zip");
        FileUtils.copyFile(zip, stream.output());


        // Some cleaning
        zip.delete();
        FileTools.deleteFolder(outputDirectory);
    }
}
