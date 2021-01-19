package va.rit.teho.report;

import java.util.ArrayList;
import java.util.List;

public class ReportHeader {
    private final String name;
    private final boolean vertical;
    private final List<ReportHeader> subHeaders;

    public ReportHeader(String name, boolean vertical) {
        this.name = name;
        this.vertical = vertical;
        this.subHeaders = new ArrayList<>();
    }

    public ReportHeader(String name, List<ReportHeader> subHeaders) {
        this.name = name;
        this.subHeaders = subHeaders;
        this.vertical = false;
    }

    public String getName() {
        return name;
    }

    public List<ReportHeader> getSubHeaders() {
        return subHeaders;
    }

    public boolean hasSubHeaders() {
        return !subHeaders.isEmpty();
    }

    public void addSubHeader(ReportHeader children) {
        this.subHeaders.add(children);
    }

    public int depth() {
        return depth(this, 0);
    }

    private int depth(ReportHeader reportHeader, int depth) {
        if (reportHeader.hasSubHeaders()) {
            return reportHeader
                    .subHeaders
                    .stream()
                    .map(h -> depth(h, depth + 1))
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(depth);
        }
        return depth;
    }

    public boolean isVertical() {
        return vertical;
    }
}
