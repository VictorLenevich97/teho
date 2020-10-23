package va.rit.teho.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RepairStationEquipmentStaff {

    @EmbeddedId
    EquipmentSubTypePerRepairStation equipmentPerRepairStation;

    @ManyToOne
    @MapsId("repair_station_id")
    @JoinColumn(name = "repair_station_id")
    RepairStation repairStation;
    @ManyToOne
    @MapsId("equipment_sub_type_id")
    @JoinColumn(name = "equipment_sub_type_id")
    EquipmentSubType equipmentSubType;
    int totalStaff;
    int availableStaff;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    public RepairStationEquipmentStaff() {
    }

    public RepairStationEquipmentStaff(EquipmentSubTypePerRepairStation equipmentPerRepairStation,
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
                Objects.equals(equipmentSubType, that.equipmentSubType) &&
                Objects.equals(tehoSession, that.tehoSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairStation, repairStation, equipmentSubType, totalStaff, availableStaff, tehoSession);
    }

    public EquipmentSubTypePerRepairStation getEquipmentPerRepairStation() {
        return equipmentPerRepairStation;
    }

    public RepairStation getRepairStation() {
        return repairStation;
    }

    public void setRepairStation(RepairStation repairStation) {
        this.repairStation = repairStation;
    }

    public EquipmentSubType getEquipmentSubType() {
        return equipmentSubType;
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

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    public RepairStationEquipmentStaff copy(UUID newSessionId) {
        return new RepairStationEquipmentStaff(
                getEquipmentPerRepairStation().copy(newSessionId),
                getTotalStaff(),
                getAvailableStaff());
    }
}
