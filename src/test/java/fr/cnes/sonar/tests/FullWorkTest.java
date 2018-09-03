///*
// * This file is part of cnesreport.
// *
// * cnesreport is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * cnesreport is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package fr.cnes.sonar.tests;
//
//import fr.cnes.sonar.report.ReportCommandLine;
//import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
//import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
//import fr.cnes.sonar.report.exceptions.UnknownParameterException;
//import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
//import fr.cnes.sonar.report.exporters.JsonExporter;
//import fr.cnes.sonar.report.exporters.XmlExporter;
//import fr.cnes.sonar.report.exporters.docx.DocXExporter;
//import fr.cnes.sonar.report.exporters.xlsx.XlsXExporter;
//import fr.cnes.sonar.report.factory.ReportFactory;
//import fr.cnes.sonar.report.utils.StringManager;
//import fr.cnes.sonar.report.model.Report;
//import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
//import org.apache.xmlbeans.XmlException;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Test the creation of a docx file from a template
// * @author lequal
// */
//public class FullWorkTest extends MasterTest {
//
//    /**
//     * Logger of this class
//     */
//    private static final Logger LOGGER = Logger.getLogger(FullWorkTest.class.getName());
//
//    @Test
//    public void completeProcessTest() {
//
//        try {
//            // extract params
//            final String url = params.get("sonar.url");
//            final String token = params.get("sonar.token");
//            final String project = params.get("sonar.project.id");
//            final String author = params.get("report.author");
//            final String date = params.get("report.date");
//            final String reportPath = params.get(ReportCommandLine.REPORT_PATH);
//            final String reportTemplate = params.get(StringManager.REPORT_TEMPLATE);
//            final String issuesTemplate = params.get(StringManager.ISSUES_TEMPLATE);
//
//            // generate report
//            ReportCommandLine.report(url, token, project, author, date, reportPath, reportTemplate, issuesTemplate);
//        } catch (BadExportationDataTypeException | BadSonarQubeRequestException | IOException |
//                UnknownParameterException | UnknownQualityGateException | OpenXML4JException |
//                XmlException e) {
//            // it logs all the stack trace
//            LOGGER.log(Level.SEVERE,e.getMessage(), e);
//        }
//
//    }
//
//}
