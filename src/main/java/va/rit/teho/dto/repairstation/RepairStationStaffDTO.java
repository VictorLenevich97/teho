package va.rit.teho.dto.repairstation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaff;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaffPK;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairStationStaffDTO {

    Long equipmentSubTypeId;
    Integer total;
    Integer available;

    public RepairStationStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public RepairStationStaffDTO() {
    }

    public RepairStationStaffDTO(Long equipmentSubTypeKey, Integer totalStaff, Integer availableStaff) {
        this.equipmentSubTypeId = equipmentSubTypeKey;
        this.total = totalStaff;
        this.available = availableStaff;
    }

    public static RepairStationStaffDTO from(RepairStationEquipmentStaff repairStationEquipmentStaff) {
        return new RepairStationStaffDTO(repairStationEquipmentStaff.getEquipmentSubType().getId(),
                                         repairStationEquipmentStaff.getTotalStaff(),
                                         repairStationEquipmentStaff.getAvailableStaff());
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

    public RepairStationEquipmentStaff toEntity(UUID sessionId,
                                                Long equipmentSubTypeId,
                                                Long repairStationId) {
        return new RepairStationEquipmentStaff(
                new RepairStationEquipmentStaffPK(repairStationId,
                                                  this.equipmentSubTypeId == null ? equipmentSubTypeId : this.equipmentSubTypeId,
                                                  sessionId),
                total,
                available);
    }
}
