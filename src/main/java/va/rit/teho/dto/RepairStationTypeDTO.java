package va.rit.teho.dto;

import va.rit.teho.entity.RepairStationType;

public class RepairStationTypeDTO {
    private Long key;
    private String name;
    private Integer workingHoursMin;
    private Integer workingHoursMax;

    public RepairStationTypeDTO(Long key) {
        this.key = key;
    }

    public RepairStationTypeDTO() {
    }

    public RepairStationTypeDTO(Long key, String name, Integer workingHoursMin, Integer workingHoursMax) {
        this.key = key;
        this.name = name;
        this.workingHoursMin = workingHoursMin;
        this.workingHoursMax = workingHoursMax;
    }

    public static RepairStationTypeDTO from(RepairStationType repairStationType) {
        return new RepairStationTypeDTO(repairStationType.getId(),
                                        repairStationType.getName(),
                                        repairStationType.getWorkingHoursMin(),
                                        repairStationType.getWorkingHoursMax());
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

    public Integer getWorkingHoursMin() {
        return workingHoursMin;
    }

    public void setWorkingHoursMin(Integer workingHoursMin) {
        this.workingHoursMin = workingHoursMin;
    }

    public Integer getWorkingHoursMax() {
        return workingHoursMax;
    }

    public void setWorkingHoursMax(Integer workingHoursMax) {
        this.workingHoursMax = workingHoursMax;
    }
}
