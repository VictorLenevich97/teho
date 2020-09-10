package va.rit.teho.dto.equipment;

import va.rit.teho.dto.AbstractNamedDTO;
import va.rit.teho.entity.EquipmentType;

public class EquipmentTypeDTO extends AbstractNamedDTO {

    private Long key;

    public EquipmentTypeDTO(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public EquipmentTypeDTO(Long key, String shortName, String fullName) {
        super(shortName, fullName);
        this.key = key;
    }

    public static EquipmentTypeDTO from(EquipmentType equipmentType) {
        return new EquipmentTypeDTO(equipmentType.getId(), equipmentType.getShortName(), equipmentType.getFullName());
    }

    public Long getKey() {
        return key;
    }
}
