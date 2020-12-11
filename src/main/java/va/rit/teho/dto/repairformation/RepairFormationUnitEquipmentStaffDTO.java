package va.rit.teho.dto.repairformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairFormationUnitPK;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairFormationUnitEquipmentStaffDTO {

    Long equipmentSubTypeId;
    Integer total;
    Integer available;

    public RepairFormationUnitEquipmentStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public RepairFormationUnitEquipmentStaffDTO() {
    }

    public RepairFormationUnitEquipmentStaffDTO(Long equipmentSubTypeKey, Integer totalStaff, Integer availableStaff) {
        this.equipmentSubTypeId = equipmentSubTypeKey;
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public static RepairFormationUnitEquipmentStaffDTO from(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        return new RepairFormationUnitEquipmentStaffDTO(repairFormationUnitEquipmentStaff.getEquipmentSubType().getId(),
                                                        repairFormationUnitEquipmentStaff.getTotalStaff(),
                                                        repairFormationUnitEquipmentStaff.getAvailableStaff());
    }

    public Long getEquipmentSubTypeId() {
        return equipmentSubTypeId;
    }

    public void setEquipmentSubTypeId(Long equipmentSubTypeId) {
        this.equipmentSubTypeId = equipmentSubTypeId;
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
                                                      Long equipmentSubTypeId,
                                                      Long repairFormationUnitId) {
        return new RepairFormationUnitEquipmentStaff(
                new RepairFormationUnitPK(repairFormationUnitId,
                                          this.equipmentSubTypeId == null ? equipmentSubTypeId : this.equipmentSubTypeId,
                                          sessionId),
                total,
                available);
    }
}
