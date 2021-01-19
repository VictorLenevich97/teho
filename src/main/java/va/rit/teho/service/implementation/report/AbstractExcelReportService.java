package va.rit.teho.service.implementation.report;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import va.rit.teho.report.ReportCell;
import va.rit.teho.report.ReportHeader;
import va.rit.teho.service.report.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class AbstractExcelReportService<T, R> implements ReportService<T> {

    private static final Workbook wb = new HSSFWorkbook();

    private final CellStyle rowStyle;

    //Из-за ограничения Excel на количество стилей пришлось создать 5 разных
    private final CellStyle baseCellStyle;
    private final CellStyle boldCellStyle;
    private final CellStyle centeredCellStyle;
    private final CellStyle centeredRotatedCellStyle;
    private final CellStyle centeredBoldCellStyle;

    public AbstractExcelReportService() {
        this.rowStyle = wb.createCellStyle();
        this.rowStyle.setWrapText(true);
        this.rowStyle.setShrinkToFit(true);

        Font defaultFont = wb.createFont();
        defaultFont.setFontName(HSSFFont.FONT_ARIAL);
        defaultFont.setFontHeightInPoints((short) 10);
        this.rowStyle.setFont(defaultFont);

        Font boldFont = wb.createFont();
        boldFont.setFontName(HSSFFont.FONT_ARIAL);
        boldFont.setFontHeightInPoints((short) 10);
        boldFont.setBold(true);

        this.baseCellStyle = createCellStyle();
        this.baseCellStyle.setFont(defaultFont);

        this.boldCellStyle = createCellStyle();
        this.boldCellStyle.setFont(boldFont);

        this.centeredCellStyle = centerCellStyle(createCellStyle());

        this.centeredBoldCellStyle = centerCellStyle(createCellStyle());
        this.centeredBoldCellStyle.setFont(boldFont);

        this.centeredRotatedCellStyle = centerCellStyle(createCellStyle());
        this.centeredRotatedCellStyle.setRotation((short) 90);
    }

    private CellStyle centerCellStyle(CellStyle c) {
        c.setAlignment(HorizontalAlignment.CENTER);
        c.setVerticalAlignment(VerticalAlignment.CENTER);
        return c;
    }

    private CellStyle createCellStyle() {
        CellStyle c = wb.createCellStyle();
        c.setBorderTop(BorderStyle.THIN);
        c.setBorderLeft(BorderStyle.THIN);
        c.setBorderRight(BorderStyle.THIN);
        c.setBorderBottom(BorderStyle.THIN);
        return c;
    }

    protected abstract List<ReportCell> populatedRowCells(T combinedData, R row);

    protected abstract String reportName();

    private Sheet createSheet(String name) {
        return wb.createSheet(name);
    }

    protected void alignCenterAndSetBold(Cell c) {
        c.setCellStyle(centeredBoldCellStyle);
    }

    protected void alignCellCenter(Cell c) {
        c.setCellStyle(centeredCellStyle);
    }

    protected void rotateCell(Cell c) {
        c.setCellStyle(centeredRotatedCellStyle);
    }

    protected ReportHeader header(String name) {
        return header(name, false);
    }

    protected ReportHeader header(String name, boolean vertical) {
        return new ReportHeader(name, vertical);
    }

    protected void createRowWideCell(Sheet sheet, int index, int colSize, String data, boolean bold, boolean centered) {
        Row row = sheet.createRow(index);
        Cell rowWideCell = row.createCell(0);

        if (centered && bold) {
            alignCenterAndSetBold(rowWideCell);
        } else if (centered) {
            alignCellCenter(rowWideCell);
        } else if (bold) {
            setBoldFont(rowWideCell);
        }

        rowWideCell.setCellValue(data);

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

    protected void setBoldFont(Cell c) {
        c.setCellStyle(boldCellStyle);
    }

    protected abstract void writeData(T data, Sheet sheet, int lastRowIndex);

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
        //Авто-выравнивание высоты строки
        row.setHeight((short) -1);
        //Применение общего стиля (перенос текста)
        row.setRowStyle(rowStyle);

        Cell cell = row.createCell(cCol);
        cell.setCellValue(reportHeader.getName());
        alignCellCenter(cell);

        if (reportHeader.isVertical()) {
            rotateCell(cell);
        }

        return levelCount;
    }

    protected void writeRows(Sheet sheet,
                             int rowStartIndex,
                             T combinedData,
                             Collection<R> data) {
        int i = 0;
        for (R item : data) {
            List<ReportCell> reportCells = populatedRowCells(combinedData, item);
            Row row = sheet.createRow(rowStartIndex + i);
            row.setHeight((short) -1);
            for (int j = 0; j < reportCells.size(); j++) {
                Cell cell = row.createCell(j);
                ReportCell reportCell = reportCells.get(j);
                cell.setCellValue(reportCell.getValue());
                if (reportCell.getAlignment().equals(HorizontalAlignment.CENTER)) {
                    alignCellCenter(cell);
                }
                if (cell.getCellStyle().getBorderBottom().equals(BorderStyle.NONE)) {
                    cell.setCellStyle(baseCellStyle);
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
        //Применяем базовый стиль на все ячейки, чтобы у области была видимая граница
        for (int i = firstRow; i <= lastRow; i++) {
            Row row = sheet.getRow(i) == null ? sheet.createRow(i) : sheet.getRow(i);
            for (int j = firstCol; j <= lastCol; j++) {
                if (row.getCell(j) == null) {
                    row.createCell(j).setCellStyle(this.baseCellStyle);
                }
            }
        }

        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

}
