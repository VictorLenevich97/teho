package va.rit.teho.dto;

import va.rit.teho.entity.RepairType;

public class RepairTypeDTO {
    private Long id;
    private String name;

    public RepairTypeDTO() {
    }

    public RepairTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RepairTypeDTO from(RepairType repairType) {
        return new RepairTypeDTO(repairType.getId(), repairType.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
