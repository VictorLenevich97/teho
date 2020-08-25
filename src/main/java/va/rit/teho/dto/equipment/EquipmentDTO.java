package va.rit.teho.dto.equipment;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.Equipment;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDTO {
    private Long key;
    private String name;
    private Long subTypeKey;
    private EquipmentTypeDTO type;
    private EquipmentSubTypeDTO subType;

    public EquipmentDTO() {
    }
    public EquipmentDTO(Long key, String name) {
        this.key = key;
        this.name = name;
    }

    public EquipmentDTO(String name) {
        this.name = name;
    }

    public static EquipmentDTO from(Equipment equipment) {
        return new EquipmentDTO(equipment.getId(), equipment.getName());
    }

    public Long getSubTypeKey() {
        return subTypeKey;
    }

    public void setSubTypeKey(Long subTypeKey) {
        this.subTypeKey = subTypeKey;
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

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
