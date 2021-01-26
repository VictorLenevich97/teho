package va.rit.teho.entity.repairformation;

import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.session.TehoSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RepairFormationUnitEquipmentStaff {

    public static final RepairFormationUnitEquipmentStaff EMPTY =
            new RepairFormationUnitEquipmentStaff(null, 0, 0);

    @EmbeddedId
    private RepairFormationUnitPK equipmentPerRepairFormationUnit;

    @ManyToOne
    @MapsId("repair_formation_unit_id")
    @JoinColumn(name = "repair_formation_unit_id")
    private RepairFormationUnit repairFormationUnit;

    @ManyToOne
    @MapsId("equipment_sub_type_id")
    @JoinColumn(name = "equipment_sub_type_id")
    private EquipmentSubType equipmentSubType;

    private Integer totalStaff;
    private Integer availableStaff;

    @ManyToOne
    @MapsId("session_id")
    @JoinColumn(name = "session_id")
    private TehoSession tehoSession;

    public RepairFormationUnitEquipmentStaff() {
    }

    public RepairFormationUnitEquipmentStaff(RepairFormationUnitPK equipmentPerRepairFormationUnit,
                                             Integer totalStaff,
                                             Integer availableStaff) {
        this.equipmentPerRepairFormationUnit = equipmentPerRepairFormationUnit;
        this.totalStaff = totalStaff;
        this.availableStaff = availableStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairFormationUnitEquipmentStaff that = (RepairFormationUnitEquipmentStaff) o;
        return Objects.equals(equipmentPerRepairFormationUnit, that.equipmentPerRepairFormationUnit) &&
                Objects.equals(repairFormationUnit, that.repairFormationUnit) &&
                Objects.equals(equipmentSubType, that.equipmentSubType) &&
                Objects.equals(totalStaff, that.totalStaff) &&
                Objects.equals(availableStaff, that.availableStaff) &&
                Objects.equals(tehoSession, that.tehoSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentPerRepairFormationUnit,
                            repairFormationUnit,
                            equipmentSubType,
                            totalStaff,
                            availableStaff,
                            tehoSession);
    }

    public RepairFormationUnitPK getEquipmentPerRepairFormationUnit() {
        return equipmentPerRepairFormationUnit;
    }

    public RepairFormationUnit getRepairFormationUnit() {
        return repairFormationUnit;
    }

    public void setRepairFormationUnit(RepairFormationUnit repairFormationUnit) {
        this.repairFormationUnit = repairFormationUnit;
    }

    public EquipmentSubType getEquipmentSubType() {
        return equipmentSubType;
    }

    public void setEquipmentSubType(EquipmentSubType equipmentSubType) {
        this.equipmentSubType = equipmentSubType;
    }

    public Integer getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    public Integer getAvailableStaff() {
        return availableStaff;
    }

    public void setAvailableStaff(int availableStaff) {
        this.availableStaff = availableStaff;
    }

    public TehoSession getTehoSession() {
        return tehoSession;
    }

    public RepairFormationUnitEquipmentStaff copy(UUID newSessionId) {
        return new RepairFormationUnitEquipmentStaff(
                getEquipmentPerRepairFormationUnit().copy(newSessionId),
                getTotalStaff(),
                getAvailableStaff());
    }
}
