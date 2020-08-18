package va.rit.teho.entity;

import javax.persistence.*;

@Entity
public class EquipmentLaborInputPerType {
    @EmbeddedId
    EquipmentLaborInputPerTypeAmount equipmentLaborInputPerTypeAmount;
    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;
    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    RepairType repairType;
    int amount;

    public EquipmentLaborInputPerType() {
        //Пустой конструктор для автоматической инициализации
    }

    public EquipmentLaborInputPerTypeAmount getEquipmentLaborInputPerTypeAmount() {
        return equipmentLaborInputPerTypeAmount;
    }

    public void setEquipmentLaborInputPerTypeAmount(EquipmentLaborInputPerTypeAmount equipmentLaborInputPerTypeAmount) {
        this.equipmentLaborInputPerTypeAmount = equipmentLaborInputPerTypeAmount;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
