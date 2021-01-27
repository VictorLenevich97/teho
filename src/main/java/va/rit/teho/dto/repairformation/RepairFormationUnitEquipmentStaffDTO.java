package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitPK;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairFormationUnitEquipmentStaffDTO {

    Long EquipmentTypeId;
    Integer total;
    Integer available;

    public RepairFormationUnitEquipmentStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public RepairFormationUnitEquipmentStaffDTO() {
    }

    public RepairFormationUnitEquipmentStaffDTO(Long EquipmentTypeKey, Integer totalStaff, Integer availableStaff) {
        this.EquipmentTypeId = EquipmentTypeKey;
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public static RepairFormationUnitEquipmentStaffDTO from(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        return new RepairFormationUnitEquipmentStaffDTO(repairFormationUnitEquipmentStaff.getEquipmentType().getId(),
                                                        repairFormationUnitEquipmentStaff.getTotalStaff(),
                                                        repairFormationUnitEquipmentStaff.getAvailableStaff());
    }

    public Long getEquipmentTypeId() {
        return EquipmentTypeId;
    }

    public void setEquipmentTypeId(Long EquipmentTypeId) {
        this.EquipmentTypeId = EquipmentTypeId;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public RepairFormationUnitEquipmentStaff toEntity(UUID sessionId,
                                                      Long EquipmentTypeId,
                                                      Long repairFormationUnitId) {
        return new RepairFormationUnitEquipmentStaff(
                new RepairFormationUnitPK(repairFormationUnitId,
                                          this.EquipmentTypeId == null ? EquipmentTypeId : this.EquipmentTypeId,
                                          sessionId),
                total,
                available);
    }
}
