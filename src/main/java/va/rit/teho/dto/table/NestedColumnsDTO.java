package va.rit.teho.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedColumnsDTO {
    private String title;
    private Object key;
    private List<NestedColumnsDTO> columns;

    public Object getKey() {
        return key;
    }

    public NestedColumnsDTO(List<NestedColumnsDTO> columns) {
        this.columns = columns;
    }

    public NestedColumnsDTO(String title, Object key, List<NestedColumnsDTO> columns) {
        this.title = title;
        this.key = key;
        this.columns = columns;
    }

    public NestedColumnsDTO(Object key, String title) {
        this.key = key;
        this.title = title;
    }

    public NestedColumnsDTO(String title, List<NestedColumnsDTO> columns) {
        this.title = title;
        this.columns = columns;
    }

    public String getTitle() {
        return title;
    }

    public List<NestedColumnsDTO> getColumns() {
        return columns;
    }
}
