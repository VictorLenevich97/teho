package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class EquipmentInRepair {

    @EmbeddedId
    EquipmentInRepairEmbeddable equipmentInRepairId;

    @ManyToOne
    @MapsId("base_id")
    @JoinColumn(name = "base_id")
    Base base;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    @ManyToOne
    @MapsId("workhours_distribution_interval_id")
    @JoinColumn(name = "workhours_distribution_interval_id")
    WorkhoursDistributionInterval workhoursDistributionInterval;

    private int count;
    private int avgLaborInput;

    public EquipmentInRepair() {
        //Пустой конструктор для инициализации
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAvgLaborInput() {
        return avgLaborInput;
    }

    public void setAvgLaborInput(int avgLaborInput) {
        this.avgLaborInput = avgLaborInput;
    }

    public EquipmentInRepairEmbeddable getEquipmentInRepairId() {
        return equipmentInRepairId;
    }

    public void setEquipmentInRepairId(EquipmentInRepairEmbeddable equipmentInRepairId) {
        this.equipmentInRepairId = equipmentInRepairId;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public WorkhoursDistributionInterval getWorkhoursDistributionInterval() {
        return workhoursDistributionInterval;
    }

    public void setWorkhoursDistributionInterval(WorkhoursDistributionInterval workhoursDistributionInterval) {
        this.workhoursDistributionInterval = workhoursDistributionInterval;
    }

}
