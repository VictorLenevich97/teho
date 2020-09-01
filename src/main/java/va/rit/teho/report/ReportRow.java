package va.rit.teho.report;

import java.util.ArrayList;
import java.util.List;

public class ReportRow {

    private List row;

    public ReportRow() {
        row = new ArrayList<>();
    }

    public ReportRow(List row) {
        this.row = row;
    }

    public List getRow() {
        return row;
    }

    public void setRow(List row) {
        this.row = row;
    }
}
