package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class EquipmentInRepair {

    @EmbeddedId
    EquipmentInRepairId equipmentInRepairId;

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

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    private double count;
    private double avgLaborInput;

    public EquipmentInRepair() {
        //Пустой конструктор для инициализации
    }

    public EquipmentInRepair(EquipmentInRepairId equipmentInRepairId,
                             double count,
                             double avgLaborInput) {
        this.equipmentInRepairId = equipmentInRepairId;
        this.count = count;
        this.avgLaborInput = avgLaborInput;
    }

    public double getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAvgLaborInput() {
        return avgLaborInput;
    }

    public void setAvgLaborInput(int avgLaborInput) {
        this.avgLaborInput = avgLaborInput;
    }

    public EquipmentInRepairId getEquipmentInRepairId() {
        return equipmentInRepairId;
    }

    public void setEquipmentInRepairId(EquipmentInRepairId equipmentInRepairId) {
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

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentInRepair that = (EquipmentInRepair) o;
        return Double.compare(that.count, count) == 0 &&
                Double.compare(that.avgLaborInput, avgLaborInput) == 0 &&
                Objects.equals(equipmentInRepairId, that.equipmentInRepairId) &&
                Objects.equals(base, that.base) &&
                Objects.equals(equipment, that.equipment) &&
                Objects.equals(workhoursDistributionInterval, that.workhoursDistributionInterval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentInRepairId, base, equipment, workhoursDistributionInterval, count, avgLaborInput);
    }
}
