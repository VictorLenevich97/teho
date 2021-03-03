package va.rit.teho.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableDataDTO<T> extends GenericTableDataDTO<T, RowData<T>> {

    public TableDataDTO(List<NestedColumnsDTO> columns, List<RowData<T>> rows) {
        super(columns, rows);
    }

    public TableDataDTO(List<NestedColumnsDTO> columns, List<RowData<T>> rows, Long totalPageNum) {
        super(columns, rows, totalPageNum);
    }
}
