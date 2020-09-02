package va.rit.teho.dto.equipment;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipmentSubTypeWithEquipmentPerTypeDTO {
    private final EquipmentTypeDTO type;
    private final List<EquipmentSubTypeDTOWithEquipment> subTypes;

    public EquipmentSubTypeWithEquipmentPerTypeDTO(EquipmentTypeDTO type,
                                                   List<EquipmentSubTypeDTOWithEquipment> subTypes) {
        this.type = type;
        this.subTypes = subTypes;
    }

    public static EquipmentSubTypeWithEquipmentPerTypeDTO from(EquipmentType type,
                                                               Map<EquipmentSubType, List<Equipment>> equipmentSubTypeListMap) {
        return new EquipmentSubTypeWithEquipmentPerTypeDTO(EquipmentTypeDTO.from(type),
                                                           equipmentSubTypeListMap
                                                                   .entrySet()
                                                                   .stream()
                                                                   .map(subTypeListEntry ->
                                                                                EquipmentSubTypeDTOWithEquipment.from(
                                                                                        subTypeListEntry.getKey(),
                                                                                        subTypeListEntry.getValue()))
                                                                   .collect(Collectors.toList()));
    }

    public EquipmentTypeDTO getType() {
        return type;
    }

    public List<EquipmentSubTypeDTOWithEquipment> getSubTypes() {
        return subTypes;
    }
}
