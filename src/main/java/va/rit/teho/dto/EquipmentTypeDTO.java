package va.rit.teho.dto;

import va.rit.teho.entity.EquipmentType;

public class EquipmentTypeDTO extends AbstractNamedDTO {

    public EquipmentTypeDTO(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public static EquipmentTypeDTO from(EquipmentType equipmentType) {
        return new EquipmentTypeDTO(equipmentType.getShortName(), equipmentType.getFullName());
    }
}
