package va.rit.teho.report;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.function.Function;

public interface ReportCellFunction<T> extends Function<T, ReportCell> {

    static <T> ReportCellFunction<T> of(Function<T, Object> getter) {
        return ReportCellFunction.of(getter, ReportCell.CellType.TEXT);
    }

    static <T> ReportCellFunction<T> of(Function<T, Object> getter, ReportCell.CellType cellType) {
        return ReportCellFunction.of(getter, cellType, HorizontalAlignment.CENTER);
    }

    static <T> ReportCellFunction<T> of(Function<T, Object> getter,
                                        ReportCell.CellType cellType,
                                        HorizontalAlignment horizontalAlignment) {
        return (T data) -> new ReportCell(getter.apply(data), cellType, horizontalAlignment);
    }
}
