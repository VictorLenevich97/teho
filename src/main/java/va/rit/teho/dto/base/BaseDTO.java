package va.rit.teho.dto.base;

import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.entity.base.Base;

public class BaseDTO extends AbstractNamedDTO {
    private Long id;

    public BaseDTO(Long id, String shortName, String fullName) {
        super(shortName, fullName);
        this.id = id;
    }

    public static BaseDTO from(Base base) {
        return new BaseDTO(base.getId(), base.getShortName(), base.getFullName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
