package va.rit.teho.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.entity.RepairStation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairStationDTO {
    private Long key;
    private String name;
    private RepairStationTypeDTO type;
    private Long baseKey;
    private Integer amount;
    private List<EquipmentStaffDTO> equipmentStaff;

    public RepairStationDTO() {
    }

    public RepairStationDTO(Long key, String name) {
        this.key = key;
        this.name = name;
    }

    public RepairStationDTO(Long key, String name, RepairStationTypeDTO type, Long baseKey, Integer amount) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.baseKey = baseKey;
        this.amount = amount;
    }

    public static RepairStationDTO from(RepairStation repairStation) {
        return new RepairStationDTO(repairStation.getId(),
                                    repairStation.getName(),
                                    RepairStationTypeDTO.from(repairStation.getRepairStationType()),
                                    repairStation.getBase().getId(),
                                    repairStation.getStationAmount());
    }

    public List<EquipmentStaffDTO> getEquipmentStaff() {
        return equipmentStaff;
    }

    public RepairStationDTO setEquipmentStaff(List<EquipmentStaffDTO> equipmentStaff) {
        this.equipmentStaff = equipmentStaff;
        return this;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepairStationTypeDTO getType() {
        return type;
    }

    public void setType(RepairStationTypeDTO type) {
        this.type = type;
    }

    public Long getBaseKey() {
        return baseKey;
    }

    public void setBaseKey(Long baseKey) {
        this.baseKey = baseKey;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
