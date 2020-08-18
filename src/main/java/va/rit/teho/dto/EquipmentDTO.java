package va.rit.teho.dto;

public class EquipmentDTO {
    private String name;
    private Long typeId;

    public EquipmentDTO() {
    }

    public EquipmentDTO(String name, Long typeId) {
        this.name = name;
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}
