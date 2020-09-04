package va.rit.teho.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jxls.transform.poi.PoiTransformer;
import va.rit.teho.exception.ExcelReportGeneratorException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.BLANK;

public class RepairFundDistributionReportStyler implements ReportRowStyler {

    private static final int DATA_BEGINNING_ROW_INDEX = 5;
    private Sheet sheet;

    @Override
    public byte[] styleRows(byte[] file) {
        byte[] data;

        try (InputStream inputStream = new ByteArrayInputStream(file); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Workbook workbook = PoiTransformer.createTransformer(inputStream).getWorkbook();
            sheet = workbook.getSheetAt(0);

            for (int i = DATA_BEGINNING_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);

                if (rowIsSubtype(currentRow)) {
                    mergeAllCellsInRow(currentRow);
                    setBoldFontToCell(currentRow.getCell(0));
                } else if (rowIsTotalResult(currentRow)) {
                    mergeFirstTwoCellsInRow(currentRow);
                    setBoldFontToCell(currentRow.getCell(0));
                }
            }

            workbook.write(outputStream);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            throw new ExcelReportGeneratorException(e);
        }

        return data;
    }

    private boolean rowIsSubtype(Row row) {
        return allCellsAreBlank(row.getCell(1), row.getCell(2));
    }

    private boolean rowIsTotalResult(Row row) {
        return allCellsAreBlank(row.getCell(1));
    }

    private void mergeAllCellsInRow(Row row) {
        mergeCellsInRow(row, row.getCell(0).getColumnIndex(), row.getLastCellNum());
    }

    private void mergeCellsInRow(Row row, int firstColumnIndex, int lastColumnIndex) {
        String cellValue = row.getCell(0).getStringCellValue();
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstColumnIndex, lastColumnIndex));

        // перезаписываем значение из-за того, что при слиянии ячеек значение ячейки стирается
        row.getCell(0).setCellValue(cellValue);
    }

    private void setBoldFontToCell(Cell cell) {
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        cell.getRichStringCellValue().applyFont(font);
    }

    private void mergeFirstTwoCellsInRow(Row row) {
        mergeCellsInRow(row, row.getCell(0).getColumnIndex(), row.getCell(1).getColumnIndex());
    }

    private boolean allCellsAreBlank(Cell... cells) {
        return Stream.of(cells).allMatch(cell -> cell.getCellType().equals(BLANK));
    }
}
