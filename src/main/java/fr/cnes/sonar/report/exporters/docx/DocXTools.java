package fr.cnes.sonar.report.exporters.docx;

import org.docx4j.XmlUtils;
import org.docx4j.model.structure.HeaderFooterPolicy;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * Different tools to manipulate docx
 * @author garconb
 */
public final class DocXTools {

    /**
     * Private constructor to hide the public one
     */
    private DocXTools() {}

    /**
     * Utils to get all the elements inside a specific element
     * @param object object to explore
     * @param toSearch class to search
     * @return list of contained objects
     */
    public static List<Object> getAllElementsFromObject(Object object, Class<?> toSearch) {
        // object to process
        Object obj = object;

        // create a list of results
        List<Object> result = new ArrayList<>();
        // get the value of the JAXBElement if it is
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }

        // add current object if it is wanted
        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        }
        // check children
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementsFromObject(child, toSearch));
            }

        }
        return result;
    }

    /**
     * Replace all placeholder in a word file
     * @param template word file
     * @param name value to put
     * @param placeholder placeholder to replace
     */
    static void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder ) {
        // All interesting Text zones
        List<Object> texts = getAllElementsFromObject(template.getMainDocumentPart(), Text.class);

        // Get headers
        List<SectionWrapper> sectionWrappers = template.getDocumentModel().getSections();
        for (SectionWrapper sw : sectionWrappers) {
            HeaderFooterPolicy hfp = sw.getHeaderFooterPolicy();
            if (hfp.getFirstHeader() != null) { // get first header
                texts.addAll(getAllElementsFromObject(hfp.getDefaultHeader(), Text.class));
            }
            if (hfp.getDefaultHeader() != null) { // get default header
                texts.addAll(getAllElementsFromObject(hfp.getDefaultHeader(), Text.class));
            }
        }

        // replace in all Text zones
        for (Object text : texts) {
            Text textElement = (Text) text;
            if (textElement.getValue().contains(placeholder)) {
                textElement.setValue(textElement.getValue().replaceFirst(placeholder, name));
            }
        }
    }

    /**
     * Fill out a table
     * @param numTable index of the table
     * @param textToAdd list of lines to add
     * @param template template to fill out
     */
    static void replaceTable(int numTable, List<List<String>> textToAdd,
                              WordprocessingMLPackage template) {
        List<Object> tables = getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class);

        // 1. find the table
        Tbl tempTable = (Tbl)tables.get(numTable);
        List<Object> rows = getAllElementsFromObject(tempTable, Tr.class);

        // first row is header, second row is content
        if (rows.size() == 2) {
            // get the template row
            Tr templateRow = (Tr) rows.get(1);

            for (List<String> replacements : textToAdd) {
                // add each row
                addRowToTable(tempTable, templateRow, replacements);
            }

            // remove the template row
            tempTable.getContent().remove(templateRow);
        }
    }

    /**
     * Return a wanted table
     * @param tables list of table to explore
     * @param templateKey key contained in the template
     * @return wanted table
     * @throws Docx4JException docx4j exception
     * @throws JAXBException jaxb exception
     */
    static Tbl getTemplateTable(List<Object> tables, String templateKey) throws Docx4JException, JAXBException {
        for (Object tbl : tables) {
            List<?> textElements = getAllElementsFromObject(tbl, Text.class);
            for (Object text : textElements) {
                Text textElement = (Text) text;
                if (textElement.getValue() != null && textElement.getValue().equals(templateKey)) {
                    return (Tbl) tbl;
                }
            }
        }
        return null;
    }

    /**
     * Add a row to a table
     * @param reviewTable table to fill out
     * @param templateRow template of a row
     * @param cells elements of the row
     */
    static void addRowToTable(Tbl reviewTable, Tr templateRow, List<String> cells) {
        Tr workingRow = XmlUtils.deepCopy(templateRow);

        // find all text (cells)
        List<?> textElements = getAllElementsFromObject(workingRow, Text.class);

        // Adds all elements in the row
        int it = 0;
        for (Object object : textElements) {
            Text text = (Text) object;
            String replacementValue = cells.get(it);
            // increment the index
            it++;
            if (replacementValue != null) {
                text.setValue(replacementValue);
            }
        }

        // adds the row in the table
        reviewTable.getContent().add(workingRow);
    }
}
