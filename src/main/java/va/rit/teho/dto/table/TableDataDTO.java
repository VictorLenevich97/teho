package va.rit.teho.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableDataDTO<T> {
    private final List<NestedColumnsDTO> columns;
    private final List<? extends RowData<T>> rows;
    private final Long totalPageNum;

    public TableDataDTO(List<NestedColumnsDTO> columns,
                        List<? extends RowData<T>> rows) {
        this.columns = columns;
        this.rows = rows;
        this.totalPageNum = null;
    }

    public TableDataDTO(List<NestedColumnsDTO> columns, List<? extends RowData<T>> rows, Long totalPageNum) {
        this.columns = columns;
        this.rows = rows;
        this.totalPageNum = totalPageNum;
    }

    public Long getTotalPageNum() {
        return totalPageNum;
    }

    public List<? extends RowData<T>> getRows() {
        return rows;
    }

    public List<NestedColumnsDTO> getColumns() {
        return columns;
    }

}
