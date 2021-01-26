package va.rit.teho.dto.equipment;

import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.entity.equipment.EquipmentType;

import javax.validation.constraints.Positive;

public class EquipmentTypeDTO extends AbstractNamedDTO {

    @Positive
    private Long id;

    public EquipmentTypeDTO() {
    }

    public EquipmentTypeDTO(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public EquipmentTypeDTO(Long id, String shortName, String fullName) {
        super(shortName, fullName);
        this.id = id;
    }

    public static EquipmentTypeDTO from(EquipmentType equipmentType) {
        return new EquipmentTypeDTO(equipmentType.getId(), equipmentType.getShortName(), equipmentType.getFullName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
