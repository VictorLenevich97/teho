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

    protected Sheet createSheet(String name) {
        return wb.createSheet(name);
    }

    protected Cell alignCellCenter(Cell c) {
        alignCell(c, HorizontalAlignment.CENTER);
        return c;
    }

    private void alignCell(Cell c, HorizontalAlignment horizontalAlignment) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        c.setCellStyle(cellStyle);
    }

    protected byte[] writeSheet(Sheet sheet) {
        int columnCount = populateCellFunctions().size();
        for(int i = 0; i < columnCount; i++) {
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

    private int writeHeader(Sheet sheet, va.rit.teho.report.Header header, int cRow, int cCol, int lowestLevel) {
        int levelCount = 1;
        if (header.hasChildren()) {
            int hLengthSum = 0;
            for (va.rit.teho.report.Header h : header.getChildren()) {
                int headerLength = writeHeader(sheet, h, cRow + 1, cCol + hLengthSum, lowestLevel);
                hLengthSum += headerLength;
            }
            levelCount = hLengthSum;
            mergeCells(sheet, cRow, cRow, cCol, cCol + levelCount - 1);
        } else if (lowestLevel > cRow) {
            mergeCells(sheet, cRow, lowestLevel, cCol, cCol);
        }
        Row row = sheet.getRow(cRow) == null ? sheet.createRow(cRow) : sheet.getRow(cRow);
        Cell cell = row.createCell(cCol);
        cell.setCellValue(header.getName());
        if (header.isCentered()) {
            alignCellCenter(cell);
        }
        return levelCount;
    }

    protected void writeRows(Sheet sheet,
                             int rowStartIndex,
                             Collection<R> data) {
        List<Function<R, ReportCell>> populateCellFunctions = populateCellFunctions();
        int i = 0;
        for (R item : data) {
            Row row = sheet.createRow(rowStartIndex + i);
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

    protected int writeHeader(Sheet sheet, List<va.rit.teho.report.Header> headerList) {
        int cCol = 0;
        int lowestLevel = headerList
                .stream()
                .map(va.rit.teho.report.Header::depth)
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
