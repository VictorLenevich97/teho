package va.rit.teho.dto;

import va.rit.teho.entity.Base;

public class BaseDTO extends AbstractNamedDTO {
    private Long key;

    public BaseDTO(Long key, String shortName, String fullName) {
        super(shortName, fullName);
        this.key = key;
    }

    public static BaseDTO from(Base base) {
        return new BaseDTO(base.getId(), base.getShortName(), base.getFullName());
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }
}
