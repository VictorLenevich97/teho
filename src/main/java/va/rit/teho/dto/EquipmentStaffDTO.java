package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.RepairStationEquipmentStaff;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentStaffDTO {

    Long equipmentSubTypeId;
    Integer totalStaff;
    Integer availableStaff;

    public EquipmentStaffDTO(Integer totalStaff, Integer availableStaff) {
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    public EquipmentStaffDTO() {
    }

    public EquipmentStaffDTO(Long equipmentSubTypeKey, Integer totalStaff, Integer availableStaff) {
        this.equipmentSubTypeId = equipmentSubTypeKey;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    public static EquipmentStaffDTO from(RepairStationEquipmentStaff repairStationEquipmentStaff) {
        return new EquipmentStaffDTO(repairStationEquipmentStaff.getEquipmentSubType().getId(),
                                     repairStationEquipmentStaff.getTotalStaff(),
                                     repairStationEquipmentStaff.getAvailableStaff());
    }

    public Long getEquipmentSubTypeId() {
        return equipmentSubTypeId;
    }

    public void setEquipmentSubTypeId(Long equipmentSubTypeId) {
        this.equipmentSubTypeId = equipmentSubTypeId;
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
