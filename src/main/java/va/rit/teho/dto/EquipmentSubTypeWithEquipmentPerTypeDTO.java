package va.rit.teho.dto;

import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipmentSubTypeWithEquipmentPerTypeDTO {
    private EquipmentTypeDTO type;
    private List<EquipmentSubTypeDTOWithEquipment> subTypes;

    public EquipmentTypeDTO getType() {
        return type;
    }

    public List<EquipmentSubTypeDTOWithEquipment> getSubTypes() {
        return subTypes;
    }

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
}
