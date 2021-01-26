package va.rit.teho.dto.equipment;

import va.rit.teho.entity.equipment.EquipmentPerFormation;

public class EquipmentPerFormationDTO {
    private final Long equipmentId;
    private final String equipmentName;
    private final int amount;

    public EquipmentPerFormationDTO(Long equipmentId, String equipmentName, int amount) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.amount = amount;
    }

    public static EquipmentPerFormationDTO from(EquipmentPerFormation equipmentPerFormation) {
        return new EquipmentPerFormationDTO(equipmentPerFormation.getEquipment().getId(),
                                            equipmentPerFormation.getEquipment().getName(),
                                            equipmentPerFormation.getAmount());
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public int getAmount() {
        return amount;
    }
}
