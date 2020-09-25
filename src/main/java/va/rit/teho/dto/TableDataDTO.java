package va.rit.teho.dto;

import java.util.List;
import java.util.Map;

public class TableDataDTO<T> {
    private final List<NestedColumnsDTO> columns;
    private final List<RowData<T>> rows;

    public TableDataDTO(List<NestedColumnsDTO> columns,
                        List<RowData<T>> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<RowData<T>> getRows() {
        return rows;
    }

    public List<NestedColumnsDTO> getColumns() {
        return columns;
    }

    public static class RowData<T> {
        private final String name;
        private final Map<String, T> data;

        public String getName() {
            return name;
        }

        public Map<String, T> getData() {
            return data;
        }

        public RowData(String name, Map<String, T> data) {
            this.name = name;
            this.data = data;
        }
    }
}
