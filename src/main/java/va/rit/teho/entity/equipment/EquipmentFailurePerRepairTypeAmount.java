package va.rit.teho.entity.equipment;

import va.rit.teho.entity.common.RepairType;

public class EquipmentFailurePerRepairTypeAmount {
    private final Equipment equipment;
    private final RepairType repairType;
    private final Double amount;

    public EquipmentFailurePerRepairTypeAmount(Equipment equipment, RepairType repairType, Double amount) {
        this.equipment = equipment;
        this.repairType = repairType;
        this.amount = amount;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public Double getAmount() {
        return amount;
    }
}
