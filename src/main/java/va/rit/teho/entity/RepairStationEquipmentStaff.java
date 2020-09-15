package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Objects;

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

    public RepairStationEquipmentStaff(EquipmentPerRepairStation equipmentPerRepairStation,
                                       int totalStaff,
                                       int availableStaff) {
        this.equipmentPerRepairStation = equipmentPerRepairStation;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairStationEquipmentStaff that = (RepairStationEquipmentStaff) o;
        return totalStaff == that.totalStaff &&
                availableStaff == that.availableStaff &&
                Objects.equals(equipmentPerRepairStation, that.equipmentPerRepairStation) &&
                Objects.equals(repairStation, that.repairStation) &&
                Objects.equals(equipment, that.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairStation, repairStation, equipment, totalStaff, availableStaff);
    }

    public EquipmentPerRepairStation getEquipmentPerRepairStation() {
        return equipmentPerRepairStation;
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

    public int getAvailableStaff() {
        return availableStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    public void setAvailableStaff(int availableStaff) {
        this.availableStaff = availableStaff;
    }
}
