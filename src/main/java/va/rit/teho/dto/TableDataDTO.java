package va.rit.teho.dto;

import java.util.List;

public class TableDataDTO {
    private final List<String> rows;
    private final List<NestedColumnsDTO> columns;
    private final Double[][] data;

    public TableDataDTO(List<String> rows,
                        List<NestedColumnsDTO> columns, Double[][] data) {
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

    public Double[][] getData() {
        return data;
    }

}
