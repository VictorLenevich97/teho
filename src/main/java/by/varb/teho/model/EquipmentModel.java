package by.varb.teho.model;

public class EquipmentModel {
    private String name;
    private Long typeId;

    public EquipmentModel() {
    }

    public EquipmentModel(String name, Long typeId) {
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
