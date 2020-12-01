package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.formation.FormationDTO;
import va.rit.teho.entity.repairformation.RepairFormation;

public class RepairFormationDTO {
    private Long id;
    private String name;
    private RepairFormationTypeDTO type;
    private FormationDTO formation;

    public RepairFormationDTO() {
    }

    public RepairFormationDTO(Long id,
                              String name,
                              RepairFormationTypeDTO type,
                              FormationDTO formation) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.formation = formation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RepairFormationTypeDTO getType() {
        return type;
    }

    public FormationDTO getFormation() {
        return formation;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static RepairFormationDTO from(RepairFormation repairFormation, boolean includeFormation) {
        return new RepairFormationDTO(repairFormation.getId(),
                                      repairFormation.getName(),
                                      RepairFormationTypeDTO.from(repairFormation.getRepairFormationType()),
                                      includeFormation ? FormationDTO.from(repairFormation.getFormation()) : null);
    }
}
