package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.entity.repairformation.RepairFormationUnit;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairFormationUnitDTO {

    @Positive
    private Long id;

    @Size(min = 3, max = 255)
    private String name;

    private RepairFormationDTO repairFormation;

    private IdAndNameDTO stationType;

    @PositiveOrZero
    private Integer amount;

    private List<RepairFormationUnitEquipmentStaffDTO> staff;

    public RepairFormationUnitDTO() {
    }

    public RepairFormationUnitDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RepairFormationUnitDTO(Long id,
                                  String name,
                                  RepairFormationDTO repairFormation,
                                  IdAndNameDTO stationType,
                                  Integer amount) {
        this.id = id;
        this.name = name;
        this.repairFormation = repairFormation;
        this.stationType = stationType;
        this.amount = amount;
    }

    public static RepairFormationUnitDTO from(RepairFormationUnit repairFormationUnit) {
        return new RepairFormationUnitDTO(repairFormationUnit.getId(),
                                          repairFormationUnit.getName(),
                                          RepairFormationDTO.from(repairFormationUnit.getRepairFormation(), false),
                                          new IdAndNameDTO(repairFormationUnit.getRepairStationType().getId(),
                                                           repairFormationUnit.getRepairStationType().getName()),
                                          repairFormationUnit.getStationAmount());
    }

    public IdAndNameDTO getStationType() {
        return stationType;
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

    public RepairFormationDTO getRepairFormation() {
        return repairFormation;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
