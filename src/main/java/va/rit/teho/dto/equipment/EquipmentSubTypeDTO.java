package va.rit.teho.dto.equipment;

import va.rit.teho.dto.AbstractNamedDTO;
import va.rit.teho.entity.EquipmentSubType;

public class EquipmentSubTypeDTO extends AbstractNamedDTO {

    public EquipmentSubTypeDTO(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public static EquipmentSubTypeDTO from(EquipmentSubType equipmentSubType) {
        return new EquipmentSubTypeDTO(equipmentSubType.getShortName(), equipmentSubType.getFullName());
    }
}
