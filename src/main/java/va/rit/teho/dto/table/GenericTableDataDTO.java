package va.rit.teho.dto.table;

import java.util.List;

public class GenericTableDataDTO<T, D extends RowData<T>> {

    private final List<NestedColumnsDTO> columns;
    private final List<D> rows;
    private final Long totalPageNum;

    public GenericTableDataDTO(List<NestedColumnsDTO> columns,
                               List<D> rows) {
        this.columns = columns;
        this.rows = rows;
        this.totalPageNum = null;
    }

    public GenericTableDataDTO(List<NestedColumnsDTO> columns, List<D> rows, Long totalPageNum) {
        this.columns = columns;
        this.rows = rows;
        this.totalPageNum = totalPageNum;
    }

    public Long getTotalPageNum() {
        return totalPageNum;
    }

    public List<D> getRows() {
        return rows;
    }

    public List<NestedColumnsDTO> getColumns() {
        return columns;
    }
}
