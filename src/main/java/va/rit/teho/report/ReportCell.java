package va.rit.teho.report;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import va.rit.teho.exception.ExcelReportGeneratorException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class ReportCell {

    private static final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat("#.###");
    private static final DecimalFormat DOUBLE_FORMATTER_VERYSMALL = new DecimalFormat("#.#####");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final Object value;
    private final CellType cellType;
    private final HorizontalAlignment alignment;

    public ReportCell(Object value, CellType cellType) {
        this.value = value;
        this.cellType = cellType;
        this.alignment = HorizontalAlignment.CENTER;
    }

    public ReportCell(Object value) {
        this.value = value;
        this.cellType = CellType.TEXT;
        this.alignment = HorizontalAlignment.CENTER;
    }

    public ReportCell(Object value, CellType cellType, HorizontalAlignment alignment) {
        this.value = value;
        this.cellType = cellType;
        this.alignment = alignment;
    }

    public String getTextValue() {
        return value.toString();
    }

    public Double getNumericValue() {
        Double doubleValue = Double.parseDouble(getTextValue());
        DecimalFormat formatter = doubleValue < 0.001 ? DOUBLE_FORMATTER_VERYSMALL : DOUBLE_FORMATTER;
        try {
            return NUMBER_FORMAT.parse(formatter.format(doubleValue)).doubleValue();
        } catch (ParseException e) {
            throw new ExcelReportGeneratorException(e);
        }
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    public CellType getCellType() {
        return cellType;
    }

    public enum CellType {
        TEXT, NUMERIC
    }
}
