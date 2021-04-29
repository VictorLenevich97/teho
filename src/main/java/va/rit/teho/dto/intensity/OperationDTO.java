package va.rit.teho.dto.intensity;

import va.rit.teho.entity.intensity.Operation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OperationDTO {

    private Long id;

    @NotNull
    @Size(min = 2)
    private String name;

    private Boolean active;

    public OperationDTO(Long id, String name, Boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public static OperationDTO from(Operation op) {
        return new OperationDTO(op.getId(), op.getName(), op.isActive());
    }
}
