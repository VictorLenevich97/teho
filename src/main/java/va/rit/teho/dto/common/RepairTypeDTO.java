package va.rit.teho.dto.common;

import va.rit.teho.entity.common.RepairType;

public class RepairTypeDTO {
    private Long id;
    private String fullName;
    private String shortName;

    public RepairTypeDTO() {
    }

    public RepairTypeDTO(Long id, String fullName, String shortName) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public static RepairTypeDTO from(RepairType repairType) {
        return new RepairTypeDTO(repairType.getId(), repairType.getFullName(), repairType.getShortName());
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
