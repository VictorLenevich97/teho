package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.RepairStationEquipmentStaff;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentStaffDTO {

    Long equipmentKey;

    public Long getEquipmentKey() {
        return equipmentKey;
    }

    public void setEquipmentKey(Long equipmentKey) {
        this.equipmentKey = equipmentKey;
    }

    Integer totalStaff;
    Integer availableStaff;

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

    public EquipmentStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    public EquipmentStaffDTO(Long equipmentKey, Integer totalStaff, Integer availableStaff) {
        this.equipmentKey = equipmentKey;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    public static EquipmentStaffDTO from(RepairStationEquipmentStaff repairStationEquipmentStaff) {
        return new EquipmentStaffDTO(repairStationEquipmentStaff.getEquipment().getId(),
                                     repairStationEquipmentStaff.getTotalStaff(),
                                     repairStationEquipmentStaff.getAvailableStaff());
    }
}
