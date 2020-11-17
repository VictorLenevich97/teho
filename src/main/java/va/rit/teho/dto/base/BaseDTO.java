package va.rit.teho.dto.base;

import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.entity.base.Base;

import java.util.List;
import java.util.stream.Collectors;

public class BaseDTO extends AbstractNamedDTO {
    private final Long id;
    private final List<EquipmentDTO> equipment;

    public BaseDTO(Long id, String shortName, String fullName, List<EquipmentDTO> equipment) {
        super(shortName, fullName);
        this.id = id;
        this.equipment = equipment;
    }

    public static BaseDTO from(Base base) {
        return new BaseDTO(base.getId(),
                           base.getShortName(),
                           base.getFullName(),
                           base
                                   .getEquipmentPerBases()
                                   .stream()
                                   .map(epb -> EquipmentDTO.from(epb.getEquipment()))
                                   .collect(Collectors.toList()));
    }

    public Long getId() {
        return id;
    }

    public List<EquipmentDTO> getEquipment() {
        return equipment;
    }
}
