package va.rit.teho.dto;

import va.rit.teho.entity.EquipmentSubType;

public class EquipmentSubTypeDTO {
    private final String shortName;
    private final String fullName;

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public EquipmentSubTypeDTO(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public static EquipmentSubTypeDTO from(EquipmentSubType equipmentSubType) {
        return new EquipmentSubTypeDTO(equipmentSubType.getShortName(), equipmentSubType.getFullName());
    }
}
