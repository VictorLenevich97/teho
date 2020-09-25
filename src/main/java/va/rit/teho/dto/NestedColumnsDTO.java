package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedColumnsDTO {
    private final String title;
    private String key;
    private List<NestedColumnsDTO> columns;

    public String getKey() {
        return key;
    }

    public NestedColumnsDTO(Object key, String title) {
        this.key = key.toString();
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
