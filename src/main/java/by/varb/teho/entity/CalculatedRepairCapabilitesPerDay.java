package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class CalculatedRepairCapabilitesPerDay {

    @EmbeddedId
    EquipmentPerRepairStation capabilitiesPerDayAmount;
    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;
    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;
    int capability;

    public CalculatedRepairCapabilitesPerDay() {
    }

    public EquipmentPerRepairStation getCapabilitiesPerDayAmount() {
        return capabilitiesPerDayAmount;
    }

    public void setCapabilitiesPerDayAmount(EquipmentPerRepairStation capabilitiesPerDayAmount) {
        this.capabilitiesPerDayAmount = capabilitiesPerDayAmount;
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

    public int getCapability() {
        return capability;
    }

    public void setCapability(int capability) {
        this.capability = capability;
    }
}