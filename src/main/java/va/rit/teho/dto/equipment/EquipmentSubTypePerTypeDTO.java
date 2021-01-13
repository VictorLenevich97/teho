package va.rit.teho.dto.equipment;

import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EquipmentSubTypePerTypeDTO {
    private final EquipmentTypeDTO type;
    private final List<EquipmentSubTypeDTO> subTypes;

    public EquipmentSubTypePerTypeDTO(EquipmentTypeDTO type, List<EquipmentSubTypeDTO> subTypes) {
        this.type = type;
        this.subTypes = subTypes;
    }

    public static Stream<EquipmentSubTypePerTypeDTO> from(EquipmentType type, Collection<EquipmentSubType> subTypes) {
        List<EquipmentSubTypeDTO> equipmentSubTypeDTOList =
                subTypes.stream().map(EquipmentSubTypeDTO::from).collect(Collectors.toList());
        if (type == null) {
            return equipmentSubTypeDTOList
                    .stream()
                    .map(equipmentSubTypeDTO -> new EquipmentSubTypePerTypeDTO(new EquipmentTypeDTO(equipmentSubTypeDTO.getId(),
                                                                                                    equipmentSubTypeDTO.getShortName(),
                                                                                                    equipmentSubTypeDTO.getFullName()),
                                                                               Collections.emptyList()));
        }
        return Stream.of(new EquipmentSubTypePerTypeDTO(EquipmentTypeDTO.from(type), equipmentSubTypeDTOList));
    }

    public EquipmentTypeDTO getType() {
        return type;
    }

    public List<EquipmentSubTypeDTO> getSubTypes() {
        return subTypes;
    }
}
