package va.rit.teho.dto.formation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import va.rit.teho.dto.common.AbstractNamedDTO;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.dto.repairformation.RepairFormationDTO;
import va.rit.teho.entity.formation.Formation;
import va.rit.teho.entity.repairformation.RepairFormation;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormationDTO extends AbstractNamedDTO {

    private final Long id;

    @ApiModelProperty(value = "parentFormation", example = "{\"id\": 1}")
    private final FormationDTO parentFormation;

    @ApiModelProperty(hidden = true)
    private final List<EquipmentDTO> equipment;

    @ApiModelProperty(hidden = true)
    private List<RepairFormationDTO> repairFormations;

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
                                        .collect(Collectors.toList()) : null);
    }

    public static FormationDTO from(Formation formation) {
        return FormationDTO.from(formation, false);
    }

    public static FormationDTO from(Formation formation, List<RepairFormation> repairFormations) {
        FormationDTO result = from(formation, false);
        result.setRepairFormations(
                repairFormations
                        .stream()
                        .map(rf -> RepairFormationDTO.from(rf, false))
                        .collect(Collectors.toList()));
        return result;
    }

    public FormationDTO getParentFormation() {
        return parentFormation;
    }

    public List<RepairFormationDTO> getRepairFormations() {
        return repairFormations;
    }

    public void setRepairFormations(List<RepairFormationDTO> repairFormations) {
        this.repairFormations = repairFormations;
    }

    public Long getId() {
        return id;
    }

    public List<EquipmentDTO> getEquipment() {
        return equipment;
    }
}
