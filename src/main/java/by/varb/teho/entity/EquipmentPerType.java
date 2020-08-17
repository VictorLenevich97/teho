package by.varb.teho.entity;

import java.util.List;

public class EquipmentPerType {
    private final EquipmentType equipmentType;
    private final Equipment equipment;

    public EquipmentPerType(EquipmentType equipmentType, Equipment equipment) {
        this.equipmentType = equipmentType;
        this.equipment = equipment;
    }
}
