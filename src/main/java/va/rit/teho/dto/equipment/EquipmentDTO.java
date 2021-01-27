package va.rit.teho.dto.equipment;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.equipment.Equipment;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDTO {

    @Positive
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 255)
    private String name;

    private EquipmentTypeDTO type;

    private EquipmentTypeDTO subType;

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
        equipmentDTO.setSubType(EquipmentTypeDTO.fromEntity(equipment.getEquipmentType()));
        Optional.ofNullable(equipment.getEquipmentType())
                .ifPresent(et -> equipmentDTO.setType(EquipmentTypeDTO.fromEntity(et)));
        return equipmentDTO;
    }

    public EquipmentTypeDTO getType() {
        return type;
    }

    public void setType(EquipmentTypeDTO type) {
        this.type = type;
    }

    public EquipmentTypeDTO getSubType() {
        return subType;
    }

    public void setSubType(EquipmentTypeDTO subType) {
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
