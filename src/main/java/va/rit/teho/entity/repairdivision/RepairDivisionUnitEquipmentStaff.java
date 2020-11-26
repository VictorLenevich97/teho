package va.rit.teho.entity.repairdivision;

import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RepairDivisionUnitEquipmentStaff {

    @EmbeddedId
    RepairDivisionUnitPK equipmentPerRepairDivisionUnit;

    @ManyToOne
    @MapsId("repair_division_unit_id")
    @JoinColumn(name = "repair_division_unit_id")
    RepairDivisionUnit repairDivisionUnit;
    @ManyToOne
    @MapsId("equipment_sub_type_id")
    @JoinColumn(name = "equipment_sub_type_id")
    EquipmentSubType equipmentSubType;
    Integer totalStaff;
    Integer availableStaff;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    TehoSession tehoSession;

    public RepairDivisionUnitEquipmentStaff() {
    }

    public RepairDivisionUnitEquipmentStaff(RepairDivisionUnitPK equipmentPerRepairDivisionUnit,
                                            Integer totalStaff,
                                            Integer availableStaff) {
        this.equipmentPerRepairDivisionUnit = equipmentPerRepairDivisionUnit;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairDivisionUnitEquipmentStaff that = (RepairDivisionUnitEquipmentStaff) o;
        return Objects.equals(equipmentPerRepairDivisionUnit, that.equipmentPerRepairDivisionUnit) &&
                Objects.equals(repairDivisionUnit, that.repairDivisionUnit) &&
                Objects.equals(equipmentSubType, that.equipmentSubType) &&
                Objects.equals(totalStaff, that.totalStaff) &&
                Objects.equals(availableStaff, that.availableStaff) &&
                Objects.equals(tehoSession, that.tehoSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairDivisionUnit,
                            repairDivisionUnit,
                            equipmentSubType,
                            totalStaff,
                            availableStaff,
                            tehoSession);
    }

    public RepairDivisionUnitPK getEquipmentPerRepairDivisionUnit() {
        return equipmentPerRepairDivisionUnit;
    }

    public RepairDivisionUnit getRepairDivisionUnit() {
        return repairDivisionUnit;
    }

    public void setRepairDivisionUnit(RepairDivisionUnit repairDivisionUnit) {
        this.repairDivisionUnit = repairDivisionUnit;
    }

    public EquipmentSubType getEquipmentSubType() {
        return equipmentSubType;
    }

    public Integer getTotalStaff() {
        return totalStaff;
    }

    public Integer getAvailableStaff() {
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

    public RepairDivisionUnitEquipmentStaff copy(UUID newSessionId) {
        return new RepairDivisionUnitEquipmentStaff(
                getEquipmentPerRepairDivisionUnit().copy(newSessionId),
                getTotalStaff(),
                getAvailableStaff());
    }
}
