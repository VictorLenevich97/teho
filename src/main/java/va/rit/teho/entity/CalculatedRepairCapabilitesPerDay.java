package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class CalculatedRepairCapabilitesPerDay {

    @EmbeddedId
    EquipmentPerRepairStationWithRepairType equipmentPerRepairStationWithRepairType;

    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    double capability;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    public CalculatedRepairCapabilitesPerDay() {
    }

    public CalculatedRepairCapabilitesPerDay(EquipmentPerRepairStationWithRepairType equipmentPerRepairStationWithRepairType,
                                             RepairStation repairStation,
                                             Equipment equipment,
                                             double capability,
                                             RepairType repairType) {
        this.equipmentPerRepairStationWithRepairType = equipmentPerRepairStationWithRepairType;
        this.repairStation = repairStation;
        this.equipment = equipment;
        this.repairType = repairType;
        this.capability = capability;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculatedRepairCapabilitesPerDay that = (CalculatedRepairCapabilitesPerDay) o;
        return Double.compare(that.capability, capability) == 0 &&
                Objects.equals(equipmentPerRepairStationWithRepairType, that.equipmentPerRepairStationWithRepairType) &&
                Objects.equals(repairStation, that.repairStation) &&
                Objects.equals(equipment, that.equipment) &&
                Objects.equals(repairType, that.repairType) &&
                Objects.equals(tehoSession, that.tehoSession);
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairStationWithRepairType,
                            repairStation,
                            equipment,
                            repairType,
                            capability,
                            tehoSession);
    }

    public EquipmentPerRepairStationWithRepairType getEquipmentPerRepairStation() {
        return equipmentPerRepairStationWithRepairType;
    }

    public void setEquipmentPerRepairStation(EquipmentPerRepairStationWithRepairType equipmentPerRepairStation) {
        this.equipmentPerRepairStationWithRepairType = equipmentPerRepairStation;
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