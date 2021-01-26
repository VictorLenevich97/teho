package va.rit.teho.entity.equipment;

import va.rit.teho.entity.common.RepairType;

import javax.persistence.*;

@Entity
public class EquipmentLaborInputPerType {

    @EmbeddedId
    private EquipmentLaborInputPerTypePK equipmentLaborInputPerTypeAmount;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    @Column(nullable = false)
    private int amount;

    public EquipmentLaborInputPerType() {
        //Пустой конструктор для автоматической инициализации
    }

    public EquipmentLaborInputPerType(Long equipmentId, Long repairTypeId, int amount) {
        this.equipmentLaborInputPerTypeAmount = new EquipmentLaborInputPerTypePK(equipmentId, repairTypeId);
        this.amount = amount;
    }

    public EquipmentLaborInputPerType(RepairType repairType, int amount) {
        this.repairType = repairType;
        this.amount = amount;
    }

    public EquipmentLaborInputPerTypePK getEquipmentLaborInputPerTypeAmount() {
        return equipmentLaborInputPerTypeAmount;
    }

    public void setEquipmentLaborInputPerTypeAmount(EquipmentLaborInputPerTypePK equipmentLaborInputPerTypeAmount) {
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

    public EquipmentLaborInputPerType setAmount(int amount) {
        this.amount = amount;
        return this;
    }
}
