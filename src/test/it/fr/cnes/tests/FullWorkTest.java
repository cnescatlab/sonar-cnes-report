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
//package fr.cnes.tests;
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
//import fr.cnes.sonar.report.input.StringManager;
//import fr.cnes.sonar.report.model.ProfileMetaData;
//import fr.cnes.sonar.report.model.QualityProfile;
//import fr.cnes.sonar.report.model.Report;
//import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
//import org.apache.xmlbeans.XmlException;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Iterator;
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
//    public void completeProcessTest() throws Exception {
//
//        try {
//
//            // Files exporters : export the resources in the correct file type
//            DocXExporter docXExporter = new DocXExporter();
//            XmlExporter profileExporter = new XmlExporter();
//            JsonExporter gateExporter = new JsonExporter();
//            XlsXExporter issuesExporter = new XlsXExporter();
//
//            // full path to the configuration folder
//            String confDirectory = String.format(ReportCommandLine.CONF_FOLDER_PATTERN,
//                    params.get(StringManager.REPORT_PATH));
//
//            // create the configuration folder
//            boolean success = (new File(confDirectory)).mkdirs();
//
//            // Producing the report
//            Report superReport = new ReportFactory(params).create();
//
//            // Export all
//            // export each linked quality profile
//            for(ProfileMetaData metaData : superReport.getProject().getQualityProfiles()) {
//                Iterator<QualityProfile> iterator = superReport.getQualityProfiles().iterator();
//                boolean goOn = true;
//                while(iterator.hasNext() && goOn) {
//                    QualityProfile qp = iterator.next();
//                    if(qp.getKey().equals(metaData.getKey())) {
//                        profileExporter.export(qp.getConf(), params, confDirectory, qp.getKey());
//                        goOn = false;
//                    }
//                }
//            }
//
//            // export the quality gate
//            gateExporter.export(superReport.getQualityGate().getConf(),params,confDirectory,superReport.getQualityGate().getName());
//
//            // prepare docx report's filename
//            String docXFilename = ReportCommandLine.formatFilename(
//                    ReportCommandLine.REPORT_FILENAME, superReport.getProjectName());
//            // export the full docx report
//            docXExporter.export(superReport, params, params.get(
//                    ReportCommandLine.REPORT_PATH), docXFilename);
//
//            // construct the xlsx filename by replacing date and name
//            String xlsXFilename = ReportCommandLine.formatFilename(
//                    ReportCommandLine.ISSUES_FILENAME, superReport.getProjectName());
//            // export the xlsx issues' list
//            issuesExporter.export(superReport,params,params.get(
//                    ReportCommandLine.REPORT_PATH), xlsXFilename);
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
