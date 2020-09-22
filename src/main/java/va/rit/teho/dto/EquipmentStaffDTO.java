package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.RepairStationEquipmentStaff;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentStaffDTO {

    Long equipmentId;
    Integer totalStaff;
    Integer availableStaff;

    public EquipmentStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }
    public EquipmentStaffDTO() {
    }

    public EquipmentStaffDTO(Long equipmentKey, Integer totalStaff, Integer availableStaff) {
        this.equipmentId = equipmentKey;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    public static EquipmentStaffDTO from(RepairStationEquipmentStaff repairStationEquipmentStaff) {
        return new EquipmentStaffDTO(repairStationEquipmentStaff.getEquipmentSubType().getId(),
                                     repairStationEquipmentStaff.getTotalStaff(),
                                     repairStationEquipmentStaff.getAvailableStaff());
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(Integer totalStaff) {
        this.totalStaff = totalStaff;
    }

    public Integer getAvailableStaff() {
        return availableStaff;
    }

    public void setAvailableStaff(Integer availableStaff) {
        this.availableStaff = availableStaff;
    }
}
