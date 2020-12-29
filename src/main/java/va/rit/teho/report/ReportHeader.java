package va.rit.teho.report;

import java.util.ArrayList;
import java.util.List;

public class ReportHeader {
    private final String name;
    private final boolean centered;
    private final boolean vertical;
    private final List<ReportHeader> subHeaders;

    public ReportHeader(String name, boolean centered, boolean vertical) {
        this.name = name;
        this.centered = centered;
        this.vertical = vertical;
        this.subHeaders = new ArrayList<>();
    }

    public ReportHeader(String name, List<ReportHeader> subHeaders) {
        this.name = name;
        this.subHeaders = subHeaders;
        this.vertical = false;
        this.centered = false;
    }

    public ReportHeader(String name, boolean centered, List<ReportHeader> subHeaders) {
        this.name = name;
        this.subHeaders = subHeaders;
        this.vertical = false;
        this.centered = centered;
    }

    public boolean isCentered() {
        return centered;
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

    public ReportHeader addSubHeader(ReportHeader children) {
        this.subHeaders.add(children);
        return this;
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
