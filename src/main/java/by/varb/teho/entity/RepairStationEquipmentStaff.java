package by.varb.teho.entity;

import javax.persistence.*;

@Entity
public class RepairStationEquipmentStaff {

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
    int totalStaff;
    int availableStaff;

    public RepairStationEquipmentStaff() {
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

    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    public int getAvailableStaff() {
        return availableStaff;
    }

    public void setAvailableStaff(int availableStaff) {
        this.availableStaff = availableStaff;
    }
}
