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
package fr.cnes.sonar.report.plugin.tasks;


import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.exporters.JsonExporter;
import fr.cnes.sonar.report.exporters.XmlExporter;
import fr.cnes.sonar.report.exporters.docx.DocXExporter;
import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
import fr.cnes.sonar.report.factory.ReportFactory;
import fr.cnes.sonar.report.factory.ServerFactory;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.model.SonarQubeServer;
import fr.cnes.sonar.report.utils.StringManager;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.RequestHandler;
import org.sonar.api.server.ws.Response;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarqube.ws.MediaTypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Execute element to produce the report
 */
public class ReportTask implements RequestHandler {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Loggers.get(ReportTask.class);

    /**
     * Replacement character for not supported chars
     */
    private static final String HASHTAG = "#";
    /**
     * Not supported characters' regex
     */
    private static final String NOT_SUPPORTED_CHARS = ":";

    /**
     * Product the report
     * @param projectId Key of the project to report
     * @param reportAuthor Author of the report
     * @param reportPath Output folder
     * @param reportTemplate template to use for the processing
     * @param issuesTemplate template for the xlsx file
     * @return logs of the task
     * @throws IOException When a file writing goes wrong.
     * @throws BadSonarQubeRequestException Invoked request is not correct.
     * @throws UnknownQualityGateException Asked quality gate is unknown.
     * @throws SonarQubeException Occurred on server side error.
     */
    public String report(final String projectId, final String reportAuthor, final String reportPath,
                         final String reportTemplate, final String issuesTemplate)
            throws IOException, BadSonarQubeRequestException, UnknownQualityGateException, BadExportationDataTypeException, XmlException, OpenXML4JException, SonarQubeException {

        // formatted date
        final String date = new SimpleDateFormat(StringManager.DATE_PATTERN).format(new Date());
        // server of SQ server
        final String sonarqubeUrl = "http://localhost:9000";
        // token to access SonarQube
        final String token = "noauth";

        // SonarQube server.
        final SonarQubeServer server = new ServerFactory(sonarqubeUrl, token).create();

        // generate report
        final Report report = new ReportFactory(server, token, projectId, reportAuthor, date).create();

        // instantiate exporters
        final DocXExporter docXExporter = new DocXExporter();
        final XmlExporter profileExporter = new XmlExporter();
        final JsonExporter gateExporter = new JsonExporter();
        final XlsXExporter issuesExporter = new XlsXExporter();

        // prepare docx report's filename
        final String docXFilename = "temp/cnesreport/analysis-report.docx";
        // export the full docx report
        File docx = docXExporter.export(report, docXFilename, "template/code-analysis-template.docx");

        // construct the xlsx filename by replacing date and name
        final String xlsXFilename = "temp/cnesreport/issues-report.xlsx";
        // export the xlsx issues' list
        File xlsx = issuesExporter.export(report, xlsXFilename, "template/issues-template.xlsx");

        // return the log
        return "nop";
    }

    /**
     * Use the user's request to start the report generation
     * @param request request coming from the user
     * @param response response to send to the user
     * @throws IOException When a file writing goes wrong.
     * @throws BadSonarQubeRequestException Invoked request is not correct.
     * @throws UnknownQualityGateException Asked quality gate is unknown.
     * @throws BadExportationDataTypeException Error on App API use.
     * @throws XmlException Occurred on XML error.
     * @throws OpenXML4JException Occurred on OpenXML error.
     * @throws SonarQubeException Occurred on server side error.
     */
    public void handle(final Request request, final Response response)
            throws IOException, BadSonarQubeRequestException, UnknownQualityGateException, BadExportationDataTypeException, XmlException, OpenXML4JException, SonarQubeException {

        // Key of the project provided by the user through parameters
        final String projectKey = request.mandatoryParam("key");
        // Code to be used in the created files. The only character that is not supported
        // in filesystems but which is in project key is ":", so we replace all occurrences by a "#".
        final String projectCode = projectKey.replaceAll(NOT_SUPPORTED_CHARS, HASHTAG);
        // Report's author
        final String author = request.mandatoryParam("author");
        // Date of today
        final String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // Construct the name of the output folder
        final String workspace = "./";

        // read request parameters and generates response output
        // generate the reports and save output
        final String result = report(
                projectKey,
                author,
                workspace,
                "template/code-analysis-template.docx",
                "template/issues-template.xlsx"
        );

        // works
        // getClass().getResourceAsStream("/template/issues-template.xlsx")

        /**
         StringBuilder sb = new StringBuilder();
         sb.append("Test String");

         File f = new File("d:\\test.zip");
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
         ZipEntry e = new ZipEntry("mytext.txt");
         out.putNextEntry(e);

         byte[] data = sb.toString().getBytes();
         out.write(data, 0, data.length);
         out.closeEntry();

         out.close();
         */

        // create the final zip with all documents to return
        InputStream reportFile = this.getClass().getResourceAsStream("/template/issues-template.xlsx");

        // finally respond to the request with the file containing all report's files
        sendFile(response, reportFile, "report-"+projectCode+".zip");
    }

    /**
     * Copy a stream into another one.
     * @param in InputStream to copy (source).
     * @param out OutputStream (destination).
     * @throws IOException When an error occurs on copy.
     */
    private void writeStream(final InputStream in, final OutputStream out) throws IOException {
        IOUtils.copy(in, out);
    }

    /**
     * Send back a file on the response of a web api action.
     * @param response Response in which join a file.
     * @param input File to return.
     * @param filename Name to give to the attachment on client side.
     */
    private void sendFile(final Response response, final InputStream input, final String filename) {
        // We write directly in the response output stream rather than in the SQ response.
        Response.Stream stream = response.stream();
        // try-with-resources to auto-close streams in case of exception
        try ( OutputStream output = response.stream().output() ) {

            // we select content type with the filename's extension,
            // if unknown then default type is "application/octet-stream"
            stream.setMediaType(MediaTypes.getByFilename(filename));
            // data are declared as attachment with a specific name
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            // copy utils stream in output one
            writeStream(input, output);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
