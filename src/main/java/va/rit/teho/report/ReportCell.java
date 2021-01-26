package va.rit.teho.report;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.text.DecimalFormat;

public class ReportCell {
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

    public String getNumericValue() {
        return new DecimalFormat("#.##").format(Double.parseDouble(value.toString()));
    }

    public String getValue() {
        if (cellType.equals(CellType.TEXT)) {
            return getTextValue();
        } else {
            return getNumericValue();
        }
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    public enum CellType {
        TEXT, NUMERIC
    }
}
