package va.rit.teho.dto.repairstation;

import com.fasterxml.jackson.annotation.JsonInclude;
import va.rit.teho.dto.base.BaseDTO;
import va.rit.teho.entity.repairstation.RepairStation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairStationDTO {
    private Long id;
    private String name;
    private RepairStationTypeDTO type;
    private BaseDTO base;
    private Integer amount;
    private List<RepairStationStaffDTO> staff;

    public RepairStationDTO() {
    }

    public RepairStationDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RepairStationDTO(Long id, String name, RepairStationTypeDTO type, BaseDTO base, Integer amount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.base = base;
        this.amount = amount;
    }

    public static RepairStationDTO from(RepairStation repairStation) {
        return new RepairStationDTO(repairStation.getId(),
                                    repairStation.getName(),
                                    RepairStationTypeDTO.from(repairStation.getRepairStationType()),
                                    BaseDTO.from(repairStation.getBase()),
                                    repairStation.getStationAmount());
    }

    public BaseDTO getBase() {
        return base;
    }

    public void setBase(BaseDTO base) {
        this.base = base;
    }

    public List<RepairStationStaffDTO> getStaff() {
        return staff;
    }

    public RepairStationDTO setStaff(List<RepairStationStaffDTO> staff) {
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

    public RepairStationTypeDTO getType() {
        return type;
    }

    public void setType(RepairStationTypeDTO type) {
        this.type = type;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
