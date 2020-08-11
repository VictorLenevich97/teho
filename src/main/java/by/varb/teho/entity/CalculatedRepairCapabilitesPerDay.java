package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class CalculatedRepairCapabilitesPerDay {

    @EmbeddedId
    EquipmentPerRepairStation equipmentPerRepairStation;
    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;
    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;
    double capability;

    public CalculatedRepairCapabilitesPerDay() {
    }

    public CalculatedRepairCapabilitesPerDay(EquipmentPerRepairStation equipmentPerRepairStation, RepairStation repairStation, Equipment equipment, double capability) {
        this.equipmentPerRepairStation = equipmentPerRepairStation;
        this.repairStation = repairStation;
        this.equipment = equipment;
        this.capability = capability;
    }

    public EquipmentPerRepairStation getEquipmentPerRepairStation() {
        return equipmentPerRepairStation;
    }

    public void setEquipmentPerRepairStation(EquipmentPerRepairStation equipmentPerRepairStation) {
        this.equipmentPerRepairStation = equipmentPerRepairStation;
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

    public double getCapability() {
        return capability;
    }

    public void setCapability(double capability) {
        this.capability = capability;
    }
}