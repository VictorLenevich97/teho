package va.rit.teho.dto;

import java.util.List;

public class TableDataDTO<T> {
    private final List<String> rows;
    private final List<NestedColumnsDTO> columns;
    private final T[][] data;

    public TableDataDTO(List<String> rows,
                        List<NestedColumnsDTO> columns,
                        T[][] data) {
        this.rows = rows;
        this.columns = columns;
        this.data = data;
    }

    public List<String> getRows() {
        return rows;
    }

    public List<NestedColumnsDTO> getColumns() {
        return columns;
    }

    public T[][] getData() {
        return data;
    }

}
