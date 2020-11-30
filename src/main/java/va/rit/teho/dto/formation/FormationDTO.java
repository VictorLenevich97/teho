package va.rit.teho.dto.formation;

import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.entity.formation.Formation;

import java.util.List;
import java.util.stream.Collectors;

public class FormationDTO extends AbstractNamedDTO {
    private final Long id;
    private final List<EquipmentDTO> equipment;

    public FormationDTO(Long id, String shortName, String fullName, List<EquipmentDTO> equipment) {
        super(shortName, fullName);
        this.id = id;
        this.equipment = equipment;
    }

    public static FormationDTO from(Formation formation) {
        return new FormationDTO(formation.getId(),
                                formation.getShortName(),
                                formation.getFullName(),
                                formation
                                   .getEquipmentPerFormations()
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
