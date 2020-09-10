package va.rit.teho.dto;

import va.rit.teho.entity.RepairType;

public class RepairTypeDTO {
    private Long key;
    private String name;

    public RepairTypeDTO() {
    }

    public RepairTypeDTO(Long key, String name) {
        this.key = key;
        this.name = name;
    }

    public static RepairTypeDTO from(RepairType repairType) {
        return new RepairTypeDTO(repairType.getId(), repairType.getName());
    }

    public Long getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
