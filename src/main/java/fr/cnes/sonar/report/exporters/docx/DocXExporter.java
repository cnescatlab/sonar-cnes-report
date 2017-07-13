package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.input.Params;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.model.Report;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static fr.cnes.sonar.report.input.StringManager.string;

/**
 * Exports the report in .docx format
 * @author begarco
 */
public class DocXExporter implements IExporter {

    /**
     *  Name of the property containing the destination path of the report
     */
    private static final String REPORT_TEMPLATE = "report.template";
    /**
     * Placeholder for the table containing detailed issues
     */
    private static final String DETAILS_TABLE_PLACEHOLDER = "$ISSUES_DETAILS";
    /**
     * Placeholder for the table containing counts of issues by type and severity
     */
    private static final String COUNT_TABLE_PLACEHOLDER = "$ISSUES_COUNT";

    /**
     * Overridden export for docX
     * @param data Data to export as Report
     * @param params Program's parameters
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @throws BadExportationDataTypeException Data has not the good type
     * @throws UnknownParameterException report.path is not set
     * @throws OpenXML4JException ...
     * @throws IOException ...
     * @throws XmlException ...
     */
    @Override
    public void export(Object data, Params params, String path, String filename)
            throws BadExportationDataTypeException, UnknownParameterException, OpenXML4JException, IOException, XmlException {
        // check data type
        if (!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // data casting
        Report report = (Report) data;

        // open excel file from the path given in the parameters
        File file = new File(params.get(REPORT_TEMPLATE));
        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            OPCPackage opcPackage = OPCPackage.open(fileInputStream);
            XWPFDocument document = new XWPFDocument(opcPackage)
        )
        {

            // Fill charts
            DocXTools.fillCharts(opcPackage, document,report.getFacets());

            // Add issues
            List<List<String>> issues = DataAdapter.getIssues(report);
            String[] issuesArrayFr = {string("header.name"),string("header.description"),
                    string("header.type"),string("header.severity"),string("header.number")};
            List<String> headerIssues = new ArrayList<>(Arrays.asList(issuesArrayFr));
            DocXTools.fillTable(document, headerIssues, issues, DETAILS_TABLE_PLACEHOLDER);

            // Add issues count by type and severity
            List<List<String>> types = DataAdapter.getTypes(report);
            DocXTools.fillTable(document, headerIssues.subList(2,5), types, COUNT_TABLE_PLACEHOLDER);


            // Map which contains all values to replace
            // the key is the placeholder and the value is the value to write over
            Map<String, String> replacementValues = loadPlaceholdersMap(report);

            // replace all placeholder in the document (head, body, foot) with the map
            DocXTools.replacePlaceholder(document, replacementValues);

            // Save the result by creating a new file in the directory given by report.path property
            String outputName = params.get("report.path") + "/" + filename;
            FileOutputStream out = new FileOutputStream(outputName);
            // close open resources
            document.write(out);
            out.close();
            document.close();
        }
    }

    /**
     * Load in a map all the placeholder (key) with the corresponding replacement value (value)
     * @param report Report from which data are extracted
     * @return the placeholders map
     */
    private Map<String, String> loadPlaceholdersMap(Report report) {
        // final map to return
        Map<String, String> replacementValues = new HashMap<>();
        // Replacement of placeholder
        // report meta data placeholders
        replacementValues.put("XX-AUTHOR-XX", report.getProjectAuthor());
        replacementValues.put("XX-DATE-XX", report.getProjectDate());
        replacementValues.put("XX-PROJECTNAME-XX", report.getProjectName());
        // configuration placeholders
        replacementValues.put("XX-QUALITYGATENAME-XX", report.getQualityGate().getName());
        replacementValues.put("XX-QUALITYGATEFILE-XX", report.getQualityGate().getName() + ".xml");
        replacementValues.put("XX-QUALITYPROFILENAME-XX", report.getQualityProfilesName());
        replacementValues.put("XX-QUALITYPROFILEFILE-XX", report.getQualityProfilesFilename());
        // Synthesis placeholders
        for (Measure m : report.getMeasures()) {
            String placeholder = getPlaceHolderName(m.getMetric());
            String value = m.getMetric().contains("rating") ? numberToLetter(m.getValue()) : m.getValue();
            replacementValues.put(placeholder, value);
        }
        return replacementValues;
    }

    /**
     * Convert the numeric note to a letter
     * @param value numeric note
     * @return a letter
     */
    private String numberToLetter(String value) {
        String res;
        // make the link between numbers and letters
        switch (value) {
            case "1.0":
                res = "A";
                break;
            case "2.0":
                res = "B";
                break;
            case "3.0":
                res = "C";
                break;
            case "4.0":
                res = "D";
                break;
            case "5.0":
                res = "E";
                break;
            default:
                res = "value";
                break;
        }
        return res;
    }

    /**
     * Give the corresponding placeholder
     * @param metric value whose it have to find the placeholder
     * @return value of the place holder
     */
    private String getPlaceHolderName(String metric) {
        String res;
        switch (metric) {
            case "reliability_rating":
                res = "XX-RELIABILITY-XX";
                break;
            case "duplicated_lines_density":
                res = "XX-DUPLICATION-XX";
                break;
            case "sqale_rating":
                res = "XX-MAINTAINBILITY-XX";
                break;
            case "coverage":
                res = "XX-COVERAGE-XX";
                break;
            case "ncloc":
                res = "XX-COMPLEXITY-XX";
                break;
            case "alert_status":
                res = "XX-QUALITYGATE-XX";
                break;
            case "security_rating":
                res = "XX-SECURITY-XX";
                break;
            default:
                res = "XX-XXXXXXXXXXXXXXX-XX";
                break;
        }
        return res;
    }

}
