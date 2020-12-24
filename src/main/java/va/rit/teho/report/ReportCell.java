package va.rit.teho.report;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class ReportCell {
    private final String value;
    private final HorizontalAlignment alignment;

    public ReportCell(String value) {
        this.value = value;
        this.alignment = HorizontalAlignment.CENTER;
    }

    public ReportCell(String value, HorizontalAlignment alignment) {
        this.value = value;
        this.alignment = alignment;
    }

    public String getValue() {
        return value;
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }
}
