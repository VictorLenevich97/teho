package va.rit.teho.dto.equipment;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;

import java.util.List;
import java.util.stream.Collectors;

public class EquipmentSubTypeDTOWithEquipment extends EquipmentSubTypeDTO {
    private final List<EquipmentDTO> equipment;

    public EquipmentSubTypeDTOWithEquipment(String shortName, String fullName, List<EquipmentDTO> equipment) {
        super(shortName, fullName);
        this.equipment = equipment;
    }

    public static EquipmentSubTypeDTOWithEquipment from(EquipmentSubType st, List<Equipment> equipment) {
        List<EquipmentDTO> equipmentDTOList = equipment.stream()
                                                       .map(EquipmentDTO::idAndNameFrom)
                                                       .collect(Collectors.toList());
        return new EquipmentSubTypeDTOWithEquipment(st.getShortName(), st.getFullName(), equipmentDTOList);
    }

    public List<EquipmentDTO> getEquipment() {
        return equipment;
    }
}
