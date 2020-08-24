package va.rit.teho.dto;

import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentSubTypePerTypeDTO {
    private EquipmentTypeDTO type;
    private List<EquipmentSubTypeDTO> subTypes;

    public EquipmentTypeDTO getType() {
        return type;
    }

    public List<EquipmentSubTypeDTO> getSubTypes() {
        return subTypes;
    }

    public EquipmentSubTypePerTypeDTO(EquipmentTypeDTO type, List<EquipmentSubTypeDTO> subTypes) {
        this.type = type;
        this.subTypes = subTypes;
    }

    public static EquipmentSubTypePerTypeDTO from(EquipmentType type, Collection<EquipmentSubType> subTypes) {
        List<EquipmentSubTypeDTO> equipmentSubTypeDTOListdtoList =
                subTypes.stream().map(EquipmentSubTypeDTO::from).collect(Collectors.toList());
        return new EquipmentSubTypePerTypeDTO(EquipmentTypeDTO.from(type), equipmentSubTypeDTOListdtoList);
    }
}
