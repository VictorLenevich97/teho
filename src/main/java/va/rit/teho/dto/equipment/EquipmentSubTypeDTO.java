package va.rit.teho.dto.equipment;

import va.rit.teho.dto.AbstractNamedDTO;
import va.rit.teho.entity.EquipmentSubType;

public class EquipmentSubTypeDTO extends AbstractNamedDTO {

    private Long key;

    public EquipmentSubTypeDTO(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public EquipmentSubTypeDTO(Long key, String shortName, String fullName) {
        super(shortName, fullName);
        this.key = key;
    }

    public static EquipmentSubTypeDTO from(EquipmentSubType equipmentSubType) {
        return new EquipmentSubTypeDTO(equipmentSubType.getId(),
                                       equipmentSubType.getShortName(),
                                       equipmentSubType.getFullName());
    }

    public Long getKey() {
        return key;
    }
}
