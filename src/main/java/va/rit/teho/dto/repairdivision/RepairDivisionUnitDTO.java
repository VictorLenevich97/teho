package va.rit.teho.dto.repairdivision;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairDivisionUnitDTO {
    private Long id;
    private String name;
    private RepairDivisionUnitTypeDTO type;
    private IdAndNameDTO stationType;
    private Integer amount;
    private List<RepairDivisionUnitEquipmentStaffDTO> staff;

    public RepairDivisionUnitDTO() {
    }

    public RepairDivisionUnitDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdAndNameDTO getStationType() {
        return stationType;
    }

    public RepairDivisionUnitDTO(Long id,
                                 String name,
                                 RepairDivisionUnitTypeDTO type,
                                 IdAndNameDTO stationType,
                                 Integer amount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stationType = stationType;
        this.amount = amount;
    }

    public static RepairDivisionUnitDTO from(RepairDivisionUnit repairDivisionUnit) {
        return new RepairDivisionUnitDTO(repairDivisionUnit.getId(),
                                         repairDivisionUnit.getName(),
                                         RepairDivisionUnitTypeDTO.from(repairDivisionUnit.getRepairDivisionUnitType()),
                                         new IdAndNameDTO(repairDivisionUnit.getRepairStationType().getId(),
                                                          repairDivisionUnit.getRepairStationType().getName()),
                                         repairDivisionUnit.getStationAmount());
    }

    public List<RepairDivisionUnitEquipmentStaffDTO> getStaff() {
        return staff;
    }

    public RepairDivisionUnitDTO setStaff(List<RepairDivisionUnitEquipmentStaffDTO> staff) {
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

    public RepairDivisionUnitTypeDTO getType() {
        return type;
    }

    public void setType(RepairDivisionUnitTypeDTO type) {
        this.type = type;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
