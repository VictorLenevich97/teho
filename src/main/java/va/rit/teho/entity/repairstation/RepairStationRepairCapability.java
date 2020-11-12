package va.rit.teho.entity.repairstation;

import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RepairStationRepairCapability {

    @EmbeddedId
    RepairStationRepairCapabilityPK equipmentPerRepairStationWithRepairType;

    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;

    @ManyToOne
    @MapsId("equipment_id")
    @JoinColumn(name = "equipment_id")
    Equipment equipment;

    @ManyToOne
    @MapsId("repair_type_id")
    @JoinColumn(name = "repair_type_id")
    RepairType repairType;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    double capability;

    public RepairStationRepairCapability() {
    }

    public RepairStationRepairCapability(RepairStationRepairCapabilityPK equipmentPerRepairStationWithRepairType,
                                         double capability) {
        this.equipmentPerRepairStationWithRepairType = equipmentPerRepairStationWithRepairType;
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
        RepairStationRepairCapability that = (RepairStationRepairCapability) o;
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

    public RepairStationRepairCapabilityPK getEquipmentPerRepairStation() {
        return equipmentPerRepairStationWithRepairType;
    }

    public void setEquipmentPerRepairStation(RepairStationRepairCapabilityPK equipmentPerRepairStation) {
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

    public RepairStationRepairCapability copy(UUID newSessionId) {
        return new RepairStationRepairCapability(getEquipmentPerRepairStation().copy(newSessionId),
                                                 getCapability());
    }
}