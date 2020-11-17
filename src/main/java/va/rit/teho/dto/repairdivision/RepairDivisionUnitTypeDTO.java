package va.rit.teho.dto.repairdivision;

import va.rit.teho.entity.repairdivision.RepairDivisionUnitType;

public class RepairDivisionUnitTypeDTO {
    private Long id;
    private String name;
    private Integer workingHoursMin;
    private Integer workingHoursMax;

    public RepairDivisionUnitTypeDTO(Long key) {
        this.id = key;
    }

    public RepairDivisionUnitTypeDTO() {
    }

    public RepairDivisionUnitTypeDTO(Long id, String name, Integer workingHoursMin, Integer workingHoursMax) {
        this.id = id;
        this.name = name;
        this.workingHoursMin = workingHoursMin;
        this.workingHoursMax = workingHoursMax;
    }

    public static RepairDivisionUnitTypeDTO from(RepairDivisionUnitType repairDivisionUnitType) {
        return new RepairDivisionUnitTypeDTO(repairDivisionUnitType.getId(),
                                             repairDivisionUnitType.getName(),
                                             repairDivisionUnitType.getWorkingHoursMin(),
                                             repairDivisionUnitType.getWorkingHoursMax());
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
