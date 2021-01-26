package va.rit.teho.dto.common;

import va.rit.teho.entity.common.RepairType;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class RepairTypeDTO {

    @Positive
    private Long id;

    @Size(min = 3, max = 255)
    private String fullName;

    @Size(min = 2, max = 255)
    private String shortName;

    private boolean calculatable;

    public RepairTypeDTO() {
    }

    public RepairTypeDTO(Long id, String fullName, String shortName, boolean calculatable) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.calculatable = calculatable;
    }

    public static RepairTypeDTO from(RepairType repairType) {
        return new RepairTypeDTO(repairType.getId(),
                                 repairType.getFullName(),
                                 repairType.getShortName(),
                                 repairType.isCalculatable());
    }

    public boolean isCalculatable() {
        return calculatable;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }
}
