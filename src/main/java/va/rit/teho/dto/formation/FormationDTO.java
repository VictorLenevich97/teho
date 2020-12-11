package va.rit.teho.dto.formation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.entity.formation.Formation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormationDTO extends AbstractNamedDTO {

    private final Long id;

    private final FormationDTO parentFormation;

    private final List<EquipmentDTO> equipment;

    public FormationDTO getParentFormation() {
        return parentFormation;
    }

    public FormationDTO(Long id,
                        String shortName,
                        String fullName,
                        FormationDTO parentFormation,
                        List<EquipmentDTO> equipment) {
        super(shortName, fullName);
        this.id = id;
        this.parentFormation = parentFormation;
        this.equipment = equipment;
    }

    public static FormationDTO from(Formation formation, boolean includeEquipment) {
        return new FormationDTO(formation.getId(),
                                formation.getShortName(),
                                formation.getFullName(),
                                formation.getParentFormation() == null ? null :
                                        FormationDTO.from(formation.getParentFormation(), false),
                                includeEquipment ? formation
                                        .getEquipmentPerFormations()
                                        .stream()
                                        .map(epb -> EquipmentDTO.from(epb.getEquipment()))
                                        .collect(Collectors.toList()) : Collections.emptyList());
    }

    public static FormationDTO from(Formation formation) {
        return FormationDTO.from(formation, false);
    }

    public Long getId() {
        return id;
    }

    public List<EquipmentDTO> getEquipment() {
        return equipment;
    }
}
