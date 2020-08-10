package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class EquipmentInRepair {

    @EmbeddedId
    EquipmentInRepairEmbeddable equipmentInRepairId;

    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    @ManyToOne
    @MapsId("workhours_distribution_interval_id")
    @JoinColumn(name = "workhours_distribution_interval_id")
    WorkhoursDistributionInterval workhoursDistributionInterval;

    private Double qij;

    public EquipmentInRepair() {
        //Пустой конструктор для инициализации
    }

    public EquipmentInRepairEmbeddable getEquipmentInRepairId() {
        return equipmentInRepairId;
    }

    public void setEquipmentInRepairId(EquipmentInRepairEmbeddable equipmentInRepairId) {
        this.equipmentInRepairId = equipmentInRepairId;
    }

    public RepairStation getRepairStation() {
        return repairStation;
    }

    public void setRepairStation(RepairStation repairStation) {
        this.repairStation = repairStation;
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

    public Double getQij() {
        return qij;
    }

    public void setQij(Double qij) {
        this.qij = qij;
    }
}
