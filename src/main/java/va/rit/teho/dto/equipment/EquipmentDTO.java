package va.rit.teho.dto.equipment;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.equipment.Equipment;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDTO {
    private Long id;
    private String name;
    private EquipmentTypeDTO type;
    private EquipmentSubTypeDTO subType;

    public EquipmentDTO() {
    }

    public EquipmentDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public EquipmentDTO(String name) {
        this.name = name;
    }

    public static EquipmentDTO idAndNameFrom(Equipment equipment) {
        return new EquipmentDTO(equipment.getId(), equipment.getName());
    }

    public static EquipmentDTO from(Equipment equipment) {
        EquipmentDTO equipmentDTO = new EquipmentDTO();
        equipmentDTO.setId(equipment.getId());
        equipmentDTO.setName(equipment.getName());
        equipmentDTO.setSubType(EquipmentSubTypeDTO.from(equipment.getEquipmentSubType()));
        equipmentDTO.setType(EquipmentTypeDTO.from(equipment.getEquipmentSubType().getEquipmentType()));
        return equipmentDTO;
    }

    public EquipmentTypeDTO getType() {
        return type;
    }

    public void setType(EquipmentTypeDTO type) {
        this.type = type;
    }

    public EquipmentSubTypeDTO getSubType() {
        return subType;
    }

    public void setSubType(EquipmentSubTypeDTO subType) {
        this.subType = subType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
