package va.rit.teho.dto.equipment;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.entity.equipment.EquipmentType;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentTypeDTO extends AbstractNamedDTO {

    @Positive
    private Long id;

    @Positive
    private Long parentTypeId;

    private List<EquipmentTypeDTO> subTypes;

    public EquipmentTypeDTO() {
    }

    public EquipmentTypeDTO(String shortName,
                            String fullName,
                            List<EquipmentTypeDTO> subTypes) {
        super(shortName, fullName);
        this.subTypes = subTypes;
    }

    public EquipmentTypeDTO(Long id,
                            String shortName,
                            String fullName) {
        super(shortName, fullName);
        this.id = id;
    }

    public EquipmentTypeDTO(Long id,
                            Long parentTypeId,
                            String shortName,
                            String fullName) {
        this(id, shortName, fullName);
        this.parentTypeId = parentTypeId;
    }

    public EquipmentTypeDTO(Long id,
                            String shortName,
                            String fullName,
                            List<EquipmentTypeDTO> subTypes) {
        this(shortName, fullName, subTypes);
        this.id = id;
    }

    public static EquipmentTypeDTO fromEntity(EquipmentType equipmentType) {
        return new EquipmentTypeDTO(equipmentType.getId(),
                                    equipmentType.getShortName(),
                                    equipmentType.getFullName());
    }

    public static EquipmentTypeDTO fromEntityIncludeSubtypes(EquipmentType equipmentType) {
        return new EquipmentTypeDTO(equipmentType.getId(),
                                    equipmentType.getShortName(),
                                    equipmentType.getFullName(),
                                    equipmentType
                                            .getEquipmentTypes()
                                            .stream()
                                            .map(EquipmentTypeDTO::fromEntityIncludeSubtypes)
                                            .collect(Collectors.toList()));
    }

    public Long getParentTypeId() {
        return parentTypeId;
    }

    public List<EquipmentTypeDTO> getSubTypes() {
        return subTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
