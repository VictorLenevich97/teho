package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RowData<T> {
        private final Long id;
        private final String name;
        private final Map<String, T> data;

        public String getName() {
            return name;
        }

        public Map<String, T> getData() {
            return data;
        }

        public Long getId() {
            return id;
        }

        public RowData(Long id, String name, Map<String, T> data) {
            this.id = id;
            this.name = name;
            this.data = data;
        }
    }
}
