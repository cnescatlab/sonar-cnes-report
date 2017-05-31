package fr.cnes.sonar.report.exporters.docx;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.exporters.IExporter;
import fr.cnes.sonar.report.model.*;
import fr.cnes.sonar.report.params.Params;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.dml.chart.CTChartSpace;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DrawingML.Chart;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Exports the report in .docx format
 * @author begarco
 */
public class DocXExporter implements IExporter {

    /**
     * Overridden export for docX
     * @param data Data to export as Report
     * @param params Program's parameters
     * @param filename Name of the file to export
     * @throws Exception ...
     */
    @Override
    public void export(Object data, Params params, String filename) throws Exception {
        // check data type
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        // data casting
        Report report = (Report) data;

        // Get the template file
        WordprocessingMLPackage wordMLPackage = Docx4J.load(new java.io.File(params.get("report.template")));

        // Fill charts
        fillCharts(wordMLPackage, report.getFacets());

        // Add issues
        List<List<String>> issues = getIssues(report);
        DocXTools.replaceTable(4, issues, wordMLPackage);

        // Add metrics
        List<List<String>> metrics = getMetrics(report);
        DocXTools.replaceTable(5, metrics, wordMLPackage);

        // Replacement of placeholder
        // report meta data placeholders
        DocXTools.replacePlaceholder(wordMLPackage, report.getProjectAuthor(), "XX-AUTHOR-XX");
        DocXTools.replacePlaceholder(wordMLPackage, report.getProjectDate(), "XX-DATE-XX");
        DocXTools.replacePlaceholder(wordMLPackage, report.getProjectName(), "XX-PROJECTNAME-XX");
        // configuration placeholders
        DocXTools.replacePlaceholder(wordMLPackage, report.getQualityGate().getName(), "XX-QUALITYGATENAME-XX");
        DocXTools.replacePlaceholder(wordMLPackage, report.getQualityGate().getName() + ".xml", "XX-QUALITYGATEFILE-XX");
        DocXTools.replacePlaceholder(wordMLPackage, report.getQualityProfilesName(), "XX-QUALITYPROFILENAME-XX");
        DocXTools.replacePlaceholder(wordMLPackage, report.getQualityProfilesFilename(), "XX-QUALITYPROFILEFILE-XX");
        // Synthese placeholders
        for (Measure m : report.getMeasures()) {
            DocXTools.replacePlaceholder(wordMLPackage,
                    m.getMetric().contains("rating")?numberToLetter(m.getValue()):m.getValue(),
                    getPlaceHolderName(m.getMetric()));
        }

        // Save the result
        wordMLPackage.save(new File(params.get("report.path")+"/"+filename));
    }

    /**
     * Get formatted metrics to be printed
     * @param report Report from which to extract data
     * @return list of list of string (metric,measure)
     */
    private List<List<String>> getMetrics(Report report) {
        // result to return
        List<List<String>> metrics = new ArrayList<>();

        // construct each metric
        for (Measure m : report.getMeasures()) {
            metrics.add(Arrays.asList(m.getMetric(),m.getValue()));
        }

        return metrics;
    }

    /**
     * Fill the chart "camembert"
     * @param wordMLPackage word document
     * @param facets data as facets
     */
    private void fillCharts(WordprocessingMLPackage wordMLPackage, List<Facet> facets) throws InvalidFormatException, JAXBException {
        // get charts
        Chart chart1 = (Chart) wordMLPackage.getParts().get(new PartName("/word/charts/chart1.xml"));
        Chart chart2 = (Chart) wordMLPackage.getParts().get(new PartName("/word/charts/chart2.xml"));

        // get main document
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

        // get raw open xml
        String chart1String = XmlUtils.marshaltoString(chart1.getJaxbElement(), true, true);
        String chart2String = XmlUtils.marshaltoString(chart2.getJaxbElement(), true, true);

        // get data
        List<Value> dataPerType = getFacetValues(facets, "types");
        List<Value> dataPerSeverity = getFacetValues(facets, "severities");

        // modify charts
        // search cells
        String ss1 = chart1String.replaceAll("<c:v>.*?</c:v>", "CCTOCHANGECC");
        String[] splitted1 = ss1.split("CCTOCHANGECC");
        String ss2 = chart2String.replaceAll("<c:v>.*?</c:v>", "CCTOCHANGECC");
        String[] splitted2 = ss2.split("CCTOCHANGECC");

        // rebuild open xml with values
        // chart1
        StringBuilder sb = new StringBuilder(splitted1[0]);
        for (int part = 0 ; part < splitted1.length-1 ; ++part) {
            Value v = dataPerSeverity.get(part%dataPerSeverity.size());
            sb.append("<c:v>").append(part < splitted1.length / 2 ? v.getVal() : v.getCount()).append("</c:v>");
            sb.append(splitted1[part+1]);
        }
        chart1String = sb.toString();

        // chart 2
        sb = new StringBuilder(splitted2[0]);
        for (int part = 0 ; part < splitted2.length-1 ; ++part) {
            Value v = dataPerType.get(part%dataPerType.size());
            sb.append("<c:v>").append(part < splitted2.length / 2 ? v.getVal() : v.getCount()).append("</c:v>");
            sb.append(splitted2[part+1]);
        }
        chart2String = sb.toString();

        // save charts
        chart1.setJaxbElement((CTChartSpace) ((JAXBElement) XmlUtils.unmarshalString(( chart1String ))).getValue());
        mainDocumentPart.addTargetPart(chart1, RelationshipsPart.AddPartBehaviour.OVERWRITE_IF_NAME_EXISTS);
        chart2.setJaxbElement((CTChartSpace) ((JAXBElement) XmlUtils.unmarshalString(( chart2String ))).getValue());
        mainDocumentPart.addTargetPart(chart2, RelationshipsPart.AddPartBehaviour.OVERWRITE_IF_NAME_EXISTS);

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
     * Get formatted issues summary
     * @param report report from which to export data
     * @return issues list
     */
    private List<List<String>> getIssues(Report report) {
        List<List<String>> issues = new ArrayList<>();  // result to return

        // Get the issues' id
        List<Value> items = getFacetValues(report.getFacets(), "rules");

        for (Value v : items) { // construct each issues
            List<String> issue = new ArrayList<>();
            Rule rule = report.getRule(v.getVal());
            if(rule!=null) { // if the rule is found, fill information
                // add name
                issue.add(rule.getName());
                // add description
                issue.add(rule.getHtmlDesc().replaceAll("<[^>]*>", ""));
                // add type
                issue.add(rule.getType());
                // add severity
                issue.add(rule.getSeverity());
                // add number
                issue.add(Integer.toString(v.getCount()));
            } else { // else set just known information
                // add name
                issue.add(v.getVal());
                // add description
                issue.add("?");
                // add type
                issue.add("?");
                // add severity
                issue.add("?");
                // add number
                issue.add(Integer.toString(v.getCount()));
            }

            issues.add(issue);
        }

        return issues;
    }

    /**
     * Return values of a given facet
     * @param facetName name of th facet to get
     * @return a list or null
     */
    private List<Value> getFacetValues(List<Facet> facets, String facetName) {

        // iterate on facets' list
        Iterator iterator = facets.iterator();
        // list of results
        List<Value> items = null;
        while(iterator.hasNext() && items==null) {
            // get current facet
            Facet facet = (Facet) iterator.next();
            // check if current facet is the wanted one
            if(facet.getProperty().equals(facetName)) {
                items = facet.getValues();
            }
        }

        return items;
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
