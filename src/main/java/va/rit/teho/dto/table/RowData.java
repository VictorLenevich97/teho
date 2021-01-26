package va.rit.teho.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowData<T> {

    @Size(min = 3, max = 255)
    private final String name;

    private final T data;

    @Positive
    private Long id;

    public RowData(String name, T data) {
        this.name = name;
        this.data = data;
    }

    public RowData(Long id, String name, T data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public T getData() {
        return data;
    }

    public Long getId() {
        return id;
    }
}
