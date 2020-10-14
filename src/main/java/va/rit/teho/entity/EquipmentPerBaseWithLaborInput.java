package va.rit.teho.entity;

public class EquipmentPerBaseWithLaborInput {
    private final EquipmentPerBase equipmentPerBase;
    private final Integer laborInput;

    public EquipmentPerBase getEquipmentPerBase() {
        return equipmentPerBase;
    }

    public Integer getLaborInput() {
        return laborInput;
    }

    public EquipmentPerBaseWithLaborInput(EquipmentPerBase equipmentPerBase, Integer laborInput) {
        this.equipmentPerBase = equipmentPerBase;
        this.laborInput = laborInput;
    }
}
