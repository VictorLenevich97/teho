package va.rit.teho.dto.equipment;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.equipment.Equipment;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDTO {

    @Positive
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 255)
    private String name;

    private EquipmentTypeDTO type;

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
        equipmentDTO.setType(EquipmentTypeDTO.fromEntity(equipment.getEquipmentType()));
        return equipmentDTO;
    }

    public EquipmentTypeDTO getType() {
        return type;
    }

    public void setType(EquipmentTypeDTO type) {
        this.type = type;
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
