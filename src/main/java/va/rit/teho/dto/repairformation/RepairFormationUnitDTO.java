package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.entity.repairformation.RepairFormationUnit;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairFormationUnitDTO {
    private Long id;
    private String name;
    private RepairFormationTypeDTO type;
    private IdAndNameDTO stationType;
    private Integer amount;
    private List<RepairFormationUnitEquipmentStaffDTO> staff;

    public RepairFormationUnitDTO() {
    }

    public RepairFormationUnitDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdAndNameDTO getStationType() {
        return stationType;
    }

    public RepairFormationUnitDTO(Long id,
                                  String name,
                                  RepairFormationTypeDTO type,
                                  IdAndNameDTO stationType,
                                  Integer amount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stationType = stationType;
        this.amount = amount;
    }

    public static RepairFormationUnitDTO from(RepairFormationUnit repairFormationUnit) {
        return new RepairFormationUnitDTO(repairFormationUnit.getId(),
                                          repairFormationUnit.getName(),
                                          RepairFormationTypeDTO.from(repairFormationUnit.getRepairFormation().getRepairFormationType()),
                                          new IdAndNameDTO(repairFormationUnit.getRepairStationType().getId(),
                                                          repairFormationUnit.getRepairStationType().getName()),
                                          repairFormationUnit.getStationAmount());
    }

    public List<RepairFormationUnitEquipmentStaffDTO> getStaff() {
        return staff;
    }

    public RepairFormationUnitDTO setStaff(List<RepairFormationUnitEquipmentStaffDTO> staff) {
        this.staff = staff;
        return this;
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

    public RepairFormationTypeDTO getType() {
        return type;
    }

    public void setType(RepairFormationTypeDTO type) {
        this.type = type;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
