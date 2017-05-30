package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;
import fr.cnes.sonar.report.model.Issue;
import fr.cnes.sonar.report.model.Report;
import fr.cnes.sonar.report.params.Params;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports the report in .docx format
 * @author begarco
 */
public class XlsXExporter implements IExporter {

    @Override
    public void export(Object data, Params params, String filename) throws Exception {
        if(!(data instanceof Report)) {
            throw new BadExportationDataTypeException();
        }
        Report report = (Report) data;

        String outputfilepath = params.get("report.path")+"/"+filename;

        SpreadsheetMLPackage pkg = SpreadsheetMLPackage.createPackage();

        WorksheetPart sheet = pkg.createWorksheetPart(new PartName("/xl/worksheets/sheet1.xml"), "Issues", 1);

        addContent(sheet, report);

        pkg.save(new File(outputfilepath));
    }

    /**
     * Fill the sheet
     * @param sheet Sheet to fill
     * @param report Data to put inside
     */
    private void addContent(WorksheetPart sheet, Report report) {
        // Minimal content already present
        SheetData sheetData = sheet.getJaxbElement().getSheetData();

        // Issues
        List<Issue> issues = report.getIssues();

        // Add header
        Row row = Context.getsmlObjectFactory().createRow();
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(createCell("rule"));
        cells.add(createCell("message"));
        cells.add(createCell("type"));
        cells.add(createCell("severity"));
        cells.add(createCell("file"));
        cells.add(createCell("line"));
        row.getC().addAll(cells);
        sheetData.getRow().add(row);

        // add issues
        for(Issue issue : issues) {
            // initialization of a new row
            row = Context.getsmlObjectFactory().createRow();
            cells = new ArrayList<>();

            // adding data
            cells.add(createCell(issue.getRule()));
            cells.add(createCell(issue.getMessage()));
            cells.add(createCell(issue.getType()));
            cells.add(createCell(issue.getSeverity()));
            cells.add(createCell(issue.getComponent()));
            cells.add(createCell(issue.getLine()));

            // adding a new row
            row.getC().addAll(cells);
            sheetData.getRow().add(row);
        }

    }

    /**
     * Create a cell able to contain strings
     * @param content string to put in the cell
     * @return a new cell
     */
    private static Cell createCell(String content) {

        Cell cell = Context.getsmlObjectFactory().createCell();

        CTXstringWhitespace ctx = Context.getsmlObjectFactory().createCTXstringWhitespace();
        ctx.setValue(content);

        CTRst ctrst = new CTRst();
        ctrst.setT(ctx);

        cell.setT(STCellType.INLINE_STR);
        cell.setIs(ctrst); // add ctrst as inline string

        return cell;

    }

}
