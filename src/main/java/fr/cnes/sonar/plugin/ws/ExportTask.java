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
import fr.cnes.sonar.plugin.tools.DefaultBranch;
import fr.cnes.sonar.plugin.tools.FileTools;
import fr.cnes.sonar.plugin.tools.ZipFolder;
import fr.cnes.sonar.report.ReportCommandLine;
import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.sonar.api.config.Configuration;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.RequestHandler;
import org.sonar.api.server.ws.Response;
import org.sonarqube.ws.MediaTypes;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ExportTask implements RequestHandler {

    // Sonarqube configuration
    private final Configuration config;

    /**
     * Value "false" of an api call parameter
     */
    private static final String FALSE = "false";

    /**
     * Value "no" of an api call parameter
     */
    private static final String NO = "no";

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
    public void handle(Request request, Response response) throws BadExportationDataTypeException, IOException,
            UnknownQualityGateException, OpenXML4JException, XmlException, SonarQubeException, ParseException {

        // Get project key
        String projectKey = request.getParam(PluginStringManager.getProperty("api.report.args.key")).getValue();

        // Getting stream and change headers
        Response.Stream stream = response.stream();

        // Get a temp folder
        final File outputDirectory = File.createTempFile("cnesreport", Long.toString(System.nanoTime()));

        // Last line create file instead of folder, we delete file to put folder at the same place later
        Files.delete(outputDirectory.toPath());

        // Start generation, re-using standalone script
        try {
            final Request.StringParam pBranch =
                    request.getParam(PluginStringManager.getProperty("api.report.args.branch"));
            
            final Request.StringParam pLanguage =
                    request.getParam(PluginStringManager.getProperty("api.report.args.language"));

            final Request.StringParam pEnableDocx =
                    request.getParam(PluginStringManager.getProperty("api.report.args.enableDocx"));

            final Request.StringParam pEnableMd =
                    request.getParam(PluginStringManager.getProperty("api.report.args.enableMd"));

            final Request.StringParam pEnableXlsx =
                    request.getParam(PluginStringManager.getProperty("api.report.args.enableXlsx"));

            final Request.StringParam pEnableCsv =
                    request.getParam(PluginStringManager.getProperty("api.report.args.enableCsv"));

            final Request.StringParam pEnableConf =
                    request.getParam(PluginStringManager.getProperty("api.report.args.enableConf"));

            // Build SonarQube local URL
            String port = config.get("sonar.web.port").orElse(PluginStringManager.getProperty("plugin.defaultPort"));
            String context = config.get("sonar.web.context").orElse(PluginStringManager.getProperty("plugin.defaultContext"));
            String sonarUrl = String.format(PluginStringManager.getProperty("plugin.defaultHost"), port, context);

            // Get files templates paths if defined in the decicated SonarQube configuration panel
            String docxPath = config.get("sonar.cnesreport.docx.path").orElse(null);
            String mdPath = config.get("sonar.cnesreport.md.path").orElse(null);
            String xlsxPath = config.get("sonar.cnesreport.xlsx.path").orElse(null);

            // create a new client to talk with sonarqube's services
            WsClient wsClient = WsClientFactories.getLocal().newClient(request.localConnector());

            // prepare params for the report generation
            List<String> reportParams = new ArrayList<>(Arrays.asList(
                    "report",
                    "-o", outputDirectory.getAbsolutePath(),
                    "-s", sonarUrl,
                    "-p", projectKey,
                    "-b", pBranch.isPresent()?pBranch.getValue(): DefaultBranch.getDefaultBranchFromProject(wsClient, projectKey),
                    "-a", request.getParam(PluginStringManager.getProperty("api.report.args.author")).getValue(),
                    "-t", request.getParam(PluginStringManager.getProperty("api.report.args.token")).getValue(),
                    "-l", pLanguage.isPresent()?pLanguage.getValue(): StringManager.getProperty(StringManager.DEFAULT_LANGUAGE)
            ));

            // add files templates paths to params if defined
            if (docxPath != null) {
                reportParams.add("-r");
                reportParams.add(docxPath);
            }
            if (mdPath != null) {
                reportParams.add("-n");
                reportParams.add(mdPath);
            }
            if (xlsxPath != null) {
                reportParams.add("-x");
                reportParams.add(xlsxPath);
            }

            String pEnableDocxValue = pEnableDocx.getValue();
            String pEnableMdValue = pEnableMd.getValue();
            String pEnableXlsxValue = pEnableXlsx.getValue();
            String pEnableCsvValue = pEnableCsv.getValue();
            String pEnableConfValue = pEnableConf.getValue();

            // add disable files generation params if requested
            if(pEnableDocxValue != null && (pEnableDocxValue.equals(FALSE) || pEnableDocxValue.equals(NO))) {
                reportParams.add("-w");
            }
            if(pEnableMdValue != null && (pEnableMdValue.equals(FALSE) || pEnableMdValue.equals(NO))) {
                reportParams.add("-m");
            }
            if(pEnableXlsxValue != null && (pEnableXlsxValue.equals(FALSE) || pEnableXlsxValue.equals(NO))) {
                reportParams.add("-e");
            }
            if(pEnableCsvValue != null && (pEnableCsvValue.equals(FALSE) || pEnableCsvValue.equals(NO))) {
                reportParams.add("-f");
            }
            if(pEnableConfValue != null && (pEnableConfValue.equals(FALSE) || pEnableConfValue.equals(NO))) {
                reportParams.add("-c");
            }

            // Execute report generation
            ReportCommandLine.execute(reportParams.toArray(new String[reportParams.size()]), wsClient);

            stream.setMediaType("application/zip");
            String filename = ReportFactory.formatFilename("zip.report.output", "", "", projectKey);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + '"');


            // generate zip output and send it
            ZipFolder.pack(outputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath() + ".zip");
            File zip = new File(outputDirectory.getAbsolutePath() + ".zip");
            FileUtils.copyFile(zip, stream.output());
            Files.deleteIfExists(zip.toPath());
        } catch (BadSonarQubeRequestException e) {          
            response.stream().setMediaType(MediaTypes.JSON);
            try (
                OutputStreamWriter writer = new OutputStreamWriter(response.stream().output(), StandardCharsets.UTF_8);
                JsonWriter jsonWriter = new JsonWriter(writer);
            ) {
                jsonWriter.beginObject();
                jsonWriter.name("error").value(PluginStringManager.getProperty("api.tokenerror"));
                jsonWriter.endObject();
                jsonWriter.flush();
            }
        }

        FileTools.deleteFolder(outputDirectory);
    }
}
