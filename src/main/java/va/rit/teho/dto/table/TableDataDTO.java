package va.rit.teho.dto.table;

import va.rit.teho.dto.NestedColumnsDTO;

import java.util.List;

public class TableDataDTO<T> {
    private final List<NestedColumnsDTO> columns;
    private final List<? extends RowData<T>> rows;

    public TableDataDTO(List<NestedColumnsDTO> columns,
                        List<? extends RowData<T>> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<? extends RowData<T>> getRows() {
        return rows;
    }

    public List<NestedColumnsDTO> getColumns() {
        return columns;
    }

}
