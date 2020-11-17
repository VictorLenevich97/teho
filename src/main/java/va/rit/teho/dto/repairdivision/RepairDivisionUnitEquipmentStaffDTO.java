package va.rit.teho.dto.repairdivision;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitEquipmentStaff;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitPK;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairDivisionUnitEquipmentStaffDTO {

    Long equipmentSubTypeId;
    Integer total;
    Integer available;

    public RepairDivisionUnitEquipmentStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public RepairDivisionUnitEquipmentStaffDTO() {
    }

    public RepairDivisionUnitEquipmentStaffDTO(Long equipmentSubTypeKey, Integer totalStaff, Integer availableStaff) {
        this.equipmentSubTypeId = equipmentSubTypeKey;
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public static RepairDivisionUnitEquipmentStaffDTO from(RepairDivisionUnitEquipmentStaff repairDivisionUnitEquipmentStaff) {
        return new RepairDivisionUnitEquipmentStaffDTO(repairDivisionUnitEquipmentStaff.getEquipmentSubType().getId(),
                                                       repairDivisionUnitEquipmentStaff.getTotalStaff(),
                                                       repairDivisionUnitEquipmentStaff.getAvailableStaff());
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

    public RepairDivisionUnitEquipmentStaff toEntity(UUID sessionId,
                                                     Long equipmentSubTypeId,
                                                     Long repairDivisionUnitId) {
        return new RepairDivisionUnitEquipmentStaff(
                new RepairDivisionUnitPK(repairDivisionUnitId,
                                         this.equipmentSubTypeId == null ? equipmentSubTypeId : this.equipmentSubTypeId,
                                         sessionId),
                total,
                available);
    }
}
