package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.Equipment;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDTO {
    private Long key;
    private String name;
    private Long typeId;

    public EquipmentDTO() {
    }

    public EquipmentDTO(Long key, String name) {
        this.key = key;
        this.name = name;
    }

    public Long getKey() {
        return key;
    }

    public EquipmentDTO(String name, Long typeId) {
        this.name = name;
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public static EquipmentDTO from(Equipment equipment) {
        return new EquipmentDTO(equipment.getId(), equipment.getName());
    }
}
