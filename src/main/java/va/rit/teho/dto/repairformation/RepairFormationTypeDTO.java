package va.rit.teho.dto.repairformation;

import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.entity.repairformation.RepairFormationType;

public class RepairFormationTypeDTO {
    private Long id;
    private String name;
    private IdAndNameDTO restorationType;
    private Integer workingHoursMin;
    private Integer workingHoursMax;

    public RepairFormationTypeDTO(Long key) {
        this.id = key;
    }

    public RepairFormationTypeDTO() {
    }

    public RepairFormationTypeDTO(Long id,
                                  String name,
                                  IdAndNameDTO restorationType,
                                  Integer workingHoursMin,
                                  Integer workingHoursMax) {
        this.id = id;
        this.name = name;
        this.restorationType = restorationType;
        this.workingHoursMin = workingHoursMin;
        this.workingHoursMax = workingHoursMax;
    }

    public static RepairFormationTypeDTO from(RepairFormationType repairFormationType) {
        return new RepairFormationTypeDTO(repairFormationType.getId(),
                                          repairFormationType.getName(),
                                          new IdAndNameDTO(repairFormationType.getRestorationType().getId(),
                                                           repairFormationType.getRestorationType().getName()),
                                          repairFormationType.getWorkingHoursMin(),
                                          repairFormationType.getWorkingHoursMax());
    }

    public IdAndNameDTO getRestorationType() {
        return restorationType;
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
