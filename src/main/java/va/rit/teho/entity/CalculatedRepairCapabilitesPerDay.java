package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Objects;

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
    public CalculatedRepairCapabilitesPerDay(EquipmentPerRepairStation equipmentPerRepairStation,
                                             RepairStation repairStation,
                                             Equipment equipment,
                                             double capability) {
        this.equipmentPerRepairStation = equipmentPerRepairStation;
        this.repairStation = repairStation;
        this.equipment = equipment;
        this.capability = capability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculatedRepairCapabilitesPerDay that = (CalculatedRepairCapabilitesPerDay) o;
        return Double.compare(that.capability, capability) == 0 &&
                Objects.equals(equipmentPerRepairStation, that.equipmentPerRepairStation) &&
                Objects.equals(repairStation, that.repairStation) &&
                Objects.equals(equipment, that.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairStation, repairStation, equipment, capability);
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