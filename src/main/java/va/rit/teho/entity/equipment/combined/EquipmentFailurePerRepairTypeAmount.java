package va.rit.teho.entity.equipment.combined;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentPerFormation;
import va.rit.teho.entity.formation.Formation;

public class EquipmentFailurePerRepairTypeAmount {

    private final EquipmentPerFormation equipmentPerFormation;
    private final RepairType repairType;
    private final Integer laborInput;
    private final Double amount;

    public EquipmentFailurePerRepairTypeAmount(EquipmentPerFormation equipmentPerFormation,
                                               RepairType repairType,
                                               Double amount) {
        this.equipmentPerFormation = equipmentPerFormation;
        this.repairType = repairType;
        this.laborInput = 0;
        this.amount = amount;
    }

    public EquipmentFailurePerRepairTypeAmount(EquipmentPerFormation equipmentPerFormation,
                                               RepairType repairType,
                                               Integer laborInput,
                                               Double amount) {
        this.equipmentPerFormation = equipmentPerFormation;
        this.repairType = repairType;
        this.laborInput = laborInput;
        this.amount = amount;
    }

    public EquipmentPerFormation getEquipmentPerFormation() {
        return equipmentPerFormation;
    }

    public Integer getLaborInput() {
        return laborInput;
    }

    public Formation getFormation() {
        return equipmentPerFormation.getFormation();
    }

    public Equipment getEquipment() {
        return equipmentPerFormation.getEquipment();
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public Double getAmount() {
        return amount;
    }
}
