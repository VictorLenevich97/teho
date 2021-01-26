package va.rit.teho.dto.common;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class IdAndNameDTO {

    @Positive
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 255)
    private String name;

    public IdAndNameDTO() {
    }

    public IdAndNameDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
