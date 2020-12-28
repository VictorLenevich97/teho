package va.rit.teho.service.implementation.report;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import va.rit.teho.report.Header;
import va.rit.teho.report.ReportCell;
import va.rit.teho.service.report.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractReportService<T, R> implements ReportService<T> {

    private static final Workbook wb = new HSSFWorkbook();

    protected abstract List<Function<R, ReportCell>> populateCellFunctions();

    protected abstract String reportName();

    private Sheet createSheet(String name) {
        return wb.createSheet(name);
    }

    protected Cell alignCellCenter(Cell c) {
        alignCell(c, HorizontalAlignment.CENTER);
        return c;
    }

    protected void rotateCell(Cell c) {
        c.getCellStyle().setWrapText(true);
        c.getCellStyle().setShrinkToFit(true);
        c.getCellStyle().setRotation((short) 90);
    }

    protected abstract List<Header> buildHeader();

    @Override
    public byte[] generateReport(T data) {
        Sheet sheet = createSheet(reportName());

        final int[] lastRow = {writeHeader(sheet, buildHeader()) + 1};

        writeData(data, sheet, lastRow);

        return writeSheet(sheet);
    }

    protected abstract void writeData(T data, Sheet sheet, int[] lastRow);

    private void alignCell(Cell c, HorizontalAlignment horizontalAlignment) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        c.setCellStyle(cellStyle);
    }

    protected byte[] writeSheet(Sheet sheet) {
        int columnCount = populateCellFunctions().size();
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

    private int writeHeader(Sheet sheet, Header header, int cRow, int cCol, int lowestLevel) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        int levelCount = 1;
        if (header.hasChildren()) {
            int hLengthSum = 0;
            for (Header h : header.getChildren()) {
                int headerLength = writeHeader(sheet, h, cRow + 1, cCol + hLengthSum, lowestLevel);
                hLengthSum += headerLength;
            }
            levelCount = hLengthSum;
            mergeCells(sheet, cRow, cRow, cCol, cCol + levelCount - 1);
        } else if (lowestLevel > cRow) {
            mergeCells(sheet, cRow, lowestLevel, cCol, cCol);
        }
        Row row = sheet.getRow(cRow) == null ? sheet.createRow(cRow) : sheet.getRow(cRow);
        row.setHeight((short) -1);
        row.setRowStyle(cellStyle);
        Cell cell = row.createCell(cCol);
        cell.setCellValue(header.getName());
        if (header.isCentered()) {
            alignCellCenter(cell);
        }
        if (header.isVertical()) {
            rotateCell(cell);
        }
        return levelCount;
    }

    protected void writeRows(Sheet sheet,
                             int rowStartIndex,
                             Collection<R> data) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        List<Function<R, ReportCell>> populateCellFunctions = populateCellFunctions();
        int i = 0;
        for (R item : data) {
            Row row = sheet.createRow(rowStartIndex + i);
            row.setHeight((short) -1);
            row.setRowStyle(cellStyle);
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

    protected int writeHeader(Sheet sheet, List<Header> headerList) {
        int cCol = 0;
        int lowestLevel = headerList
                .stream()
                .map(Header::depth)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        for (Header header : headerList) {
            cCol += writeHeader(sheet, header, 0, cCol, lowestLevel);
        }
        return lowestLevel;
    }

    protected void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

}
