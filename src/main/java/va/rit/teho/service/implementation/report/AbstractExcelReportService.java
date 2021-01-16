package va.rit.teho.service.implementation.report;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportCellFunction;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.report.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class AbstractExcelReportService<T, R> implements ReportService<T> {

    private final CellStyle rowStyle;

    private final Font font;

    private static final Workbook wb = new HSSFWorkbook();

    public AbstractExcelReportService() {
        this.rowStyle = wb.createCellStyle();
        this.rowStyle.setWrapText(true);

        this.font = wb.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
    }

    protected abstract List<ReportCellFunction<R>> populateCellFunctions(T combinedData);

    protected abstract String reportName();

    private Sheet createSheet(String name) {
        return wb.createSheet(name);
    }

    protected Cell alignCellCenter(Cell c) {
        alignCell(c, HorizontalAlignment.CENTER);
        c.getCellStyle().setWrapText(true);
        c.getCellStyle().setShrinkToFit(true);
        return c;
    }

    protected void rotateCell(Cell c) {
        c.getCellStyle().setWrapText(true);
        c.getCellStyle().setShrinkToFit(true);
        c.getCellStyle().setRotation((short) 90);
    }

    protected ReportHeader header(String name) {
        return new ReportHeader(name, true, false);
    }

    protected ReportHeader header(String name, boolean centered) {
        return new ReportHeader(name, centered, false);
    }

    protected ReportHeader header(String name, boolean centered, boolean vertical) {
        return new ReportHeader(name, centered, vertical);
    }

    protected void createRowWideCell(Sheet sheet, int index, int colSize, String data, boolean bold, boolean centered) {
        Cell formationCell = sheet.createRow(index).createCell(0);

        if (centered) {
            alignCellCenter(formationCell);
        }

        if (bold) {
            setBoldFont(formationCell);
        }

        formationCell.setCellValue(data);

        mergeCells(sheet, index, index, 0, colSize);
    }

    protected abstract List<ReportHeader> buildHeader(T combinedData);

    @Override
    public byte[] generateReport(T data) {
        Sheet sheet = createSheet(reportName());

        final int[] lastRow = writeHeader(sheet, buildHeader(data));

        writeData(data, sheet, lastRow[0]);

        return writeSheet(sheet, lastRow[1]);
    }

    protected Cell setBoldFont(Cell c) {
        CellStyle style = c.getCellStyle() == null ? wb.createCellStyle() : c.getCellStyle();
        style.setFont(font);
        return c;
    }

    protected abstract int writeData(T data, Sheet sheet, int lastRowIndex);

    private void alignCell(Cell c, HorizontalAlignment horizontalAlignment) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        c.setCellStyle(cellStyle);
    }

    protected byte[] writeSheet(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i, true);
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            wb.write(outputStream);
            wb.removeSheetAt(wb.getSheetIndex(sheet));
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private int writeHeader(Sheet sheet, ReportHeader reportHeader, int cRow, int cCol, int lowestLevel) {
        int levelCount = 1;
        if (reportHeader.hasSubHeaders()) {
            int hLengthSum = 0;
            for (ReportHeader h : reportHeader.getSubHeaders()) {
                int headerLength = writeHeader(sheet, h, cRow + 1, cCol + hLengthSum, lowestLevel);
                hLengthSum += headerLength;
            }
            levelCount = hLengthSum;
            if (levelCount != 1) {
                mergeCells(sheet, cRow, cRow, cCol, cCol + levelCount - 1);
            }
        } else if (lowestLevel > cRow) {
            mergeCells(sheet, cRow, lowestLevel, cCol, cCol);
        }
        Row row = sheet.getRow(cRow) == null ? sheet.createRow(cRow) : sheet.getRow(cRow);
        row.setHeight((short) -1);
        row.setRowStyle(rowStyle);
        Cell cell = row.createCell(cCol);
        cell.setCellValue(reportHeader.getName());
        if (reportHeader.isCentered()) {
            alignCellCenter(cell);
        }
        if (reportHeader.isVertical()) {
            rotateCell(cell);
        }
        return levelCount;
    }

    protected void writeRows(Sheet sheet,
                             int rowStartIndex,
                             T combinedData,
                             Collection<R> data) {
        List<ReportCellFunction<R>> populateCellFunctions = populateCellFunctions(combinedData);
        int i = 0;
        for (R item : data) {
            Row row = sheet.createRow(rowStartIndex + i);
            row.setHeight((short) -1);
            for (int j = 0; j < populateCellFunctions.size(); j++) {
                Cell cell = row.createCell(j);
                ReportCell reportCell = populateCellFunctions.get(j).apply(item);
                cell.setCellValue(reportCell.getValue());
                if (reportCell.getAlignment().equals(HorizontalAlignment.CENTER)) {
                    alignCellCenter(cell);
                } else {
                    alignCell(cell, reportCell.getAlignment());
                }
            }
            i++;
        }
    }

    protected int[] writeHeader(Sheet sheet, List<ReportHeader> reportHeaderList) {
        int cCol = 0;
        int lowestLevel = reportHeaderList
                .stream()
                .map(ReportHeader::depth)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        for (ReportHeader reportHeader : reportHeaderList) {
            cCol += writeHeader(sheet, reportHeader, 0, cCol, lowestLevel);
        }
        return new int[]{lowestLevel + 1, cCol + 1};
    }

    protected void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

}
